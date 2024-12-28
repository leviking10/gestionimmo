package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "immobilisations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED) // Permet de créer une table par classe fille
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class Immobilisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeImmo; // Code unique pour l'immobilisation

    @Column(nullable = false)
    private String designation;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @Column(nullable = false)
    private LocalDate dateAcquisition;

    @Column(nullable = false)
    private double valeurAcquisition;

    @Column
    private String localisation;

    @Lob
    private String qrCode; // Code QR pour l'immobilisation

    @Column
    private LocalDate dateMiseEnService;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCession statutCession = StatutCession.DISPONIBLE; // Statut par défaut

    @Column
    private LocalDate dateCession;

    @Column
    @Positive
    private Double valeurCession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAffectation statut = StatutAffectation.DISPONIBLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatImmobilisation etatImmo = EtatImmobilisation.EN_SERVICE;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private TypeAmortissement typeAmortissement;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    @Setter
    @Transient
    private TypeImmobilisation type;

    public TypeImmobilisation getType() {
        if (this.getClass().isAnnotationPresent(DiscriminatorValue.class)) {
            String discriminatorValue = this.getClass().getAnnotation(DiscriminatorValue.class).value();
            return TypeImmobilisation.valueOf(discriminatorValue.toUpperCase());
        }
        return null;
    }
}
