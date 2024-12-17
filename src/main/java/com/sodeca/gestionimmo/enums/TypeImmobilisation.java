package com.sodeca.gestionimmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TypeImmobilisation {
    ORDINATEUR,
    TELEPHONE,
    MOBILIER,
    MACHINE,
    VEHICULE;
@JsonCreator
public static TypeImmobilisation fromString(String value) {
    if (value == null || value.isEmpty()) {
        return null;
    }
    try {
        return TypeImmobilisation.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Type d'immobilisation inconnu : " + value);
    }
}
}