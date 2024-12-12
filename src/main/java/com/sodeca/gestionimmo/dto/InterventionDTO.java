package com.sodeca.gestionimmo.dto;

import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.enums.TypeIntervention;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterventionDTO {
    private Long id;
    private Long immobilisationId;
    private TypeIntervention type;
    private StatutIntervention statut;
    private LocalDate datePlanification;
    private LocalDate dateExecution;
    private Long technicienId;
    private String description;
    private String rapport;
}
