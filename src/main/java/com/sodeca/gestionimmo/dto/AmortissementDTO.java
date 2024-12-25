package com.sodeca.gestionimmo.dto;

import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmortissementDTO {
    private int id; // ID de l'amortissement
    private Long idImmobilisation; // ID de l'immobilisation associée
    private String methode; // Méthode d'amortissement (ex. Linéaire)
    private double montantAmorti; // Montant pour cette période
    private LocalDate dateDebutExercice; // Date de début de l'exercice
    private LocalDate dateCalcul; // Date du calcul
    private double valeurNette; // Valeur nette de l'immobilisation après amortissement
    private StatutAmmortissement statut; // Statut de l'amortissement (ex. "En cours", "ammorti", "annulé")
    private Double tauxDegressif;
    private double montantCumule;
    private Double coefficientDegressif;
    private Double tauxAnnuel;
    private Double prorata;

}