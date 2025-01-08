package com.sodeca.gestionimmo.utils;

/**
 * Classe utilitaire pour les constantes globales.
 */
public class Constants {

    // URL pour la génération des QR Codes
    public static final String QR_CODE_URL_PREFIX = "https://gestionimmo.sodeca.com/immobilisation/";

    // Format par défaut pour les dates
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    // Messages d'erreur standard
    public static final String ERROR_CATEGORY_NOT_FOUND = "Catégorie introuvable : ";
    public static final String ERROR_UNSUPPORTED_FILE_FORMAT = "Format de fichier non supporté : ";
    public static final String ERROR_IMPORT_VALIDATION = "Des erreurs sont survenues lors de l'importation. Consultez les logs.";
    public static final String ERROR_CSV_VALIDATION = "Erreur de validation dans le fichier CSV.";
}
