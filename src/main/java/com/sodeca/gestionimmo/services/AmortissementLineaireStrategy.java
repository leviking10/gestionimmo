package com.sodeca.gestionimmo.services;
import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AmortissementLineaireStrategy implements AmortissementStrategy {

    @Override
    public List<Amortissement> calculerAmortissements(Immobilisation immobilisation, Optional<Amortissement> dernierAmortissement) {
        List<Amortissement> amortissements = new ArrayList<>();

        double valeurComptable = dernierAmortissement.map(Amortissement::getValeurNette)
                .orElse(immobilisation.getValeurAcquisition());
        int dureeAmortissement = immobilisation.getCategorie().getDureeAmortissement();
        int dureeRestante = dureeAmortissement - dernierAmortissement
                .map(a -> a.getDateCalcul().getYear() - immobilisation.getDateAcquisition().getYear())
                .orElse(0);

        if (dureeRestante <= 0) {
            throw new RuntimeException("Durée restante invalide pour l'immobilisation.");
        }

        double montantAnnuel = valeurComptable / dureeAmortissement;
        LocalDate dateDebutExercice = dernierAmortissement
                .map(Amortissement::getDateCalcul)
                .orElse(LocalDate.of(immobilisation.getDateAcquisition().getYear(), 1, 1));

        for (int i = 0; i < dureeRestante; i++) {
            double montantAmorti = (i == 0 && isProrataApplicable(immobilisation, dateDebutExercice))
                    ? prorataCalcul(immobilisation, montantAnnuel)
                    : montantAnnuel;

            valeurComptable -= montantAmorti;

            amortissements.add(Amortissement.builder()
                    .immobilisation(immobilisation)
                    .methode("Linéaire")
                    .montantAmorti(round(montantAmorti, 2))
                    .dateDebutExercice(dateDebutExercice.plusYears(i))
                    .dateCalcul(dateDebutExercice.plusYears(i + 1).minusDays(1))
                    .valeurNette(round(Math.max(valeurComptable, 0.0), 2))
                    .statut(i == dureeRestante - 1 ? StatutAmmortissement.AMMORTI : StatutAmmortissement.EN_COURS)
                    .build());
        }

        return amortissements;
    }

    private boolean isProrataApplicable(Immobilisation immobilisation, LocalDate dateDebutExercice) {
        return !dateDebutExercice.isEqual(immobilisation.getDateAcquisition());
    }

    private double prorataCalcul(Immobilisation immobilisation, double montantAnnuel) {
        int joursRestants = 365 - immobilisation.getDateAcquisition().getDayOfYear() + 1;
        return (montantAnnuel * joursRestants) / 365;
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
