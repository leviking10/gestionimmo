package com.sodeca.gestionimmo.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsommationCarburantDTO {
    private Long id;
    private Long vehiculeId;
    private LocalDate date;
    private double quantiteLitres;
    private double coutTotal;
    private String stationService;
    private double kilometrage; // Nouveau champ
}
