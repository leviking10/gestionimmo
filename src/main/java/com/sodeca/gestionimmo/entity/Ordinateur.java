package com.sodeca.gestionimmo.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
    private String utilisateur;
    private LocalDate dateAffectation;
}
