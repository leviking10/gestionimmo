package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findByCategorie(String categorie);

}
