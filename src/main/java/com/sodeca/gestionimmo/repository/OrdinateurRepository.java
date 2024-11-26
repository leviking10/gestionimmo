package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Ordinateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdinateurRepository extends JpaRepository<Ordinateur, Long> {
    // Requêtes spécifiques à Ordinateur
}
