package com.sodeca.gestionimmo.dto;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InventaireDTO {
    private Long pieceId;
    private String reference;
    private String nom;
    private int stockDisponible;
    private int stockMinimum;
    private boolean estSousStock; // Indique si le stock est inférieur au stock minimum

    // Calcul automatique de estSousStock dans un constructeur personnalisé
    public InventaireDTO(Long pieceId, String reference, String nom, int stockDisponible, int stockMinimum) {
        this.pieceId = pieceId;
        this.reference = reference;
        this.nom = nom;
        this.stockDisponible = stockDisponible;
        this.stockMinimum = stockMinimum;
        this.estSousStock = stockDisponible < stockMinimum;
    }

    // Méthode pour mettre à jour estSousStock
    public void verifierStock() {
        this.estSousStock = stockDisponible < stockMinimum;
    }
}
