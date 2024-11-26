package com.sodeca.gestionimmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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