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
     * -- GETTER --
     *  Récupère le libellé associé au type d'amortissement.
     *
     * @return le libellé en chaîne de caractères.
     */
    private final String label;

    TypeAmortissement(String label) {
        this.label = label;
    }

    /**
     * Méthode utilitaire pour obtenir un TypeAmortissement à partir d'une chaîne.
     *
     * @param label le libellé à rechercher.
     * @return le TypeAmortissement correspondant.
     * @throws IllegalArgumentException si le libellé ne correspond à aucun type.
     */
    public static @NotNull(message = "La methode d'amortissement est obligatoire") TypeAmortissement fromLabels(String label) {
        for (TypeAmortissement type : values()) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type d'amortissement invalide : " + label);
    }
}
