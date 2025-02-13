package com.sodeca.gestionimmo.dto;

import com.sodeca.gestionimmo.enums.StatutDemande;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandePieceDTO {
    private Long id;
    private Long pieceId;
    private Long technicienId;
    private int quantiteDemandee;
    private String commentaire;
    private StatutDemande statut; // Ajout pour le statut de la demande
    private LocalDateTime dateDemande;
    private LocalDateTime dateValidation;
    private LocalDateTime dateAnnulation;
    private LocalDateTime dateLivraison;
}
