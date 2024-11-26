package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.StatutCession;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cessions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant de la cession

    @ManyToOne
    @JoinColumn(name = "immobilisation_id", nullable = false)
    private Immobilisation immobilisation; // Immobilisation associée

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCession statutCession; // Statut de la cession (VENDU ou REBUTE)

    @Column(nullable = false)
    private LocalDate dateCession; // Date de la cession ou du rebut

    @Column
    private Double valeurCession; // Valeur de la cession (ou valeur résiduelle si REBUTE)
}
