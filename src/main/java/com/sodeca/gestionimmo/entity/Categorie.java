package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories_immobilisations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categorie; // Nom de la catégorie (ex. "Mobilier", "Matériel informatique", etc.)

    @Column
    private String description; // Description de la catégorie

    @Column(nullable = false)
    private int dureeAmortissement; // Durée par défaut d'amortissement (en années)

    @Column
    private boolean actif; // Permet de désactiver une catégorie si elle n'est plus utilisée
}
