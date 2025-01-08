package com.sodeca.gestionimmo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter
@Setter
public class VehiculeDTO extends ImmobilisationDTO {
    private String immatriculation;
    private String marque;
    private String modele;
    private String kilometrage;
    private LocalDate dateDerniereRevision;
}
