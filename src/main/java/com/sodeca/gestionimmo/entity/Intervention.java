package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.enums.TypeIntervention;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "interventions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "immobilisation_id", nullable = false)
    private Immobilisation immobilisation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeIntervention type; // PREVENTIVE ou CORRECTIVE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutIntervention statut; // EN_ATTENTE, EN_COURS, TERMINEE

    @Column(nullable = false)
    private LocalDate datePlanification; // Date prévue pour l'intervention

    @Column
    private LocalDate dateExecution; // Date réelle d'exécution

    @ManyToOne
    @JoinColumn(name = "technicien_id", nullable = true)
    private Personnel technicien; // Technicien responsable

    @Lob
    private String description; // Détails sur l'intervention

    @Lob
    private String rapport; // Rapport final après l'intervention

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planification_id", nullable = true)
    private Planification planification;

}
