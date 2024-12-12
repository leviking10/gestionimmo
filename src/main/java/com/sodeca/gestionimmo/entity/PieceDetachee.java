package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pieces_detachees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PieceDetachee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private int stockDisponible;

    @Column(nullable = false)
    private int stockMinimum; // Stock minimum pour alertes

    @Column
    private String description;
}
