package com.sodeca.gestionimmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum EtatImmobilisation {
    EN_SERVICE,
    EN_MAINTENANCE,
    HORS_SERVICE ,
    PLANIFIE ,
    SIGNALE;

    @JsonCreator
    public static EtatImmobilisation fromLabel(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return EtatImmobilisation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Etat d'immobilisation inconnu : " + value);
        }

    }
}
