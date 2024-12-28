package com.sodeca.gestionimmo.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum StatutAffectation {
    DISPONIBLE("Disponible"),
    AFFECTEE("Affectée");

    private final String label;

    StatutAffectation(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    public static StatutAffectation fromLabelOrName(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value) || e.getLabel().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Statut invalide : " + value));
    }
}
