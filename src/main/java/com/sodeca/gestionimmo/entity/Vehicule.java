package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicules")
public class Vehicule extends Immobilisation {

    @Column(nullable = false)
    private String immatriculation;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;
    @Column
    private String kilometrage;
    @Column
    private LocalDate dateDerniereRevision;
}