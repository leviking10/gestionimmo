package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.TypeImmobilisation;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "telephones")
@DiscriminatorValue("TELEPHONE")
public class Telephone extends Immobilisation {
    private String marque;
    private String modele;
    private String imei;
    @Column(unique = true)
    private String numeroSerie;
}
