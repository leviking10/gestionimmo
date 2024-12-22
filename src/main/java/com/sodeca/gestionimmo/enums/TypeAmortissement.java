package com.sodeca.gestionimmo.enums;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * Enumération représentant les types d'amortissement disponibles.
 */
@Getter
public enum TypeAmortissement {

    LINEAIRE("Linéaire"),      // Amortissement linéaire
    DEGRESSIF("Dégressif");    // Amortissement dégressif

    /**
     * Libellé associé au type d'amortissement.
     */
    private final String label;

    /**
     * Constructeur de l'enum.
     *
     * @param label Libellé associé au type d'amortissement.
     */
    TypeAmortissement(String label) {
        this.label = label;
    }

    /**
     * Méthode utilitaire pour obtenir un TypeAmortissement à partir d'une chaîne (label ou nom de constante).
     *
     * @param value La valeur à rechercher (label ou nom de constante).
     * @return Le TypeAmortissement correspondant.
     * @throws IllegalArgumentException si la valeur ne correspond à aucun type.
     */
    public static @NotNull(message = "La méthode d'amortissement est obligatoire") TypeAmortissement fromLabelOrName(String value) {
        for (TypeAmortissement type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.getLabel().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type d'amortissement invalide : " + value);
    }
}
