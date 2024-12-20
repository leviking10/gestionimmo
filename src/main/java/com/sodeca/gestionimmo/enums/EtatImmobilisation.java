package com.sodeca.gestionimmo.enums;

import lombok.Getter;

@Getter
public enum EtatImmobilisation {
    EN_SERVICE("En service"),
    EN_MAINTENANCE("En maintenance"),
    HORS_SERVICE ("Hors service"),
    PLANIFIE ("Planifié"),
    SIGNALE ("Signalé");
    private final String label;

    EtatImmobilisation(String label) {
        this.label = label;
    }

    public static EtatImmobilisation fromLabel(String label) {
        for (EtatImmobilisation etat : values()) {
            if (etat.getLabel().equalsIgnoreCase(label)) {
                return etat;
            }
        }
        throw new IllegalArgumentException("Etat d'immobilisation invalide : " + label);
    }
}
