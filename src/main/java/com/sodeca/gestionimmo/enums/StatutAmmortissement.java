package com.sodeca.gestionimmo.enums;

public enum StatutAmmortissement {
    A_CALCULER("A calculer"),
    EN_COURS("En cours"),
    AMMORTI("Ammorti"),
    ANNULE("Annulé");


    private String statut;

    StatutAmmortissement(String statut) {
        this.statut = statut;
    }

    public String getStatut() {
        return statut;
    }
}