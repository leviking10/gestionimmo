package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Categorie findByCategorie(String categorie);

}
