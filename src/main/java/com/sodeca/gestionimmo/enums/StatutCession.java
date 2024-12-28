package com.sodeca.gestionimmo.enums;

public enum StatutCession {
    DISPONIBLE("Disponible"),
    VENDU("Vendu"),
    SORTIE("sortie"),
    MISE_EN_REBUT("Mise en rebut"),;
    private final String description;

    StatutCession(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
