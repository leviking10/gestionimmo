package com.sodeca.gestionimmo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AffectationDTO {
    private Long id;
    private Long immobilisationId; // Peut être un ordinateur, un téléphone, etc.
    private Long personnelId;
    private LocalDate dateAffectation;
    private LocalDate dateRetour;
}
