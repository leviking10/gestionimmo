package com.sodeca.gestionimmo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class TelephoneDTO extends ImmobilisationDTO {
    private String type;
    private String marque;
    private String modele;
    private String imei;
    private String numeroSerie;
    private String etat;
    private String utilisateur;
    private LocalDate dateAffectation;

}