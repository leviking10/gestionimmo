package com.sodeca.gestionimmo.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.enums.TypeMouvement;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.MouvementStockMapper;
import com.sodeca.gestionimmo.repository.MouvementStockRepository;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ApprovisionnementImportService {

    private final PieceDetacheeRepository pieceRepository;
    private final MouvementStockRepository mouvementRepository;
    private final MouvementStockMapper mouvementMapper;
    private static final Logger logger = LoggerFactory.getLogger(ApprovisionnementImportService.class);

    public ApprovisionnementImportService(PieceDetacheeRepository pieceRepository,
                                          MouvementStockRepository mouvementRepository,
                                          MouvementStockMapper mouvementMapper) {
        this.pieceRepository = pieceRepository;
        this.mouvementRepository = mouvementRepository;
        this.mouvementMapper = mouvementMapper;
    }

    public List<MouvementStockDTO> importApprovisionnements(MultipartFile file) throws IOException {
        validateFile(file);

        String fileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return processApprovisionnementsFromExcel(file);
        } else if (fileName.endsWith(".csv")) {
            return processApprovisionnementsFromCsv(file);
        } else {
            throw new BusinessException("Format de fichier non supporté. Veuillez uploader un fichier Excel ou CSV.",
                    "UNSUPPORTED_FILE_FORMAT", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Le fichier est vide ou non fourni.",
                    "EMPTY_FILE", HttpStatus.BAD_REQUEST);
        }
        if (file.getOriginalFilename() == null) {
            throw new BusinessException("Le fichier doit avoir un nom valide.",
                    "INVALID_FILE_NAME", HttpStatus.BAD_REQUEST);
        }
    }

    private List<MouvementStockDTO> processApprovisionnementsFromExcel(MultipartFile file) throws IOException {
        List<MouvementStockDTO> mouvements = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            sheet.forEach(row -> {
                if (row.getRowNum() == 0) return; // Ignorer l'en-tête
                processExcelRow(row, mouvements);
            });
        } catch (IOException ex) {
            logger.error("Erreur de lecture du fichier Excel : {}", ex.getMessage(), ex);
            throw new BusinessException("Erreur de lecture du fichier Excel.",
                    "EXCEL_IO_ERROR", HttpStatus.BAD_REQUEST, ex);
        } catch (IllegalArgumentException ex) {
            logger.error("Erreur dans les données du fichier Excel : {}", ex.getMessage(), ex);
            throw new BusinessException("Erreur dans les données du fichier Excel.",
                    "EXCEL_DATA_ERROR", HttpStatus.BAD_REQUEST, ex);
        } catch (Exception ex) {
            logger.error("Erreur inattendue lors du traitement du fichier Excel : {}", ex.getMessage(), ex);
            throw new BusinessException("Erreur inattendue lors du traitement du fichier Excel.",
                    "EXCEL_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
        return mouvements;
    }

    private void processExcelRow(Row row, List<MouvementStockDTO> mouvements) {
        try {
            MouvementStockDTO mouvementDTO = mapExcelRowToMouvementDTO(row);
            mouvements.add(enregistrerMouvement(mouvementDTO));
        } catch (BusinessException ex) {
            logger.warn("Erreur métier lors du traitement de la ligne Excel {} : {}", row.getRowNum(), ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            logger.warn("Erreur de validation des données à la ligne Excel {} : {}", row.getRowNum(), ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.error("Erreur inattendue à la ligne Excel {} : {}", row.getRowNum(), ex.getMessage(), ex);
            throw new BusinessException("Erreur inattendue lors du traitement de la ligne Excel.",
                    "EXCEL_ROW_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }
    private List<MouvementStockDTO> processApprovisionnementsFromCsv(MultipartFile file) throws IOException {
        List<MouvementStockDTO> mouvements = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            boolean isHeader = true;

            while ((values = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Ignorer la première ligne d'en-tête
                    continue;
                }
                processCsvLine(values, mouvements);
            }
        } catch (CsvValidationException e) {
            throw new BusinessException("Erreur de validation dans le fichier CSV.",
                    "CSV_VALIDATION_ERROR", HttpStatus.BAD_REQUEST, e);
        } catch (IOException e) {
            throw new BusinessException("Erreur de lecture du fichier CSV.",
                    "CSV_IO_ERROR", HttpStatus.BAD_REQUEST, e);
        }
        return mouvements;
    }
    private void processCsvLine(String[] csvRow, List<MouvementStockDTO> stockMovements) {
        if (csvRow == null || csvRow.length == 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("Ligne CSV vide ou invalide rencontrée : {}", Arrays.toString(csvRow));
            }
            return;
        }
        try {
            MouvementStockDTO movementDTO = mapCsvRowToMouvementDTO(csvRow);
            stockMovements.add(enregistrerMouvement(movementDTO));
        } catch (BusinessException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Erreur métier lors du traitement d'une ligne CSV : {}", Arrays.toString(csvRow), ex);
            }
        } catch (RuntimeException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Erreur inattendue lors du traitement d'une ligne CSV : {}", Arrays.toString(csvRow), ex);
            }
            throw new BusinessException(
                    "Erreur inattendue lors du traitement du fichier CSV.",
                    "CSV_PROCESSING_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex
            );
        }
    }



    private MouvementStockDTO enregistrerMouvement(MouvementStockDTO dto) {
        PieceDetachee piece = pieceRepository.findByReference(dto.getReferencePiece())
                .orElseThrow(() -> new BusinessException("Pièce introuvable avec la référence : " + dto.getReferencePiece(),
                        "PIECE_NOT_FOUND", HttpStatus.BAD_REQUEST));

        piece.setStockDisponible(piece.getStockDisponible() + dto.getQuantite());
        pieceRepository.save(piece);

        MouvementStock mouvement = new MouvementStock();
        mouvement.setPiece(piece);
        mouvement.setQuantite(dto.getQuantite());
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setCommentaire(dto.getCommentaire());
        mouvement.setDateMouvement(LocalDateTime.now());

        MouvementStock savedMouvement = mouvementRepository.save(mouvement);
        return mouvementMapper.toDTO(savedMouvement);
    }

    private MouvementStockDTO mapExcelRowToMouvementDTO(Row row) {
        MouvementStockDTO dto = new MouvementStockDTO();
        dto.setReferencePiece(getCellValue(row.getCell(0)));
        dto.setQuantite(Integer.parseInt(getCellValue(row.getCell(1))));
        dto.setCommentaire(getCellValue(row.getCell(2)));
        return dto;
    }

    private MouvementStockDTO mapCsvRowToMouvementDTO(String[] values) {
        MouvementStockDTO dto = new MouvementStockDTO();
        dto.setReferencePiece(values[0]);
        dto.setQuantite(Integer.parseInt(values[1]));
        dto.setCommentaire(values[2]);
        return dto;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }
}
