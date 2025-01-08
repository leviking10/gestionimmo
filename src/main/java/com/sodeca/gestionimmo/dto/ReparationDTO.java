package com.sodeca.gestionimmo.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReparationDTO {
    private Long id;
    private Long vehiculeId;
    private LocalDate date;
    private String description;
    private double cout;
    private String fournisseur;
    private double kilometrage;
    private String typeReparation;
}
