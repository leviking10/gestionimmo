package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Intervention;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Planification;
import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.enums.TypeIntervention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    /**
     * Trouver les interventions associées à une immobilisation.
     *
     * @param immobilisation L'immobilisation concernée.
     * @return Liste des interventions.
     */
    List<Intervention> findByImmobilisation(Immobilisation immobilisation);

    /**
     * Trouver les interventions associées à un technicien.
     *
     * @param technicien Le technicien concerné.
     * @return Liste des interventions.
     */
    List<Intervention> findByTechnicien(Personnel technicien);

    /**
     * Trouver les interventions en fonction de leur statut.
     *
     * @param statut Le statut des interventions (EN_ATTENTE, EN_COURS, TERMINEE).
     * @return Liste des interventions.
     */
    List<Intervention> findByStatut(StatutIntervention statut);

    /**
     * Trouver les interventions planifiées à une date spécifique.
     *
     * @param datePlanification La date de planification.
     * @return Liste des interventions.
     */
    List<Intervention> findByDatePlanification(LocalDate datePlanification);

    /**
     * Trouver les interventions planifiées dans une plage de dates.
     *
     * @param startDate La date de début.
     * @param endDate   La date de fin.
     * @return Liste des interventions.
     */
    List<Intervention> findByDatePlanificationBetween(LocalDate startDate, LocalDate endDate);

    void deleteByImmobilisationAndTypeAndStatut(Immobilisation immobilisation, TypeIntervention typeIntervention, StatutIntervention statutIntervention);

    List<Intervention> findByPlanification(Planification planification);
}
