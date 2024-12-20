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
public class AmortissementDegressifStrategy implements AmortissementStrategy {

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

        double coefficient = getCoefficientDegressif(dureeAmortissement);
        double tauxDegressif = (100.0 / dureeAmortissement) * coefficient;
        LocalDate dateDebutExercice = dernierAmortissement
                .map(Amortissement::getDateCalcul)
                .orElse(LocalDate.of(immobilisation.getDateAcquisition().getYear(), 1, 1));

        for (int i = 0; i < dureeRestante; i++) {
            double montantAmorti = valeurComptable * (tauxDegressif / 100);
            valeurComptable -= montantAmorti;

            amortissements.add(Amortissement.builder()
                    .immobilisation(immobilisation)
                    .methode("Dégressif")
                    .montantAmorti(round(montantAmorti, 2))
                    .coefficientDegressif(coefficient)
                    .dateDebutExercice(dateDebutExercice.plusYears(i))
                    .dateCalcul(dateDebutExercice.plusYears(i + 1).minusDays(1))
                    .valeurNette(round(Math.max(valeurComptable, 0.0), 2))
                    .statut(i == dureeRestante - 1 ? StatutAmmortissement.AMMORTI : StatutAmmortissement.EN_COURS)
                    .build());
        }

        return amortissements;
    }

    private double getCoefficientDegressif(int duree) {
        if (duree <= 4) return 1.5;
        if (duree <= 6) return 2.0;
        return 2.5;
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}