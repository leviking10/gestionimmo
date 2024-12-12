package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Mobilier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobilierRepository extends JpaRepository<Mobilier, Long> {
    // Ajoutez des requêtes personnalisées si nécessaire
}
