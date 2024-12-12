package com.sodeca.gestionimmo.dto;
import com.sodeca.gestionimmo.enums.Priorite;
import com.sodeca.gestionimmo.enums.StatutPlanification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanificationDTO {

    private Long id;
    private String description;
    private Priorite priorite;
    private StatutPlanification statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long immobilisationId; // ID de l'immobilisation concernée
    private List<Long> technicienIds; // Liste des IDs des techniciens assignés
    private String rapport;
}