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
public class AmortissementLineaireStrategy implements AmortissementStrategy {

    private static final Logger logger = LoggerFactory.getLogger(AmortissementLineaireStrategy.class);

    @Override
    public List<Amortissement> calculerAmortissements(Immobilisation immobilisation, Optional<Amortissement> dernierAmortissement) {
        List<Amortissement> amortissements = new ArrayList<>();

        double valeurAcquisition = immobilisation.getValeurAcquisition();
        int dureeAmortissement = immobilisation.getCategorie().getDureeAmortissement();
        int dureeRestante = calculateDureeRestante(immobilisation, dernierAmortissement);

        validateDureeRestante(dureeRestante);

        LocalDate dateDebutExercice = dernierAmortissement
                .map(Amortissement::getDateCalcul)
                .orElse(LocalDate.of(immobilisation.getDateAcquisition().getYear(), 1, 1));

        // Calcul du montant amorti annuel (constant après la première année)
        double montantAnnuel = valeurAcquisition / dureeAmortissement;

        // Initialisation de la valeur comptable
        double valeurComptable = dernierAmortissement.map(Amortissement::getValeurNette)
                .orElse(valeurAcquisition);

        for (int i = 0; i < dureeRestante; i++) {
            double montantAmorti;

            // Ajustement pour la première année avec prorata
            if (i == 0 && isProrataApplicable(immobilisation, dateDebutExercice)) {
                montantAmorti = prorataCalcul(immobilisation, montantAnnuel);
            } else if (i == dureeRestante - 1) {
                // Ajustement pour la dernière année pour équilibrer les montants
                montantAmorti = valeurComptable;
            } else {
                montantAmorti = montantAnnuel;
            }

            valeurComptable -= montantAmorti;

            // Calcul du taux annuel avant modification de la valeur comptable
            double tauxAnnuel = (montantAmorti / valeurAcquisition) * 100;

            logger.info("Calcul linéaire - Année {} : Montant amorti={}, Valeur nette={}, Taux Annuel={}",
                    i + 1, montantAmorti, valeurComptable, tauxAnnuel);

            amortissements.add(Amortissement.builder()
                    .immobilisation(immobilisation)
                    .methode("Linéaire")
                    .montantAmorti(montantAmorti)
                    .dateDebutExercice(dateDebutExercice.plusYears(i))
                    .dateCalcul(dateDebutExercice.plusYears(i + 1).minusDays(1))
                    .valeurNette(Math.max(valeurComptable, 0.0))
                    .tauxAnnuel(tauxAnnuel)
                    .prorata(i == 0 ? prorataCalcul(immobilisation, montantAnnuel) : 0.0)
                    .statut(i == dureeRestante - 1 ? StatutAmmortissement.AMMORTI : StatutAmmortissement.EN_COURS)
                    .build());
        }

        // Validation finale pour vérifier que le total des amortissements est correct
        double totalAmortissements = amortissements.stream()
                .mapToDouble(Amortissement::getMontantAmorti)
                .sum();

        if (Math.abs(totalAmortissements - valeurAcquisition) > 0.01) {
            throw new IllegalStateException("Le total des amortissements ne correspond pas à la valeur d'acquisition.");
        }

        return amortissements;
    }

    private boolean isProrataApplicable(Immobilisation immobilisation, LocalDate dateDebutExercice) {
        return !dateDebutExercice.isEqual(immobilisation.getDateAcquisition());
    }

    private double prorataCalcul(Immobilisation immobilisation, double montantAnnuel) {
        int joursRestants = 365 - immobilisation.getDateAcquisition().getDayOfYear() + 1;
        if (joursRestants <= 0 || montantAnnuel <= 0) {
            throw new IllegalArgumentException("Jours restants ou montant annuel invalide pour le calcul du prorata.");
        }
        return (montantAnnuel * joursRestants) / 365;
    }

    private void validateDureeRestante(int dureeRestante) {
        if (dureeRestante <= 0) {
            throw new IllegalArgumentException("Durée restante invalide : aucune période d'amortissement restante.");
        }
    }

    private int calculateDureeRestante(Immobilisation immobilisation, Optional<Amortissement> dernierAmortissement) {
        int dureeAmortissement = immobilisation.getCategorie().getDureeAmortissement();
        return dureeAmortissement - dernierAmortissement
                .map(a -> a.getDateCalcul().getYear() - immobilisation.getDateAcquisition().getYear())
                .orElse(0);
    }
}
