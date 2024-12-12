package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reparation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double cout;

    @Column
    private String fournisseur; // Ex: Nom du garage ou du prestataire

    @Column
    private double kilometrage; // Nouveau champ : pour enregistrer le kilométrage lors de la réparation

    @Column
    private String typeReparation; // Ex: Moteur, Pneu, Entretien général, etc.
}
