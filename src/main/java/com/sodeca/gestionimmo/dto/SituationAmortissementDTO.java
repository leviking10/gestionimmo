package com.sodeca.gestionimmo.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SituationAmortissementDTO {

    private List<AmortissementDTO> amortissements; // Liste des amortissements filtrés
    private double cumulAmortissements;           // Amortissement cumulé jusqu'à la date
    private double valeurNette;                   // Valeur nette comptable (V.N.C)
    private String statut;                        // Statut de l'immobilisation à la date (EN_COURS, AMORTI, SORTIE)
}