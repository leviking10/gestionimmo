package com.sodeca.gestionimmo.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Mobilier extends Immobilisation {
    private String typeMobilier; // Ex: Bureau, Chaise
    private String materiau; // Ex: Bois, Métal
}
