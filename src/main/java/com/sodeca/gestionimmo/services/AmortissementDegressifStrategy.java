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

        double tauxDegressif = 2.0 / dureeAmortissement; // Exemple de coefficient dégressif
        LocalDate dateDebutExercice = dernierAmortissement
                .map(Amortissement::getDateCalcul)
                .orElse(LocalDate.of(immobilisation.getDateAcquisition().getYear(), 1, 1));

        for (int i = 0; i < dureeRestante; i++) {
            double montantAmorti = valeurComptable * tauxDegressif;
            valeurComptable -= montantAmorti;

            Amortissement amortissement = new Amortissement();
            amortissement.setImmobilisation(immobilisation);
            amortissement.setMethode("Dégressif");
            amortissement.setMontantAmorti(round(montantAmorti, 2));
            amortissement.setDateDebutExercice(dateDebutExercice.plusYears(i));
            amortissement.setDateCalcul(dateDebutExercice.plusYears(i + 1).minusDays(1));
            amortissement.setValeurNette(round(Math.max(valeurComptable, 0.0), 2));
            amortissement.setStatut(i == dureeRestante - 1 ? StatutAmmortissement.AMMORTI : StatutAmmortissement.EN_COURS);

            amortissements.add(amortissement);
        }

        return amortissements;
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
