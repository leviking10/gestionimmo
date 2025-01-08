package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.CategorieDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.CategorieMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;
    private final CategorieMapper mapper; // Utilisation de CategorieMapper

    public CategorieServiceImpl(CategorieRepository categorieRepository, CategorieMapper mapper) {
        this.categorieRepository = categorieRepository;
        this.mapper = mapper;
    }

    @Override
    public CategorieDTO createCategorie(CategorieDTO categorieDTO) {
        // Convertir DTO en entité
        Categorie categorie = mapper.toCategorie(categorieDTO);

        // Sauvegarder dans la base de données
        Categorie savedCategorie = categorieRepository.save(categorie);

        // Retourner le DTO
        return mapper.toCategorieDTO(savedCategorie);
    }

    @Override
    public List<CategorieDTO> getAllCategories() {
        // Récupérer toutes les catégories et les convertir en DTOs
        return categorieRepository.findAll()
                .stream()
                .map(mapper::toCategorieDTO)
                .toList();
    }

    @Override
    public CategorieDTO getCategorieById(Long id) {
        // Récupérer une catégorie par ID
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Catégorie introuvable"));
        return mapper.toCategorieDTO(categorie);
    }

    @Override
    public List<CategorieDTO> createCategories(List<CategorieDTO> categorieDTOs) {
        // Convertir chaque DTO en entité
        List<Categorie> categories = categorieDTOs.stream()
                .map(mapper::toCategorie)
                .toList();

        // Sauvegarder toutes les entités en une seule transaction
        List<Categorie> savedCategories = categorieRepository.saveAll(categories);

        // Convertir les entités sauvegardées en DTO et les retourner
        return savedCategories.stream()
                .map(mapper::toCategorieDTO)
                .toList();
    }

    @Override
    public CategorieDTO updateCategorie(Long id, CategorieDTO categorieDTO) {
        // Récupérer l'entité existante
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'ID : " + id));

        // Mettre à jour les champs via mapper (optionnel)
        mapper.updateCategorieFromDto(categorieDTO, categorie);

        // Sauvegarder les modifications
        Categorie updatedCategorie = categorieRepository.save(categorie);

        return mapper.toCategorieDTO(updatedCategorie);
    }

    @Override
    public void deleteCategorie(Long id) {
        // Vérifier si la catégorie existe
        if (!categorieRepository.existsById(id)) {
            throw new BusinessException("Catégorie introuvable avec l'ID : " + id);
        }

        // Supprimer la catégorie
        categorieRepository.deleteById(id);
    }
}
