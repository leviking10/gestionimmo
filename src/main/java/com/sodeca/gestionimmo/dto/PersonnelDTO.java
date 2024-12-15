package com.sodeca.gestionimmo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonnelDTO {
    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private String telephone;
    private String poste;
    private String departement;
    private String adresse;
    private String localite;
    private String service;
    private boolean actif=true;
}
