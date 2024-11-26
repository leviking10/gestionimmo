package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "ammortissements")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Amortissement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "id_immobilisation", nullable = false)
    private Immobilisation immobilisation;
    private String methode; // Méthode d'amortissement (ex. Linéaire)
    @Column
    private double montantAmorti; // Montant pour cette période
    @Column
    private LocalDate dateDebutExercice; // Nouvelle propriété pour la gestion du prorata
    @Column
    private LocalDate dateCalcul; // Date du calcul
    @Column(nullable = false)
    private int dureeRestante; // Durée restante en années ou mois
    @Column
    private double valeurNette; // Valeur nette de l'immobilisation
    @Enumerated(EnumType.STRING) // Spécifier le type d'énumération
    private StatutAmmortissement statut; // Statut de l'amortissement (ex. En cours, ammorti, annulé)
}