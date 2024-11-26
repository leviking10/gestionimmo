package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.AmortissementRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AmortissementServiceImpl implements AmortissementService {

    private final AmortissementRepository amortissementRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final ImmobilisationMapper mapper;

    public AmortissementServiceImpl(AmortissementRepository amortissementRepository,
                                    ImmobilisationRepository immobilisationRepository,
                                    ImmobilisationMapper mapper) {
        this.amortissementRepository = amortissementRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AmortissementDTO> getAmortissementsByImmobilisation(Long immobilisationId) {
        return amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(amortissement -> new AmortissementDTO(
                        amortissement.getId(),
                        amortissement.getImmobilisation().getId(),
                        amortissement.getMethode(),
                        round(amortissement.getMontantAmorti(), 2),
                        amortissement.getDateCalcul(),
                        amortissement.getDateDebutExercice(),
                        round(amortissement.getValeurNette(), 2),
                        amortissement.getStatut()
                ))
                .collect(Collectors.toList());
    }



    // Méthode utilitaire pour arrondir à un nombre donné de décimales
    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    @Override
    public List<AmortissementDTO> generateAmortissementsForImmobilisation(Long immobilisationId, String methode) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        Optional<Amortissement> dernierAmortissement = amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .max((a1, a2) -> a1.getDateCalcul().compareTo(a2.getDateCalcul()));

        if (dernierAmortissement.isPresent() && dernierAmortissement.get().getStatut().equals(StatutAmmortissement.AMMORTI)) {
            throw new RuntimeException("L'immobilisation est déjà entièrement amortie.");
        }

        double valeurComptable = dernierAmortissement.map(Amortissement::getValeurNette).orElse(immobilisation.getValeurAcquisition());
        int dureeAmortissement = immobilisation.getCategorie().getDureeAmortissement();
        int dureeRestante = dureeAmortissement - dernierAmortissement.map(a -> a.getDateCalcul().getYear() - immobilisation.getDateAcquisition().getYear()).orElse(0);

        if (dureeRestante <= 0) {
            throw new RuntimeException("Durée restante invalide pour l'immobilisation.");
        }

        List<Amortissement> amortissements = new ArrayList<>();

        // Définir la date de début d'exercice (01/01 de l'année d'acquisition)
        LocalDate dateDebutExercice = LocalDate.of(immobilisation.getDateAcquisition().getYear(), 1, 1);
        LocalDate dateCalcul = dernierAmortissement.map(Amortissement::getDateCalcul).orElse(dateDebutExercice);

        // Gestion de la première année avec prorata temporis
        long joursRestantsPremiereAnnee = dateCalcul.until(LocalDate.of(dateCalcul.getYear(), 12, 31)).getDays() + 1;
        long totalJoursAnnee = 360; // Convention pour le prorata temporis

        if ("Linéaire".equalsIgnoreCase(methode)) {
            double montantAnnuel = valeurComptable / dureeAmortissement;

            // Calcul du prorata pour la première année
            double montantPremiereAnnee = (joursRestantsPremiereAnnee / (double) totalJoursAnnee) * montantAnnuel;
            valeurComptable -= montantPremiereAnnee;

            Amortissement premierAmortissement = new Amortissement();
            premierAmortissement.setImmobilisation(immobilisation);
            premierAmortissement.setMethode("Linéaire");
            premierAmortissement.setMontantAmorti(round(montantPremiereAnnee, 2));
            premierAmortissement.setDateDebutExercice(dateDebutExercice);
            premierAmortissement.setDateCalcul(dateCalcul);
            premierAmortissement.setValeurNette(round(valeurComptable, 2));
            premierAmortissement.setDureeRestante(dureeRestante - 1);
            premierAmortissement.setStatut(StatutAmmortissement.EN_COURS);

            amortissements.add(premierAmortissement);

            // Années complètes
            for (int i = 1; i < dureeRestante - 1; i++) {
                double montantAmorti = montantAnnuel;
                valeurComptable -= montantAmorti;

                Amortissement amortissement = new Amortissement();
                amortissement.setImmobilisation(immobilisation);
                amortissement.setMethode("Linéaire");
                amortissement.setMontantAmorti(round(montantAmorti, 2));
                amortissement.setDateDebutExercice(dateDebutExercice.plusYears(i));
                amortissement.setDateCalcul(dateCalcul.plusYears(i));
                amortissement.setValeurNette(round(Math.max(valeurComptable, 0.0), 2));
                amortissement.setDureeRestante(dureeRestante - 1 - i);
                amortissement.setStatut(StatutAmmortissement.EN_COURS);

                amortissements.add(amortissement);
            }

            // Gestion de la dernière année avec prorata temporis
            LocalDate finAmortissement = dateCalcul.plusYears(dureeRestante - 1);
            long joursDerniereAnnee = finAmortissement.until(LocalDate.of(finAmortissement.getYear(), 12, 31)).getDays() + 1;
            double montantDerniereAnnee = (joursDerniereAnnee / (double) totalJoursAnnee) * montantAnnuel;

            Amortissement dernierAmortissementEntity = new Amortissement();
            dernierAmortissementEntity.setImmobilisation(immobilisation);
            dernierAmortissementEntity.setMethode("Linéaire");
            dernierAmortissementEntity.setMontantAmorti(round(montantDerniereAnnee, 2));
            dernierAmortissementEntity.setDateDebutExercice(dateDebutExercice.plusYears(dureeRestante - 1));
            dernierAmortissementEntity.setDateCalcul(finAmortissement);
            dernierAmortissementEntity.setValeurNette(0.0); // L'immobilisation est totalement amortie
            dernierAmortissementEntity.setDureeRestante(0);
            dernierAmortissementEntity.setStatut(StatutAmmortissement.AMMORTI);

            amortissements.add(dernierAmortissementEntity);
        } else {
            throw new RuntimeException("Méthode d'amortissement non reconnue : " + methode);
        }

        amortissementRepository.saveAll(amortissements);

        return amortissements.stream()
                .map(a -> new AmortissementDTO(
                        a.getId(),
                        a.getImmobilisation().getId(),
                        a.getMethode(),
                        a.getMontantAmorti(),
                        a.getDateDebutExercice(),
                        a.getDateCalcul(),
                        a.getValeurNette(),
                        a.getStatut()))
                .toList();
    }


    @Override
    public void deleteAmortissement(int id) {
        if (!amortissementRepository.existsById(id)) {
            throw new RuntimeException("Amortissement introuvable avec l'ID : " + id);
        }
        amortissementRepository.deleteById(id);
    }

    @Override
    public void cancelAmortissement(int id) {
        Amortissement amortissement = amortissementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amortissement introuvable avec l'ID : " + id));

        if (amortissement.getStatut() == StatutAmmortissement.AMMORTI) {
            throw new RuntimeException("Impossible d'annuler un amortissement déjà complet.");
        }

        amortissement.setStatut(StatutAmmortissement.ANNULE);
        amortissementRepository.save(amortissement);
    }

}