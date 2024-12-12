package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.DemandePiece;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.enums.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandePieceRepository extends JpaRepository<DemandePiece, Long> {

    /**
     * Trouve toutes les demandes associées à un technicien donné.
     *
     * @param technicien l'entité du technicien
     * @return une liste des demandes associées
     */
    List<DemandePiece> findByTechnicien(Personnel technicien);

    /**
     * Trouve toutes les demandes associées à une pièce donnée.
     *
     * @param piece l'entité de la pièce
     * @return une liste des demandes associées
     */
    List<DemandePiece> findByPiece(PieceDetachee piece);

    /**
     * Trouve toutes les demandes ayant un statut spécifique.
     *
     * @param statut le statut de la demande
     * @return une liste des demandes ayant ce statut
     */
    List<DemandePiece> findByStatut(StatutDemande statut);

    /**
     * Trouve toutes les demandes non validées.
     *
     * @param validee état de validation
     * @return une liste des demandes non validées
     */
    List<DemandePiece> findByValidee(boolean validee);
}
