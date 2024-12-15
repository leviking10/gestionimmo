package com.sodeca.gestionimmo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "ordinateurs")
public class Ordinateur extends Immobilisation {
    private String type;
    private String marque;
    private String modele;
    private String processeur;
    private String ram;
    private String disqueDur;
    private String os;
    private String etat;
    @Column(unique = true, nullable = false) // Rend le champ unique et non null
    private String numeroSerie;
}
