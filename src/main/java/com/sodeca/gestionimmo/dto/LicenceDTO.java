package com.sodeca.gestionimmo.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenceDTO {

    private Long id;
    private String nom; // Nom de la licence
    private String fournisseur; // Fournisseur ou éditeur
    private LocalDate dateExpiration; // Date d'expiration
    private int quantite; // Quantité disponible
    private double cout; // Coût total
    private String details; // Informations supplémentaires
}
