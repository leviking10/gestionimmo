package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Reparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReparationRepository extends JpaRepository<Reparation, Long> {
    List<Reparation> findByVehiculeIdOrderByDateDesc(Long vehiculeId); // Historique par date
}
