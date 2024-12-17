package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.TypeImmobilisation;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@DiscriminatorValue("MOBILIER")
public class Mobilier extends Immobilisation {
    private String typeMobilier; // Ex: Bureau, Chaise
    private String materiau; // Ex: Bois, Métal
}
