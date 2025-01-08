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
        String fileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return processApprovisionnementsFromExcel(file);
        } else if (fileName.endsWith(".csv")) {
            return processApprovisionnementsFromCsv(file);
        } else {
            throw new BusinessException("Format de fichier non supporté. Veuillez uploader un fichier Excel ou CSV.");
        }
    }

    private List<MouvementStockDTO> processApprovisionnementsFromExcel(MultipartFile file) throws IOException {
        List<MouvementStockDTO> mouvements = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            try {
                MouvementStockDTO mouvementDTO = mapExcelRowToMouvementDTO(row);
                mouvements.add(enregistrerMouvement(mouvementDTO));
            } catch (Exception e) {
                throw new BusinessException("Erreur ligne " + row.getRowNum() + ": " + e.getMessage());
            }
        }
        workbook.close();
        return mouvements;
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
                try {
                    // Mappage de la ligne CSV en MouvementStockDTO
                    MouvementStockDTO mouvementDTO = mapCsvRowToMouvementDTO(values);

                    // Enregistrement du mouvement
                    mouvements.add(enregistrerMouvement(mouvementDTO));
                } catch (BusinessException e) {
                    // Journaux pour capturer les erreurs métier spécifiques
                    logger.warn("Erreur lors du traitement d'une ligne CSV : {}", Arrays.toString(values), e);
                } catch (Exception e) {
                    // Journaux pour capturer les erreurs inattendues
                    logger.error("Erreur inattendue lors du traitement d'une ligne CSV : {}", Arrays.toString(values), e);
                    throw new BusinessException("Erreur inattendue lors du traitement du fichier CSV.",
                            "CSV_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, e);
                }
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

    private MouvementStockDTO enregistrerMouvement(MouvementStockDTO dto) {
        PieceDetachee piece = pieceRepository.findByReference(dto.getReferencePiece())
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec la référence : " + dto.getReferencePiece()));

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
