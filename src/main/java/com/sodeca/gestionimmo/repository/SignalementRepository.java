package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Signalement;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Immobilisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignalementRepository extends JpaRepository<Signalement, Long> {

    /**
     * Trouver tous les signalements faits par un membre du personnel donné.
     *
     * @param personnel Le membre du personnel.
     * @return Liste des signalements.
     */
    List<Signalement> findByPersonnel(Personnel personnel);

    /**
     * Trouver tous les signalements associés à une immobilisation donnée.
     *
     * @param immobilisation L'immobilisation concernée.
     * @return Liste des signalements.
     */
    List<Signalement> findByImmobilisation(Immobilisation immobilisation);

    /**
     * Trouver tous les signalements récents pour un équipement spécifique.
     *
     * @param immobilisation L'immobilisation concernée.
     * @return Liste des signalements récents.
     */
    List<Signalement> findByImmobilisationOrderByDateSignalementDesc(Immobilisation immobilisation);
}
