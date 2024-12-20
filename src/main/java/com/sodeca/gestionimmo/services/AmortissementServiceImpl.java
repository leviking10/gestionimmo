package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.SituationAmortissementDTO;
import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import com.sodeca.gestionimmo.enums.TypeAmortissement;
import com.sodeca.gestionimmo.repository.AmortissementRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AmortissementServiceImpl implements AmortissementService {

    private final AmortissementRepository amortissementRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final AmortissementLineaireStrategy lineaireStrategy;
    private final AmortissementDegressifStrategy degressifStrategy;

    public AmortissementServiceImpl(
            AmortissementRepository amortissementRepository,
            ImmobilisationRepository immobilisationRepository,
            AmortissementLineaireStrategy lineaireStrategy,
            AmortissementDegressifStrategy degressifStrategy) {
        this.amortissementRepository = amortissementRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.lineaireStrategy = lineaireStrategy;
        this.degressifStrategy = degressifStrategy;
    }

    @Override
    public List<AmortissementDTO> getAmortissementsByImmobilisation(Long immobilisationId) {
        return amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<AmortissementDTO> generateAmortissementsForImmobilisation(Long immobilisationId, String methode) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        Optional<Amortissement> dernierAmortissement = getDernierAmortissement(immobilisationId);

        if (dernierAmortissement.isPresent() && dernierAmortissement.get().getStatut() == StatutAmmortissement.AMMORTI) {
            throw new RuntimeException("L'immobilisation est déjà entièrement amortie.");
        }

        List<Amortissement> amortissements;

        if (TypeAmortissement.LINEAIRE.getLabel().equalsIgnoreCase(methode)) {
            amortissements = lineaireStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
        } else if (TypeAmortissement.DEGRESSIF.getLabel().equalsIgnoreCase(methode)) {
            amortissements = degressifStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
        } else {
            throw new RuntimeException("Méthode d'amortissement non reconnue : " + methode);
        }

        amortissementRepository.saveAll(amortissements);

        return amortissements.stream()
                .map(this::mapToDTO)
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

    @Override
    public SituationAmortissementDTO getSituationAmortissementsAvecCumul(Long immobilisationId, String date) {
        LocalDate filterDate = LocalDate.parse(date);
        List<Amortissement> amortissements = amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .filter(amortissement -> !amortissement.getDateCalcul().isAfter(filterDate))
                .toList();

        double cumulAmortissements = amortissements.stream()
                .mapToDouble(Amortissement::getMontantAmorti)
                .sum();

        List<AmortissementDTO> amortissementDTOs = amortissements.stream()
                .map(this::mapToDTO)
                .toList();

        return new SituationAmortissementDTO(amortissementDTOs, cumulAmortissements);
    }

    private Optional<Amortissement> getDernierAmortissement(Long immobilisationId) {
        return amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .max((a1, a2) -> a1.getDateCalcul().compareTo(a2.getDateCalcul()));
    }

    private AmortissementDTO mapToDTO(Amortissement amortissement) {
        return new AmortissementDTO(
                amortissement.getId(),
                amortissement.getImmobilisation().getId(),
                amortissement.getMethode(),
                amortissement.getMontantAmorti(),
                amortissement.getDateDebutExercice(),
                amortissement.getDateCalcul(),
                amortissement.getValeurNette(),
                amortissement.getStatut()
        );
    }
}
