package com.sodeca.gestionimmo.dto;

import com.sodeca.gestionimmo.entity.Categorie;
import lombok.AllArgsConstructor;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategorieDTO {
    private Long id;
    private String categorie; // Nom de la catégorie (ex. "Mobilier", "Matériel informatique", etc.)
    private String description; // Description de la catégorie
    private int dureeAmortissement; // Durée par défaut d'amortissement (en années)
    private boolean actif; // Statut actif/inactif de la catégorie
}