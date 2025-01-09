package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class AmortissementDegressifStrategy implements AmortissementStrategy {

    private static final Logger logger = LoggerFactory.getLogger(AmortissementDegressifStrategy.class);
    private static final String METHODE_DEGRESSIF = "Dégressif";
    private static final String METHODE_LINEAIRE = "Linéaire";
    @Override
    public List<Amortissement> calculerAmortissements(Immobilisation immobilisation, Optional<Amortissement> dernierAmortissement) {
        List<Amortissement> amortissements = new ArrayList<>();
        double valeurComptable = dernierAmortissement.map(Amortissement::getValeurNette)
                .orElse(immobilisation.getValeurAcquisition());
        int dureeAmortissement = immobilisation.getCategorie().getDureeAmortissement();
        int dureeRestante = calculateDureeRestante(immobilisation, dernierAmortissement);

        validateDureeRestante(dureeRestante);

        double coefficient = getCoefficientDegressif(dureeAmortissement);
        double tauxDegressif = (100.0 / dureeAmortissement) * coefficient;

        LocalDate dateDebutExercice = dernierAmortissement
                .map(Amortissement::getDateCalcul)
                .orElse(immobilisation.getDateMiseEnService().withDayOfYear(1));

        double montantCumule = dernierAmortissement.map(Amortissement::getMontantCumule).orElse(0.0);
        boolean utiliserLineaire = false;

        for (int i = 0; i < dureeRestante; i++) {
            double montantAmortiDegressif = valeurComptable * (tauxDegressif / 100);
            double montantAmortiLineaire = valeurComptable / (dureeRestante - i);

            // Transition au linéaire si applicable
            if (montantAmortiLineaire > montantAmortiDegressif) {
                utiliserLineaire = true;
                montantAmortiDegressif = montantAmortiLineaire;
            }

            // Gestion du prorata pour la première année
            if (i == 0 && isProrataApplicable(immobilisation, dateDebutExercice)) {
                double montantProrata = prorataCalculDegressif(immobilisation, montantAmortiDegressif);
                valeurComptable -= montantProrata;
                montantCumule += montantProrata;

                logger.info("Prorata appliqué : Montant prorata = {}, Cumul = {}", montantProrata, montantCumule);

                amortissements.add(Amortissement.builder()
                        .immobilisation(immobilisation)
                        .methode(METHODE_DEGRESSIF)
                        .montantAmorti(montantProrata)
                        .montantCumule(montantCumule)
                        .coefficientDegressif(coefficient)
                        .tauxDegressif(tauxDegressif)
                        .dateDebutExercice(dateDebutExercice)
                        .dateCalcul(LocalDate.of(dateDebutExercice.getYear(), 12, 31))
                        .valeurNette(Math.max(valeurComptable, 0.0))
                        .statut(StatutAmmortissement.EN_COURS)
                        .build());
            }

            // Année sans prorata ou après le prorata
            double montantAmorti = Math.min(montantAmortiDegressif, valeurComptable);
            valeurComptable -= montantAmorti;
            montantCumule += montantAmorti;

            logger.info("Année {} : Méthode = {}, Montant amorti = {}, Cumul = {}", i + 1, utiliserLineaire ? METHODE_LINEAIRE : METHODE_DEGRESSIF, montantAmorti, montantCumule);

            amortissements.add(Amortissement.builder()
                    .immobilisation(immobilisation)
                    .methode(utiliserLineaire ? METHODE_LINEAIRE : METHODE_DEGRESSIF)
                    .montantAmorti(montantAmorti)
                    .montantCumule(montantCumule)
                    .coefficientDegressif(coefficient)
                    .tauxDegressif(utiliserLineaire ? null : tauxDegressif)
                    .dateDebutExercice(dateDebutExercice)
                    .dateCalcul(LocalDate.of(dateDebutExercice.getYear() + 1, 12, 31)) // Toujours 31 décembre
                    .valeurNette(Math.max(valeurComptable, 0.0))
                    .statut(i == dureeRestante - 1 ? StatutAmmortissement.AMORTI : StatutAmmortissement.EN_COURS)
                    .build());

            // Mise à jour de la date de début d'exercice pour l'année suivante
            dateDebutExercice = dateDebutExercice.plusYears(1).withDayOfYear(1);
        }

        validateAmortissementTotal(immobilisation, montantCumule);
        return amortissements;
    }

    private double prorataCalculDegressif(Immobilisation immobilisation, double montantAnnuel) {
        int moisRestants = 12 - immobilisation.getDateAcquisition().getMonthValue() + 1;
        if (montantAnnuel <= 0) {
            throw new IllegalArgumentException("Mois restants ou montant annuel invalide pour le calcul du prorata.");
        }
        return (montantAnnuel * moisRestants) / 12;
    }

    private boolean isProrataApplicable(Immobilisation immobilisation, LocalDate dateDebutExercice) {
        return !dateDebutExercice.isEqual(immobilisation.getDateAcquisition());
    }

    private void validateDureeRestante(int dureeRestante) {
        if (dureeRestante <= 0) {
            throw new IllegalArgumentException("Durée restante invalide : aucune période d'amortissement restante.");
        }
    }

    private double getCoefficientDegressif(int duree) {
        if (duree <= 4) return 1.5;
        if (duree <= 6) return 2.0;
        return 2.5;
    }

    private int calculateDureeRestante(Immobilisation immobilisation, Optional<Amortissement> dernierAmortissement) {
        int dureeAmortissement = immobilisation.getCategorie().getDureeAmortissement();
        return dureeAmortissement - dernierAmortissement
                .map(a -> a.getDateCalcul().getYear() - immobilisation.getDateAcquisition().getYear())
                .orElse(0);
    }

    private void validateAmortissementTotal(Immobilisation immobilisation, double montantCumule) {
        if (Math.abs(montantCumule - immobilisation.getValeurAcquisition()) > 0.01) {
            throw new IllegalStateException("Le total des amortissements ne correspond pas à la valeur d'acquisition.");
        }
    }
}
