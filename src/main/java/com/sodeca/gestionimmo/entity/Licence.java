package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "licences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Licence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom; // Nom de la licence (ex. : Office 365, Adobe Suite)

    @Column(nullable = false)
    private String fournisseur; // Fournisseur ou éditeur de la licence

    @Column(nullable = false)
    private LocalDate dateExpiration; // Date d’expiration actuelle

    @Column(nullable = false)
    private int quantite; // Quantité de licences disponibles

    @Column(nullable = false)
    private double cout; // Coût unitaire ou total de la licence

    @Lob
    private String details; // Détails supplémentaires (ex. : fonctionnalités, restrictions)

    @CreationTimestamp
    private LocalDate createdDate; // Date de création de l’enregistrement

    @UpdateTimestamp
    private LocalDate lastModifiedDate; // Dernière modification de l’enregistrement
}