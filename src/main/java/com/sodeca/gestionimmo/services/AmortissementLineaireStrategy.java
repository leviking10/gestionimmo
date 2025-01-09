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

        // Taux constant pour toute la durée
        double tauxAnnuelConstant = (1.0 / dureeAmortissement) * 100;

        LocalDate dateDebutExercice = dernierAmortissement
                .map(Amortissement::getDateCalcul)
                .orElse(immobilisation.getDateMiseEnService().withDayOfYear(1)); // Date exacte de mise en service

        double montantAnnuel = valeurAcquisition / dureeAmortissement;
        double valeurComptable = dernierAmortissement.map(Amortissement::getValeurNette)
                .orElse(valeurAcquisition);

        for (int i = 0; i < dureeRestante; i++) {
            double montantAmorti;

            if (i == 0 && isProrataApplicable(immobilisation)) {
                montantAmorti = prorataCalcul(immobilisation, montantAnnuel);
            } else {
                montantAmorti = Math.min(montantAnnuel, valeurComptable);
            }

            valeurComptable -= montantAmorti;

            logger.info("Année {} : Montant amorti = {}, Valeur nette = {}", i + 1, montantAmorti, valeurComptable);

            amortissements.add(Amortissement.builder()
                    .immobilisation(immobilisation)
                    .methode("Linéaire")
                    .montantAmorti(montantAmorti)
                    .dateDebutExercice(dateDebutExercice.plusYears(i))
                    .dateCalcul(dateDebutExercice.plusYears((long) i + 1).minusDays(1)) // Cast explicite de i + 1 en long
                    .valeurNette(Math.max(valeurComptable, 0.0))
                    .tauxAnnuel(tauxAnnuelConstant)
                    .prorata(i == 0 && isProrataApplicable(immobilisation) ? prorataCalcul(immobilisation, montantAnnuel) : 0.0)
                    .statut(valeurComptable <= 0 ? StatutAmmortissement.AMORTI : StatutAmmortissement.EN_COURS)
                    .build());
        }


        if (valeurComptable > 0) {
            logger.info("Ajout d'une dernière ligne pour équilibrer. Montant restant = {}", valeurComptable);

            amortissements.add(Amortissement.builder()
                    .immobilisation(immobilisation)
                    .methode("Linéaire")
                    .montantAmorti(valeurComptable)
                    .dateDebutExercice(dateDebutExercice.plusYears(dureeRestante))
                    .dateCalcul(dateDebutExercice.plusYears((long) dureeRestante + 1).minusDays(1))
                    .valeurNette(0.0)
                    .tauxAnnuel(tauxAnnuelConstant)
                    .prorata(0.0)
                    .statut(StatutAmmortissement.AMORTI)
                    .build());
        }

        double totalAmortissements = amortissements.stream()
                .mapToDouble(Amortissement::getMontantAmorti)
                .sum();

        logger.info("Valeur d'acquisition : {}, Total des amortissements : {}", valeurAcquisition, totalAmortissements);

        if (Math.abs(totalAmortissements - valeurAcquisition) > 0.1) { // Tolérance
            throw new IllegalStateException("Le total des amortissements ne correspond pas à la valeur d'acquisition.");
        }

        return amortissements;
    }


    // Vérifie si un prorata est applicable
    private boolean isProrataApplicable(Immobilisation immobilisation) {
        LocalDate dateMiseEnService = immobilisation.getDateMiseEnService();
        return dateMiseEnService.getDayOfYear() != 1; // Pas de prorata si la mise en service est le 01/01
    }

    // Calcule le prorata pour la première année
    private double prorataCalcul(Immobilisation immobilisation, double montantAnnuel) {
        int joursRestants = 365 - immobilisation.getDateMiseEnService().getDayOfYear() + 1;
        return (montantAnnuel * joursRestants) / 365;
    }

    // Valide la durée restante
    private void validateDureeRestante(int dureeRestante) {
        if (dureeRestante <= 0) {
            throw new IllegalArgumentException("Durée restante invalide : aucune période d'amortissement restante.");
        }
    }

    // Calcule la durée restante
    private int calculateDureeRestante(Immobilisation immobilisation, Optional<Amortissement> dernierAmortissement) {
        int dureeAmortissement = immobilisation.getCategorie().getDureeAmortissement();
        LocalDate dateMiseEnService = immobilisation.getDateMiseEnService();
        return dureeAmortissement - dernierAmortissement
                .map(a -> a.getDateCalcul().getYear() - dateMiseEnService.getYear())
                .orElse(0);
    }
}
