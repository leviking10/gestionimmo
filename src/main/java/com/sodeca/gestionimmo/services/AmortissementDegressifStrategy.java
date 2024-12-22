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
                .orElse(LocalDate.of(immobilisation.getDateAcquisition().getYear(), 1, 1));

        double montantCumule = dernierAmortissement.map(Amortissement::getMontantCumule).orElse(0.0);
        double totalAmortissementCalcule = 0.0;

        for (int i = 0; i < dureeRestante; i++) {
            double montantAmorti = valeurComptable * (tauxDegressif / 100);

            // Calcul du prorata pour la première année
            if (i == 0 && isProrataApplicable(immobilisation, dateDebutExercice)) {
                montantAmorti = prorataCalculDegressif(immobilisation, montantAmorti);
            }

            // Ajustement pour la dernière année
            if (i == dureeRestante - 1) {
                montantAmorti = immobilisation.getValeurAcquisition() - totalAmortissementCalcule; // Ajustement final
            }

            valeurComptable -= montantAmorti;
            montantCumule += montantAmorti;
            totalAmortissementCalcule += montantAmorti;

            logger.info("Calcul dégressif - Année {} : Montant amorti={}, Valeur nette={}, Montant cumulé={}, Total Calculé={}",
                    i + 1, montantAmorti, valeurComptable, montantCumule, totalAmortissementCalcule);

            amortissements.add(Amortissement.builder()
                    .immobilisation(immobilisation)
                    .methode("Dégressif")
                    .montantAmorti(montantAmorti)
                    .coefficientDegressif(coefficient)
                    .tauxDegressif(tauxDegressif)
                    .montantCumule(montantCumule)
                    .dateDebutExercice(dateDebutExercice.plusYears(i))
                    .dateCalcul(dateDebutExercice.plusYears(i + 1).minusDays(1))
                    .valeurNette(Math.max(valeurComptable, 0.0))
                    .statut(i == dureeRestante - 1 ? StatutAmmortissement.AMMORTI : StatutAmmortissement.EN_COURS)
                    .build());
        }

        // Validation finale pour s'assurer que la somme des montants cumulés est correcte
        validateAmortissementTotal(immobilisation, montantCumule);

        return amortissements;
    }

    private double prorataCalculDegressif(Immobilisation immobilisation, double montantAnnuel) {
        int moisRestants = 12 - immobilisation.getDateAcquisition().getMonthValue() + 1;
        if (moisRestants <= 0 || montantAnnuel <= 0) {
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
