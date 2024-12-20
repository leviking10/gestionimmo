package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.DemandePieceDTO;
import com.sodeca.gestionimmo.entity.DemandePiece;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.enums.StatutDemande;
import com.sodeca.gestionimmo.mapper.DemandePieceMapper;
import com.sodeca.gestionimmo.repository.DemandePieceRepository;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandePieceServiceImpl implements DemandePieceService {

    private final DemandePieceRepository demandeRepository;
    private final PieceDetacheeRepository pieceRepository;
    private final PersonnelRepository personnelRepository;
    private final DemandePieceMapper mapper;

    public DemandePieceServiceImpl(DemandePieceRepository demandeRepository,
                                   PieceDetacheeRepository pieceRepository,
                                   PersonnelRepository personnelRepository,
                                   DemandePieceMapper mapper) {
        this.demandeRepository = demandeRepository;
        this.pieceRepository = pieceRepository;
        this.personnelRepository = personnelRepository;
        this.mapper = mapper;
    }

    @Override
    public DemandePieceDTO createDemande(DemandePieceDTO dto) {
        // Vérification de la pièce demandée
        PieceDetachee piece = pieceRepository.findById(dto.getPieceId())
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + dto.getPieceId()));

        // Vérification du technicien demandeur
        Personnel technicien = personnelRepository.findById(dto.getTechnicienId())
                .orElseThrow(() -> new RuntimeException("Technicien introuvable avec l'ID : " + dto.getTechnicienId()));

        // Création de la demande
        DemandePiece demande = mapper.toEntity(dto);
        demande.setPiece(piece);
        demande.setTechnicien(technicien);
        demande.setDateDemande(LocalDateTime.now());
        demande.setStatut(StatutDemande.EN_ATTENTE);

        return mapper.toDTO(demandeRepository.save(demande));
    }

    @Override
    public DemandePieceDTO validerDemande(Long id) {
        DemandePiece demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable avec l'ID : " + id));

        // Vérification du statut
        if (demande.getStatut() == StatutDemande.APPROUVEE) {
            throw new RuntimeException("La demande est déjà validée.");
        } else if (demande.getStatut() == StatutDemande.ANNULEE) {
            throw new RuntimeException("Impossible de valider une demande annulée.");
        }

        // Vérification du stock
        PieceDetachee piece = demande.getPiece();
        if (piece.getStockDisponible() < demande.getQuantiteDemandee()) {
            throw new RuntimeException("Stock insuffisant pour valider la demande.");
        }

        // Mise à jour du stock
        piece.setStockDisponible(piece.getStockDisponible() - demande.getQuantiteDemandee());
        pieceRepository.save(piece);

        // Mise à jour du statut de la demande
        demande.setStatut(StatutDemande.APPROUVEE);
        demande.setDateValidation(LocalDateTime.now());
        demandeRepository.save(demande);

        return mapper.toDTO(demande);
    }

    @Override
    public DemandePieceDTO annulerDemande(Long id) {
        DemandePiece demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable avec l'ID : " + id));

        // Vérification du statut
        if (demande.getStatut() == StatutDemande.APPROUVEE) {
            throw new RuntimeException("Impossible d'annuler une demande déjà validée.");
        } else if (demande.getStatut() == StatutDemande.ANNULEE) {
            throw new RuntimeException("Cette demande est déjà annulée.");
        }

        // Annulation de la demande
        demande.setStatut(StatutDemande.ANNULEE);
        demande.setDateAnnulation(LocalDateTime.now());

        return mapper.toDTO(demandeRepository.save(demande));
    }

    @Override
    public List<DemandePieceDTO> getAllDemandes() {
        return demandeRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<DemandePieceDTO> getDemandesByTechnicien(Long technicienId) {
        Personnel technicien = personnelRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien introuvable avec l'ID : " + technicienId));
        return demandeRepository.findByTechnicien(technicien).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<DemandePieceDTO> getDemandesByPiece(Long pieceId) {
        PieceDetachee piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + pieceId));
        return demandeRepository.findByPiece(piece).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<DemandePieceDTO> getDemandesNonValidees() {
        return demandeRepository.findByStatut(StatutDemande.EN_ATTENTE).stream()
                .map(mapper::toDTO)
                .toList();
    }
}
