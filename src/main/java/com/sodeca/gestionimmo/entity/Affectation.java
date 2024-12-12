package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "affectations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "immobilisation_id", nullable = false)
    private Immobilisation immobilisation; // Peut être un Ordinateur, Véhicule, etc.

    @ManyToOne
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    @Column(nullable = false)
    private LocalDate dateAffectation;

    @Column
    private LocalDate dateRetour; // Date de retour ou fin d’affectation (si applicable)
}

