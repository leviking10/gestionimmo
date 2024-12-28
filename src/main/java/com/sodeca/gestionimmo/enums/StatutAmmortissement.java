package com.sodeca.gestionimmo.enums;

import lombok.Getter;
@Getter
public enum StatutAmmortissement {
    A_CALCULER("A calculer"),
    EN_COURS("En cours"),
    AMORTI("Amorti"),
    ANNULE("Annulé"),
    SORTIE("Sortie");

    private final String statut;

    StatutAmmortissement(String statut) {
        this.statut = statut;
    }
}