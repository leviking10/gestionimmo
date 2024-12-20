package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Reparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReparationRepository extends JpaRepository<Reparation, Long> {
    List<Reparation> findByVehiculeIdOrderByDateDesc(Long vehiculeId); // Historique par date
    List<Reparation> findByTypeReparation(String typeReparation);
    List<Reparation> findByFournisseur(String fournisseur);
    List<Reparation> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
