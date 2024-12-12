package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "consommation_carburant")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConsommationCarburant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double quantiteLitres;

    @Column(nullable = false)
    private double coutTotal;

    @Column
    private String stationService;

    @Column
    private double kilométrage; // Nouveau champ : pour enregistrer le kilométrage du véhicule
}
