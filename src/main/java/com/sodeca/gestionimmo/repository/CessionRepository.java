package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Cession;
import com.sodeca.gestionimmo.enums.StatutCession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CessionRepository extends JpaRepository<Cession, Long> {

    // Récupérer toutes les cessions pour une immobilisation spécifique
    List<Cession> findByImmobilisationId(Long immobilisationId);

    // Récupérer les cessions par statut
    List<Cession> findByStatutCession(StatutCession statut);

    // Récupérer les cessions dans une plage de dates
    List<Cession> findByDateCessionBetween(LocalDate startDate, LocalDate endDate);

    // Compter les cessions par statut
    Long countByStatutCession(StatutCession statut);
}
