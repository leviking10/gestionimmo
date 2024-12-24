package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.SituationAmortissementDTO;
import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import com.sodeca.gestionimmo.enums.TypeAmortissement;
import com.sodeca.gestionimmo.mapper.AmortissementMapper;
import com.sodeca.gestionimmo.repository.AmortissementRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AmortissementServiceImpl implements AmortissementService {

    private static final Logger logger = LoggerFactory.getLogger(AmortissementServiceImpl.class);

    private final AmortissementRepository amortissementRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final AmortissementLineaireStrategy lineaireStrategy;
    private final AmortissementDegressifStrategy degressifStrategy;
    private final AmortissementMapper amortissementMapper;

    public AmortissementServiceImpl(
            AmortissementRepository amortissementRepository,
            ImmobilisationRepository immobilisationRepository,
            AmortissementLineaireStrategy lineaireStrategy,
            AmortissementDegressifStrategy degressifStrategy,
            AmortissementMapper amortissementMapper) {
        this.amortissementRepository = amortissementRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.lineaireStrategy = lineaireStrategy;
        this.degressifStrategy = degressifStrategy;
        this.amortissementMapper = amortissementMapper;
    }

    @Override
    public List<AmortissementDTO> getAmortissementsByImmobilisation(Long immobilisationId) {
        logger.info("Fetching amortissements for immobilisation ID: {}", immobilisationId);
        return amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(amortissementMapper::toDTO)
                .toList();
    }

    @Override
    public List<AmortissementDTO> generateAmortissementsForImmobilisation(Long immobilisationId, String methode) {
        logger.info("Generating amortissements for immobilisation ID: {} with method: {}", immobilisationId, methode);

        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        validateTypeAmortissement(immobilisation, methode);

        Optional<Amortissement> dernierAmortissement = getDernierAmortissement(immobilisationId);

        if (dernierAmortissement.isPresent() && dernierAmortissement.get().getStatut() == StatutAmmortissement.AMORTI) {
            throw new RuntimeException("L'immobilisation est déjà entièrement amortie.");
        }

        List<Amortissement> amortissements;

        if (TypeAmortissement.LINEAIRE.getLabel().equalsIgnoreCase(methode)) {
            amortissements = lineaireStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
            amortissements.forEach(amortissement -> {
                amortissement.setCoefficientDegressif(null);
                amortissement.setTauxDegressif(null);
                amortissement.setMontantCumule(0.0);
            });
        } else if (TypeAmortissement.DEGRESSIF.getLabel().equalsIgnoreCase(methode)) {
            amortissements = degressifStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
            amortissements.forEach(amortissement -> {
                amortissement.setTauxAnnuel(null);
                amortissement.setProrata(null);
            });
        } else {
            throw new RuntimeException("Méthode d'amortissement non reconnue : " + methode);
        }

        amortissementRepository.saveAll(amortissements);

        logger.info("Amortissements successfully generated for immobilisation ID: {}", immobilisationId);
        return amortissements.stream()
                .map(amortissementMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteAmortissement(int id) {
        logger.info("Deleting amortissement with ID: {}", id);
        if (!amortissementRepository.existsById(id)) {
            throw new RuntimeException("Amortissement introuvable avec l'ID : " + id);
        }
        amortissementRepository.deleteById(id);
    }

    @Override
    public void cancelAmortissement(int id) {
        logger.info("Cancelling amortissement with ID: {}", id);
        Amortissement amortissement = amortissementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amortissement introuvable avec l'ID : " + id));

        if (amortissement.getStatut() == StatutAmmortissement.AMORTI) {
            throw new RuntimeException("Impossible d'annuler un amortissement déjà complet.");
        }

        amortissement.setStatut(StatutAmmortissement.ANNULE);
        amortissementRepository.save(amortissement);
    }

    @Override
    public SituationAmortissementDTO getSituationAmortissementsAvecCumul(Long immobilisationId, String date) {
        logger.info("Fetching amortissement situation for immobilisation ID: {} up to date: {}", immobilisationId, date);
        LocalDate filterDate = LocalDate.parse(date);
        List<Amortissement> amortissements = amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .filter(amortissement -> !amortissement.getDateCalcul().isAfter(filterDate))
                .toList();

        double cumulAmortissements = amortissements.stream()
                .mapToDouble(Amortissement::getMontantAmorti)
                .sum();

        return new SituationAmortissementDTO(
                amortissementMapper.toDTOList(amortissements),
                cumulAmortissements
        );
    }

    @Override
    public List<AmortissementDTO> getAllAmortissements() {
        return null;
    }

    @Override
    public List<AmortissementDTO> getFilteredAmortissements(String categorie, String methode, String etat, String periode) {
        return null;
    }

    private Optional<Amortissement> getDernierAmortissement(Long immobilisationId) {
        logger.info("Fetching last amortissement for immobilisation ID: {}", immobilisationId);
        return amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .max((a1, a2) -> a1.getDateCalcul().compareTo(a2.getDateCalcul()));
    }

    private void validateTypeAmortissement(Immobilisation immobilisation, String methode) {
        // Vérification de la validité du paramètre "methode"
        if (methode == null || methode.trim().isEmpty()) {
            throw new RuntimeException("La méthode d'amortissement fournie est nulle ou vide.");
        }

        // Vérification de la présence du type d'amortissement dans l'immobilisation
        if (immobilisation.getTypeAmortissement() == null) {
            throw new RuntimeException("Le type d'amortissement est manquant pour l'immobilisation.");
        }

        // Conversion robuste du type d'amortissement
        TypeAmortissement typeAmortissement;
        try {
            typeAmortissement = TypeAmortissement.fromLabelOrName(immobilisation.getTypeAmortissement().toString());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Type d'amortissement invalide : " + immobilisation.getTypeAmortissement(), e);
        }

        // Validation de la correspondance entre le type et la méthode
        if (!typeAmortissement.getLabel().equalsIgnoreCase(methode)) {
            throw new RuntimeException("La méthode d'amortissement (" + methode + ") ne correspond pas au type défini pour l'immobilisation (" + typeAmortissement.getLabel() + ").");
        }
    }




}