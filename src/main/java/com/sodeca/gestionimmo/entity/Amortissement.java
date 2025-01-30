package com.sodeca.gestionimmo.entity;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Table(name = "amortissements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amortissement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_immobilisation", nullable = false)
    private Immobilisation immobilisation;
    @Column(nullable = false)
    private String methode; // Méthode d'amortissement : "Linéaire" ou "Dégressif"
    @Column(nullable = false)
    private double montantAmorti; // Montant amorti pour la période
    @Column
    private Double coefficientDegressif; // Coefficient spécifique pour le mode dégressif
    @Column(nullable = false)
    private LocalDate dateDebutExercice; // Début de l'exercice comptable (gestion au prorata)
    @Column(nullable = false)
    private LocalDate dateCalcul; // Date exacte du calcul d'amortissement
    @Column(nullable = false)
    private double valeurNette; // Valeur nette après amortissement
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAmmortissement statut; // Ex: EN_COURS, AMORTI, ANNULE
    @Column
    private Double tauxDegressif; // Taux applicable pour l'amortissement dégressif
    @Column(nullable = false)
    private double montantCumule; // Montant cumulé des amortissements
    @Column
    private Double tauxAnnuel; // Taux annuel pour le calcul linéaire ou dégressif
    @Column
    private Double prorata; // Prorata pour le premier exercice (nullable si non applicable)

}