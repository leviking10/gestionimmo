package com.sodeca.gestionimmo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class OrdinateurDTO extends ImmobilisationDTO {
    private String type;
    private String marque;
    private String modele;
    private String processeur;
    private String ram; // Taille en Go
    private String disqueDur; // Taille en Go
    private String os;
    private String etat;
    private String utilisateur;
    private LocalDate dateAffectation;
}