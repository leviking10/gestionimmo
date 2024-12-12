package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "signalements")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel; // Personnel qui a fait le signalement

    @ManyToOne
    @JoinColumn(name = "immobilisation_id", nullable = false)
    private Immobilisation immobilisation; // Immobilisation concernée

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateSignalement = LocalDateTime.now();
}
