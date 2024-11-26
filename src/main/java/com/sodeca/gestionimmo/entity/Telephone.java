package com.sodeca.gestionimmo.entity;

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
@Table(name = "telephones")
public class Telephone extends Immobilisation {
    private String type;
    private String marque;
    private String modele;
    private String imei;
    private String numeroSerie;
    private String etat;
    private String utilisateur;
    private LocalDate dateAffectation;
}
