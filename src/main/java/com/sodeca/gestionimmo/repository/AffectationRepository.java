package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {

    /**
     * Trouver toutes les affectations pour un personnel spécifique.
     *
     * @param personnelId ID du personnel.
     * @return Liste des affectations associées au personnel.
     */
    List<Affectation> findByPersonnelId(Long personnelId);

    /**
     * Trouver toutes les affectations pour une immobilisation spécifique.
     *
     * @param immobilisationId ID de l'immobilisation.
     * @return Liste des affectations associées à l'immobilisation.
     */
    List<Affectation> findByImmobilisationId(Long immobilisationId);
}
