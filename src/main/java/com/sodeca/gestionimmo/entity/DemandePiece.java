package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.StatutDemande;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandePiece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private PieceDetachee piece;
    @ManyToOne
    private Personnel technicien;
    private int quantiteDemandee;
    private String commentaire;
    private LocalDateTime dateDemande;
    @Enumerated(EnumType.STRING)
    private StatutDemande statut;
    private LocalDateTime dateValidation;
    private LocalDateTime dateAnnulation;
    private LocalDateTime dateLivraison;
}
