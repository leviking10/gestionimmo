package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.SituationAmortissementDTO;
import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import com.sodeca.gestionimmo.enums.StatutCession;
import com.sodeca.gestionimmo.enums.TypeAmortissement;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.AmortissementMapper;
import com.sodeca.gestionimmo.repository.AmortissementRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
            throw new BusinessException("L'immobilisation est déjà entièrement amortie.");
        }

        List<Amortissement> amortissements;

        if (TypeAmortissement.LINEAIRE.getLabel().equalsIgnoreCase(methode)) {
            amortissements = lineaireStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
            amortissements.forEach(amortissement -> {
                amortissement.setCoefficientDegressif(null);
                amortissement.setTauxDegressif(null);
            });
        } else if (TypeAmortissement.DEGRESSIF.getLabel().equalsIgnoreCase(methode)) {
            amortissements = degressifStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
            amortissements.forEach(amortissement -> amortissement.setTauxAnnuel(null));
        }
        else {
            throw new BusinessException("Méthode d'amortissement non supportée : " + methode);
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
            throw new BusinessException("Amortissement introuvable avec l'ID : " + id);
        }
        amortissementRepository.deleteById(id);
    }

    @Override
    public void cancelAmortissement(int id) {
        logger.info("Cancelling amortissement with ID: {}", id);
        Amortissement amortissement = amortissementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amortissement introuvable avec l'ID : " + id));

        if (amortissement.getStatut() == StatutAmmortissement.AMORTI) {
            throw new BusinessException("Impossible d'annuler un amortissement déjà complet.");
        }

        amortissement.setStatut(StatutAmmortissement.ANNULE);
        amortissementRepository.save(amortissement);
    }

    @Override
    public SituationAmortissementDTO getSituationAmortissementsAvecCumul(Long immobilisationId, String date) {
      // Rendre filterDate "effectivement finale"
        final LocalDate filterDate = LocalDate.parse(date);

        // Récupérer l'immobilisation
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        // Vérifier si l'immobilisation a une date de sortie
        LocalDate adjustedDate;
        if (immobilisation.getDateCession() != null && immobilisation.getDateCession().isBefore(filterDate)) {
            adjustedDate = immobilisation.getDateCession();
            logger.info("La date de sortie ({}) limite le calcul des amortissements.", adjustedDate);
        } else {
            adjustedDate = filterDate;
        }

        // Filtrer les amortissements jusqu'à la date ajustée
        List<Amortissement> amortissements = amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .filter(amortissement -> !amortissement.getDateCalcul().isAfter(adjustedDate))
                .toList();

        // Calculer le cumul des amortissements
        double cumulAmortissements = amortissements.stream()
                .mapToDouble(Amortissement::getMontantAmorti)
                .sum();

        // Calculer la valeur nette comptable (V.N.C)
        double valeurNette = immobilisation.getValeurAcquisition() - cumulAmortissements;

        // Déterminer le statut à date
        String statut;
        if (immobilisation.getStatutCession() == StatutCession.SORTIE || immobilisation.getStatutCession() == StatutCession.MISE_EN_REBUT) {
            statut = "SORTIE";
        } else if (valeurNette <= 0) {
            statut = "AMORTI";
        } else {
            statut = "EN_COURS";
        }
        // Créer et retourner le DTO
        return SituationAmortissementDTO.builder()
                .amortissements(amortissementMapper.toDTOList(amortissements))
                .cumulAmortissements(cumulAmortissements)
                .valeurNette(valeurNette)
                .statut(statut)
                .build();
    }

    @Override
    public List<AmortissementDTO> getAllAmortissements() {
        return amortissementRepository.findAll().stream().map(amortissementMapper::toDTO).toList();
    }

    @Override
    public List<AmortissementDTO> getFilteredAmortissements(String categorie, String methode, String etat, String periode) {
        return amortissementRepository.findAll().stream()
                .filter(amortissement -> categorie == null || amortissement.getImmobilisation().getCategorie().getDescription().equalsIgnoreCase(categorie))
                .filter(amortissement -> methode == null || amortissement.getImmobilisation().getTypeAmortissement().toString().equalsIgnoreCase(methode))
                .filter(amortissement -> etat == null || amortissement.getStatut().toString().equalsIgnoreCase(etat))
                .filter(amortissement -> periode == null || amortissement.getDateCalcul().toString().contains(periode))
                .map(amortissementMapper::toDTO)
                .toList();
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
            throw new BusinessException("La méthode d'amortissement fournie est nulle ou vide.",
                    "INVALID_AMORTISSEMENT_METHOD", HttpStatus.BAD_REQUEST);
        }

        // Vérification de la présence du type d'amortissement dans l'immobilisation
        if (immobilisation.getTypeAmortissement() == null) {
            throw new BusinessException("Le type d'amortissement est manquant pour l'immobilisation.",
                    "MISSING_AMORTISSEMENT_TYPE", HttpStatus.BAD_REQUEST);
        }

        // Conversion robuste du type d'amortissement
        TypeAmortissement typeAmortissement;
        try {
            typeAmortissement = TypeAmortissement.fromLabelOrName(immobilisation.getTypeAmortissement().toString());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Type d'amortissement invalide : " + immobilisation.getTypeAmortissement(),
                    "INVALID_AMORTISSEMENT_TYPE", HttpStatus.BAD_REQUEST, e);
        }

        // Validation de la correspondance entre le type et la méthode
        if (!typeAmortissement.getLabel().equalsIgnoreCase(methode)) {
            throw new BusinessException("La méthode d'amortissement (" + methode + ") ne correspond pas au type défini pour l'immobilisation (" + typeAmortissement.getLabel() + ").",
                    "AMORTISSEMENT_METHOD_MISMATCH", HttpStatus.BAD_REQUEST);
        }
    }





}