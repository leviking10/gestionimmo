package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Licence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenceRepository extends JpaRepository<Licence, Long> {

    // Recherche par nom de licence
    List<Licence> findByNomContainingIgnoreCase(String nom);

    // Recherche des licences expirées
    List<Licence> findByDateExpirationBefore(java.time.LocalDate date);

    // Recherche par fournisseur
    List<Licence> findByFournisseurContainingIgnoreCase(String fournisseur);
}
