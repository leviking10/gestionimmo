package com.sodeca.gestionimmo.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.enums.TypeMouvement;
import com.sodeca.gestionimmo.mapper.MouvementStockMapper;
import com.sodeca.gestionimmo.repository.MouvementStockRepository;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MouvementStockServiceImpl implements MouvementStockService {

    private final MouvementStockRepository mouvementRepository;
    private final PieceDetacheeRepository pieceRepository;
    private final MouvementStockMapper mouvementMapper;

    public MouvementStockServiceImpl(MouvementStockRepository mouvementRepository,
                                     PieceDetacheeRepository pieceRepository,
                                     MouvementStockMapper mouvementMapper) {
        this.mouvementRepository = mouvementRepository;
        this.pieceRepository = pieceRepository;
        this.mouvementMapper = mouvementMapper;
    }

    @Override
    public MouvementStockDTO enregistrerMouvement(MouvementStockDTO dto) {
        if (dto.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive.");
        }

        PieceDetachee piece = getPieceByDto(dto);

        // Mise à jour du stock
        if (dto.getTypeMouvement() == TypeMouvement.ENTREE) {
            piece.setStockDisponible(piece.getStockDisponible() + dto.getQuantite());
        } else if (dto.getTypeMouvement() == TypeMouvement.SORTIE) {
            if (piece.getStockDisponible() < dto.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour effectuer la sortie.");
            }
            piece.setStockDisponible(piece.getStockDisponible() - dto.getQuantite());
        } else {
            throw new IllegalArgumentException("Type de mouvement non reconnu.");
        }

        pieceRepository.save(piece);

        MouvementStock mouvement = mouvementMapper.toEntity(dto);
        mouvement.setPiece(piece);
        mouvement.setDateMouvement(dto.getDateMouvement() != null ? dto.getDateMouvement() : LocalDateTime.now());

        return mouvementMapper.toDTO(mouvementRepository.save(mouvement));
    }

    @Override
    public List<MouvementStockDTO> getAllMouvements() {
        return mouvementRepository.findAll().stream()
                .map(mouvementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MouvementStockDTO> getMouvementsByPiece(Long pieceId) {
        PieceDetachee piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + pieceId));
        return mouvementRepository.findByPiece(piece).stream()
                .map(mouvementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MouvementStockDTO> getMouvementsByReference(String reference) {
        PieceDetachee piece = pieceRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec la référence : " + reference));
        return mouvementRepository.findByPiece(piece).stream()
                .map(mouvementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MouvementStockDTO> importStockMovements(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().toLowerCase();

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return processStockMovementsFromExcel(file);
        } else if (fileName.endsWith(".csv")) {
            return processStockMovementsFromCsv(file);
        } else {
            throw new RuntimeException("Format de fichier non supporté. Veuillez uploader un fichier Excel ou CSV.");
        }
    }

    private List<MouvementStockDTO> processStockMovementsFromExcel(MultipartFile file) throws IOException {
        List<MouvementStockDTO> mouvements = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            try {
                MouvementStockDTO mouvement = mapExcelRowToMouvementDTO(row);
                mouvements.add(enregistrerMouvement(mouvement));
            } catch (Exception e) {
                System.err.println("Erreur à la ligne " + row.getRowNum() + ": " + e.getMessage());
            }
        }
        workbook.close();
        return mouvements;
    }

    private List<MouvementStockDTO> processStockMovementsFromCsv(MultipartFile file) throws IOException {
        List<MouvementStockDTO> mouvements = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            boolean isHeader = true;

            while ((values = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                try {
                    MouvementStockDTO mouvement = mapCsvRowToMouvementDTO(values);
                    mouvements.add(enregistrerMouvement(mouvement));
                } catch (Exception e) {
                    System.err.println("Erreur avec les données : " + e.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException("Erreur de validation dans le fichier CSV.", e);
        }
        return mouvements;
    }

    private MouvementStockDTO mapExcelRowToMouvementDTO(Row row) {
        MouvementStockDTO dto = new MouvementStockDTO();
        dto.setReferencePiece(getCellValue(row.getCell(0)));
        dto.setTypeMouvement(TypeMouvement.valueOf(getCellValue(row.getCell(1)).toUpperCase()));
        dto.setQuantite(Integer.parseInt(getCellValue(row.getCell(2))));
        dto.setCommentaire(getCellValue(row.getCell(3)));
        dto.setDateMouvement(LocalDateTime.now()); // Default to now if date not provided
        return dto;
    }

    private MouvementStockDTO mapCsvRowToMouvementDTO(String[] values) {
        MouvementStockDTO dto = new MouvementStockDTO();
        dto.setReferencePiece(values[0]);
        dto.setTypeMouvement(TypeMouvement.valueOf(values[1].toUpperCase()));
        dto.setQuantite(Integer.parseInt(values[2]));
        dto.setCommentaire(values[3]);
        dto.setDateMouvement(LocalDateTime.now());
        return dto;
    }

    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private PieceDetachee getPieceByDto(MouvementStockDTO dto) {
        if (dto.getReferencePiece() != null) {
            return pieceRepository.findByReference(dto.getReferencePiece())
                    .orElseThrow(() -> new RuntimeException("Pièce introuvable avec la référence : " + dto.getReferencePiece()));
        } else if (dto.getPieceId() != null) {
            return pieceRepository.findById(dto.getPieceId())
                    .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + dto.getPieceId()));
        } else {
            throw new IllegalArgumentException("Ni référence ni ID de la pièce n'a été fourni.");
        }
    }
}
