package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.DotationDTO;
import com.sodeca.gestionimmo.entity.Dotation;
import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.enums.TypeMouvement;
import com.sodeca.gestionimmo.repository.DotationRepository;
import com.sodeca.gestionimmo.repository.MouvementStockRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DotationServiceImpl implements DotationService {

    private final DotationRepository dotationRepository;
    private final PieceDetacheeRepository pieceRepository;
    private final PersonnelRepository personnelRepository;
    private final MouvementStockRepository mouvementRepository;

    public DotationServiceImpl(
            DotationRepository dotationRepository,
            PieceDetacheeRepository pieceRepository,
            PersonnelRepository personnelRepository,
            MouvementStockRepository mouvementRepository
    ) {
        this.dotationRepository = dotationRepository;
        this.pieceRepository = pieceRepository;
        this.personnelRepository = personnelRepository;
        this.mouvementRepository = mouvementRepository;
    }

    @Override
    public List<DotationDTO> getAllDotations() {
        return dotationRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public DotationDTO createDotation(DotationDTO dto) {
        // Récupération et validation de la pièce
        PieceDetachee piece = pieceRepository.findById(dto.getPieceId())
                .orElseThrow(() -> new RuntimeException("Pièce détachée introuvable avec l'ID : " + dto.getPieceId()));

        // Récupération et validation du technicien
        Personnel technicien = personnelRepository.findById(dto.getTechnicienId())
                .orElseThrow(() -> new RuntimeException("Technicien introuvable avec l'ID : " + dto.getTechnicienId()));

        // Vérification du stock disponible
        if (piece.getStockDisponible() < dto.getQuantite()) {
            throw new RuntimeException("Stock insuffisant pour la dotation demandée.");
        }

        // Mise à jour du stock disponible
        piece.setStockDisponible(piece.getStockDisponible() - dto.getQuantite());
        pieceRepository.save(piece);

        // Création de la dotation
        Dotation dotation = new Dotation();
        dotation.setPiece(piece);
        dotation.setTechnicien(technicien);
        dotation.setQuantite(dto.getQuantite());
        dotation.setDateDotation(LocalDateTime.now());
        Dotation savedDotation = dotationRepository.save(dotation);

        // Enregistrement du mouvement de stock
        MouvementStock mouvement = new MouvementStock();
        mouvement.setPiece(piece);
        mouvement.setQuantite(dto.getQuantite());
        mouvement.setTypeMouvement(TypeMouvement.SORTIE);
        mouvement.setCommentaire("Dotation effectuée pour le technicien : " + technicien.getNom());
        mouvement.setDateMouvement(LocalDateTime.now());
        mouvementRepository.save(mouvement);

        // Retourne le DTO correspondant
        return mapToDTO(savedDotation);
    }

    @Override
    public List<DotationDTO> getDotationsByTechnicien(Long technicienId) {
        Personnel technicien = personnelRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien introuvable avec l'ID : " + technicienId));
        return dotationRepository.findByTechnicien(technicien).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<DotationDTO> getDotationsByPiece(Long pieceId) {
        PieceDetachee piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + pieceId));
        return dotationRepository.findByPiece(piece).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<DotationDTO> getDotationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return dotationRepository.findByDateDotationBetween(startDate.atStartOfDay(), endDate.atTime(23, 59)).stream()
                .map(this::mapToDTO)
                .toList();
    }

    private DotationDTO mapToDTO(Dotation dotation) {
        return new DotationDTO(
                dotation.getId(),
                dotation.getTechnicien().getId(),
                dotation.getPiece().getId(),
                dotation.getQuantite(),
                dotation.getDateDotation()
        );
    }
}
