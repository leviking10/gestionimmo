package com.sodeca.gestionimmo.services;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PieceDetacheeServiceImpl implements PieceDetacheeService {

    private final PieceDetacheeRepository pieceRepository;
    private final MouvementStockRepository mouvementRepository;
    private final MouvementStockMapper mouvementMapper;
    private final PieceDetacheeMapper pieceMapper;
    private final DemandePieceRepository demandeRepository;
    private final PieceImportService pieceImportService;
    private final ApprovisionnementImportService approImportService;

    public PieceDetacheeServiceImpl(PieceDetacheeRepository pieceRepository,
                                    MouvementStockRepository mouvementRepository,
                                    MouvementStockMapper mouvementMapper,
                                    PieceDetacheeMapper pieceMapper,
                                    DemandePieceRepository demandeRepository,
                                    PieceImportService pieceImportService,
                                    ApprovisionnementImportService approImportService) {
        this.pieceRepository = pieceRepository;
        this.mouvementRepository = mouvementRepository;
        this.mouvementMapper = mouvementMapper;
        this.pieceMapper = pieceMapper;
        this.demandeRepository = demandeRepository;
        this.pieceImportService = pieceImportService;
        this.approImportService = approImportService;
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
                .toList();
    }

    @Override
    public List<PieceDetacheeDTO> importPiecesFromFile(MultipartFile file) throws IOException {
        return pieceImportService.importPieces(file);
    }

    @Override
    public List<MouvementStockDTO> importApprovisionnements(MultipartFile file) throws IOException {
        return approImportService.importApprovisionnements(file);
    }

    @Override
    public MouvementStockDTO approvisionnement(Long pieceId, int quantite, String commentaire) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
        }

        PieceDetachee piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + pieceId));

        piece.setStockDisponible(piece.getStockDisponible() + quantite);
        pieceRepository.save(piece);

        MouvementStock mouvement = new MouvementStock();
        mouvement.setPiece(piece);
        mouvement.setQuantite(quantite);
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setCommentaire(commentaire != null ? commentaire : "Approvisionnement");
        mouvement.setDateMouvement(LocalDateTime.now());

        MouvementStock savedMouvement = mouvementRepository.save(mouvement);
        return mouvementMapper.toDTO(savedMouvement);
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

        piece.setStockDisponible(piece.getStockDisponible() - demande.getQuantiteDemandee());
        pieceRepository.save(piece);

        MouvementStock mouvement = new MouvementStock();
        mouvement.setPiece(piece);
        mouvement.setQuantite(demande.getQuantiteDemandee());
        mouvement.setTypeMouvement(TypeMouvement.SORTIE);
        mouvement.setCommentaire("Validation de la demande par le technicien: " + demande.getTechnicien().getNom());
        mouvement.setDateMouvement(LocalDateTime.now());
        mouvementRepository.save(mouvement);

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
}
