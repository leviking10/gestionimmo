package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.DemandePieceDTO;

import java.util.List;

public interface DemandePieceService {
    /**
     * Crée une nouvelle demande de pièce.
     *
     * @param dto les informations de la demande
     * @return la demande créée
     */
    DemandePieceDTO createDemande(DemandePieceDTO dto);

    /**
     * Valide une demande existante.
     *
     * @param id l'identifiant de la demande
     * @return la demande validée
     */
    DemandePieceDTO validerDemande(Long id);

    /**
     * Annule une demande existante.
     *
     * @param id l'identifiant de la demande
     * @return la demande annulée
     */
    DemandePieceDTO annulerDemande(Long id);

    /**
     * Récupère toutes les demandes de pièces.
     *
     * @return une liste de toutes les demandes
     */
    List<DemandePieceDTO> getAllDemandes();

    /**
     * Récupère les demandes de pièces faites par un technicien spécifique.
     *
     * @param technicienId l'identifiant du technicien
     * @return une liste des demandes associées au technicien
     */
    List<DemandePieceDTO> getDemandesByTechnicien(Long technicienId);

    /**
     * Récupère les demandes de pièces associées à une pièce spécifique.
     *
     * @param pieceId l'identifiant de la pièce
     * @return une liste des demandes associées à la pièce
     */
    List<DemandePieceDTO> getDemandesByPiece(Long pieceId);

    /**
     * Récupère toutes les demandes qui n'ont pas encore été validées.
     *
     * @return une liste des demandes en attente de validation
     */
    List<DemandePieceDTO> getDemandesNonValidees();
}
