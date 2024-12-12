package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Planification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanificationRepository extends JpaRepository<Planification, Long> {
    // Récupérer les planifications pour une immobilisation donnée
    List<Planification> findByImmobilisation(Immobilisation immobilisation);

    // Récupérer les planifications pour un technicien donné
    List<Planification> findByTechniciensContaining(Personnel technicien);
}
