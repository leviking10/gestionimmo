package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    // Requêtes spécifiques à Vehicule
}
