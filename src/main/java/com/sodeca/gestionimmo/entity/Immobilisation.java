package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutCession;
import com.sodeca.gestionimmo.enums.StatutAffectation;
import com.sodeca.gestionimmo.enums.TypeImmobilisation;
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
    @Column(nullable = false, unique = true) // Unicité du code d'immobilisation
    private String codeImmo; // Code unique pour l'immobilisation

    @Column(nullable = false)
    private String designation;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false) // Association à la catégorie
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

    // Champs spécifiques pour la gestion des cessions et rébus
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCession statutCession = StatutCession.DISPONIBLE; // Statut par défaut

    @Column
    private LocalDate dateCession; // Date de cession ou de rebut

    @Column
    @Positive
    private Double valeurCession; // Valeur à la cession ou valeur résiduelle en cas de rebut
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAffectation statut = StatutAffectation.DISPONIBLE; // Par défaut DISPONIBLE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatImmobilisation etatImmo = EtatImmobilisation.EN_SERVICE; // Statut par défaut
    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
    // Champ "type" basé sur la colonne discriminante
    @Transient
    private TypeImmobilisation type;

    // Getter pour "type"
    public TypeImmobilisation getType() {
        if (this.getClass().isAnnotationPresent(DiscriminatorValue.class)) {
            String discriminatorValue = this.getClass().getAnnotation(DiscriminatorValue.class).value();
            return TypeImmobilisation.valueOf(discriminatorValue.toUpperCase());
        }
        return null;
    }

    // Setter pour "type"
    public void setType(TypeImmobilisation type) {
        this.type = type;
    }

}
