package com.sodeca.gestionimmo.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImmobilisationDTO {
    private Long id;

    @NotBlank(message = "La désignation est obligatoire")
    private String designation;
    @NotBlank(message = "La catégorie est obligatoire")
    private Long categorieId;// Identifiant de la catégorie
    private LocalDate dateAcquisition;
    @Positive(message = "La valeur d'acquisition doit être positive")
    private double valeurAcquisition;
    private String localisation;
    private String qrCode;
    private LocalDate dateMiseEnService;
    private int dureeAmortissement;
    private String statut;// En service, en réparation, en rebut, etc.
}