package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.CategorieDTO;

import java.util.List;

public interface CategorieService {

    // Créer une nouvelle catégorie
    CategorieDTO createCategorie(CategorieDTO categorieDTO);

    // Récupérer toutes les catégories
    List<CategorieDTO> getAllCategories();

    // Récupérer une catégorie par ID
    CategorieDTO getCategorieById(Long id);

    // Mettre à jour une catégorie
    CategorieDTO updateCategorie(Long id, CategorieDTO categorieDTO);

    // Supprimer une catégorie
    void deleteCategorie(Long id);
    // Ajouter plusieurs catégories
    List<CategorieDTO> createCategories(List<CategorieDTO> categorieDTOs);
}
