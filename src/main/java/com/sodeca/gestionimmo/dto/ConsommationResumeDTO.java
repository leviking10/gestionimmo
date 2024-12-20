package com.sodeca.gestionimmo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsommationResumeDTO {
    private Long vehiculeId;
    private double totalConsommationLitres;
    private double consommationMoyenne;
}
