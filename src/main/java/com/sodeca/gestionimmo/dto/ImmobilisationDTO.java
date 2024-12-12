package com.sodeca.gestionimmo.dto;

import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutAffectation;
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
    private StatutAffectation statutAffectation=StatutAffectation.DISPONIBLE;
    private EtatImmobilisation etatImmobilisation=EtatImmobilisation.FONCTIONNEL;
}