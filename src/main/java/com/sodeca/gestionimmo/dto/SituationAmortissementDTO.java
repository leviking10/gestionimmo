package com.sodeca.gestionimmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SituationAmortissementDTO {

    private List<AmortissementDTO> amortissements; // Liste des amortissements
    private double cumulAmortissements;           // Cumul des montants amortis
}
