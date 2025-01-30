package com.sodeca.gestionimmo.dto;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutAffectation;
import com.sodeca.gestionimmo.enums.TypeAmortissement;
import com.sodeca.gestionimmo.enums.TypeImmobilisation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ImmobilisationDTO {
    private Long id;
    private String codeImmo;
    @NotBlank(message = "La désignation est obligatoire")
    private String designation;
    @NotBlank(message = "La catégorie est obligatoire")
    private String categorieDesignation;
    private LocalDate dateAcquisition;
    @Positive(message = "La valeur d'acquisition doit être positive")
    private Double valeurAcquisition;
    private String localisation;
    private String qrCode;
    private LocalDate dateMiseEnService;
    private int dureeAmortissement;
    private EtatImmobilisation etatImmo;
    private StatutAffectation affectation;
    @NotNull(message = "Le type d'immobilisation est obligatoire")
    private TypeImmobilisation type;
    private TypeAmortissement typeAmortissement ;
}