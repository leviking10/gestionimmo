package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.TypeImmobilisation;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ordinateurs")
@DiscriminatorValue("ORDINATEUR")
public class Ordinateur extends Immobilisation {
    private String marque;
    private String modele;
    private String processeur;
    private String ram;
    private String disqueDur;
    private String os;
    private String etat;
    @Column(unique = true) // Rend le champ unique et non null
    private String numeroSerie;
}
