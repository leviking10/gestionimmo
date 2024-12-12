package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PieceDetachee piece;

    @ManyToOne(optional = false)
    private Personnel technicien;

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private LocalDateTime dateDotation;
}
