package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.enums.TypeMouvement;
import com.sodeca.gestionimmo.mapper.MouvementStockMapper;
import com.sodeca.gestionimmo.repository.MouvementStockRepository;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public List<MouvementStockDTO> getAllMouvements() {
        return mouvementRepository.findAll().stream()
                .map(mouvementMapper::toDTO)
                .toList();
    }
    @Override
    public List<MouvementStockDTO> searchMouvements(TypeMouvement type, LocalDateTime startDate, LocalDateTime endDate) {
        return mouvementRepository.findByTypeMouvementAndDateMouvementBetween(type, startDate, endDate).stream()
                .map(mouvementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MouvementStockDTO createMouvement(MouvementStockDTO dto) {
        // Vérifier si la pièce existe
        PieceDetachee piece = pieceRepository.findByReference(dto.getReferencePiece())
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec la référence : " + dto.getReferencePiece()));

        // Mettre à jour le stock en fonction du type de mouvement
        int nouvelleQuantite = piece.getStockDisponible();
        if (dto.getTypeMouvement() == TypeMouvement.ENTREE) {
            nouvelleQuantite += dto.getQuantite();
        } else if (dto.getTypeMouvement() == TypeMouvement.SORTIE) {
            if (piece.getStockDisponible() < dto.getQuantite()) {
                throw new RuntimeException("Quantité insuffisante en stock pour la sortie.");
            }
            nouvelleQuantite -= dto.getQuantite();
        }
        piece.setStockDisponible(nouvelleQuantite);
        pieceRepository.save(piece);

        // Créer et enregistrer le mouvement de stock
        MouvementStock mouvement = new MouvementStock();
        mouvement.setPiece(piece);
        mouvement.setQuantite(dto.getQuantite());
        mouvement.setTypeMouvement(dto.getTypeMouvement());
        mouvement.setCommentaire(dto.getCommentaire());
        mouvement.setDateMouvement(dto.getDateMouvement());

        MouvementStock savedMouvement = mouvementRepository.save(mouvement);

        // Retourner le mouvement au format DTO
        return mouvementMapper.toDTO(savedMouvement);
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
}
