package com.sodeca.gestionimmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
public enum EtatImmobilisation {
    EN_SERVICE("En service"),
    EN_MAINTENANCE("En maintenance"),
    HORS_SERVICE("Hors service"),
    PLANIFIE("Planifié"),
    SIGNALE("Signalé");

    private final String label;

    // Constructeur
    EtatImmobilisation(String label) {
        this.label = label;
    }

    // Méthode pour récupérer le libellé
    @JsonValue
    public String getLabel() {
        return label;
    }

    // Méthode statique pour créer une instance à partir d'une chaîne
    @JsonCreator
    public static EtatImmobilisation fromLabelOrName(String value) {
        for (EtatImmobilisation etat : values()) {
            if (etat.name().equalsIgnoreCase(value) || etat.getLabel().equalsIgnoreCase(value)) {
                return etat;
            }
        }
        throw new IllegalArgumentException("Type d'amortissement invalide : " + value);
    }
}
