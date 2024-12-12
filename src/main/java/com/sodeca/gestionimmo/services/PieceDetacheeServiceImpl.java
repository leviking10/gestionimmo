package com.sodeca.gestionimmo.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sodeca.gestionimmo.dto.InventaireDTO;
import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.dto.PieceDetacheeDTO;
import com.sodeca.gestionimmo.entity.DemandePiece;
import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.enums.StatutDemande;
import com.sodeca.gestionimmo.enums.TypeMouvement;
import com.sodeca.gestionimmo.mapper.MouvementStockMapper;
import com.sodeca.gestionimmo.mapper.PieceDetacheeMapper;
import com.sodeca.gestionimmo.repository.DemandePieceRepository;
import com.sodeca.gestionimmo.repository.MouvementStockRepository;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PieceDetacheeServiceImpl implements PieceDetacheeService {

    private final PieceDetacheeRepository pieceRepository;
    private final MouvementStockRepository mouvementRepository;
    private final MouvementStockMapper mouvementMapper;
    private final PieceDetacheeMapper pieceMapper;

    private final DemandePieceRepository demandeRepository;

    public PieceDetacheeServiceImpl(PieceDetacheeRepository pieceRepository,
                                    MouvementStockRepository mouvementRepository,
                                    MouvementStockMapper mouvementMapper,
                                    PieceDetacheeMapper pieceMapper, DemandePieceRepository demandeRepository) {
        this.pieceRepository = pieceRepository;
        this.mouvementRepository = mouvementRepository;
        this.mouvementMapper = mouvementMapper;
        this.pieceMapper = pieceMapper;
        this.demandeRepository = demandeRepository;
    }

    @Override
    public PieceDetacheeDTO createPiece(PieceDetacheeDTO dto) {
        PieceDetachee piece = pieceMapper.toEntity(dto);
        PieceDetachee savedPiece = pieceRepository.save(piece);
        return pieceMapper.toDTO(savedPiece);
    }

    @Override
    public PieceDetacheeDTO updatePiece(Long id, PieceDetacheeDTO dto) {
        PieceDetachee piece = pieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + id));
        piece.setNom(dto.getNom());
        piece.setReference(dto.getReference());
        piece.setDescription(dto.getDescription());
        piece.setStockDisponible(dto.getStockDisponible());
        PieceDetachee updatedPiece = pieceRepository.save(piece);
        return pieceMapper.toDTO(updatedPiece);
    }

    @Override
    public void deletePiece(Long id) {
        if (!pieceRepository.existsById(id)) {
            throw new RuntimeException("Pièce introuvable avec l'ID : " + id);
        }
        pieceRepository.deleteById(id);
    }

    @Override
    public PieceDetacheeDTO getPieceById(Long id) {
        PieceDetachee piece = pieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + id));
        return pieceMapper.toDTO(piece);
    }

    @Override
    public List<PieceDetacheeDTO> getAllPieces() {
        return pieceRepository.findAll().stream()
                .map(pieceMapper::toDTO)
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
    public List<PieceDetacheeDTO> importPiecesFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().toLowerCase();

        if (isExcelFile(fileName)) {
            return processExcelFile(file);
        } else if (isCsvFile(fileName)) {
            return processCsvFile(file);
        } else {
            throw new RuntimeException("Format de fichier non supporté. Veuillez uploader un fichier Excel ou CSV.");
        }
    }

    private boolean isExcelFile(String fileName) {
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }

    private boolean isCsvFile(String fileName) {
        return fileName.endsWith(".csv");
    }

    private List<PieceDetacheeDTO> processExcelFile(MultipartFile file) throws IOException {
        List<PieceDetacheeDTO> pieces = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Ignorer la ligne d'en-tête
            try {
                PieceDetacheeDTO pieceDTO = mapExcelRowToPieceDTO(row);
                PieceDetachee pieceEntity = pieceMapper.toEntity(pieceDTO);
                PieceDetachee savedEntity = pieceRepository.save(pieceEntity);
                pieces.add(pieceMapper.toDTO(savedEntity));
            } catch (Exception e) {
                System.err.println("Erreur lors de l'importation de la ligne " + row.getRowNum() + ": " + e.getMessage());
            }
        }
        workbook.close();
        return pieces;
    }

    private PieceDetacheeDTO mapExcelRowToPieceDTO(Row row) {
        PieceDetacheeDTO dto = new PieceDetacheeDTO();
        dto.setReference(getCellValue(row.getCell(0)));
        dto.setNom(getCellValue(row.getCell(1)));
        dto.setStockDisponible(Integer.parseInt(getCellValue(row.getCell(2))));
        dto.setStockMinimum(Integer.parseInt(getCellValue(row.getCell(3))));
        dto.setDescription(getCellValue(row.getCell(4)));
        return dto;
    }

    private List<PieceDetacheeDTO> processCsvFile(MultipartFile file) throws IOException {
        List<PieceDetacheeDTO> pieces = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            boolean isHeader = true;

            while ((values = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Ignorer la ligne d'en-tête
                    continue;
                }
                PieceDetacheeDTO piece = mapCsvRowToPieceDTO(values);
                pieces.add(piece);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException("Erreur de validation dans le fichier CSV.", e);
        }
        return pieces;
    }

    @Override
    public MouvementStockDTO validerDemande(Long demandeId) {
        DemandePiece demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable avec l'ID : " + demandeId));

        if (demande.getStatut() == StatutDemande.APPROUVEE || demande.getStatut() == StatutDemande.ANNULEE) {
            throw new RuntimeException("La demande ne peut pas être validée car elle est déjà traitée ou annulée.");
        }

        PieceDetachee piece = demande.getPiece();
        if (piece.getStockDisponible() < demande.getQuantiteDemandee()) {
            throw new RuntimeException("Stock insuffisant pour valider la demande.");
        }

        // Mise à jour du stock
        piece.setStockDisponible(piece.getStockDisponible() - demande.getQuantiteDemandee());
        pieceRepository.save(piece);

        // Enregistrement du mouvement
        MouvementStock mouvement = new MouvementStock();
        mouvement.setPiece(piece);
        mouvement.setQuantite(demande.getQuantiteDemandee());
        mouvement.setTypeMouvement(TypeMouvement.SORTIE);
        mouvement.setCommentaire("Validation de la demande par le technicien: " + demande.getTechnicien().getNom());
        mouvement.setDateMouvement(LocalDateTime.now());
        mouvementRepository.save(mouvement);

        // Mise à jour de l'état de la demande
        demande.setStatut(StatutDemande.APPROUVEE);
        demandeRepository.save(demande);

        return mouvementMapper.toDTO(mouvement);
    }
    @Override
    public List<InventaireDTO> getInventaire() {
        return pieceRepository.findAll().stream()
                .map(piece -> new InventaireDTO(
                        piece.getId(),
                        piece.getReference(),
                        piece.getNom(),
                        piece.getStockDisponible(),
                        piece.getStockMinimum(),
                        piece.getStockDisponible() < piece.getStockMinimum()
                ))
                .toList();
    }
    private PieceDetacheeDTO mapCsvRowToPieceDTO(String[] values) {
        PieceDetacheeDTO dto = new PieceDetacheeDTO();
        dto.setReference(values[0]);
        dto.setNom(values[1]);
        dto.setStockDisponible(Integer.parseInt(values[2]));
        dto.setStockMinimum(Integer.parseInt(values[3]));
        dto.setDescription(values[4]);
        return dto;
    }

    private String getCellValue(Cell cell) {
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
}
