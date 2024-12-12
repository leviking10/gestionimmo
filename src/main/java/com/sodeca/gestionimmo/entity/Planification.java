package com.sodeca.gestionimmo.entity;
import com.sodeca.gestionimmo.enums.Priorite;
import com.sodeca.gestionimmo.enums.StatutPlanification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "planifications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Planification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite; // Basse, Moyenne, Haute

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPlanification statut; // PLANIFIEE, EN_COURS, TERMINEE

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "immobilisation_id", nullable = false)
    private Immobilisation immobilisation;
    @Lob
    private String rapport;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "planification_techniciens",
            joinColumns = @JoinColumn(name = "planification_id"),
            inverseJoinColumns = @JoinColumn(name = "technicien_id")
    )
    private List<Personnel> techniciens;
}
