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
@DiscriminatorValue("VEHICULE")
public class Vehicule extends Immobilisation {
    @Column
    private String immatriculation;
    @Column
    private String marque;

    @Column
    private String modele;
    @Column
    private String kilometrage;
    @Column
    private LocalDate dateDerniereRevision;
}