package com.sodeca.gestionimmo.enums;

public enum StatutCession {
    DISPONIBLE("Disponible"),
    VENDU("Vendu"),
    REBUTE("Rébuté");
    private final String description;

    StatutCession(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
