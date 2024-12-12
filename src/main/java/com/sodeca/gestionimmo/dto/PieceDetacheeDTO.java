package com.sodeca.gestionimmo.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PieceDetacheeDTO {
    private Long id;
    private String reference;
    private String nom;
    private int stockDisponible;
    private int stockMinimum;
    private String description;
}
