package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutAmmortissement;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.AmortissementRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class AmortissementServiceImpl implements AmortissementService {

    private final AmortissementRepository amortissementRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final AmortissementLineaireStrategy lineaireStrategy;
    private final AmortissementDegressifStrategy degressifStrategy;
    private final ImmobilisationMapper mapper;

    public AmortissementServiceImpl(
            AmortissementRepository amortissementRepository,
            ImmobilisationRepository immobilisationRepository,
            AmortissementLineaireStrategy lineaireStrategy,
            AmortissementDegressifStrategy degressifStrategy,
            ImmobilisationMapper mapper) {
        this.amortissementRepository = amortissementRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.lineaireStrategy = lineaireStrategy;
        this.degressifStrategy = degressifStrategy;
        this.mapper = mapper;
    }

    @Override
    public List<AmortissementDTO> getAmortissementsByImmobilisation(Long immobilisationId) {
        return amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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

        switch (methode.toLowerCase()) {
            case "linéaire":
                amortissements = lineaireStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
                break;
            case "dégressif":
                amortissements = degressifStrategy.calculerAmortissements(immobilisation, dernierAmortissement);
                break;
            default:
                throw new RuntimeException("Méthode d'amortissement non reconnue : " + methode);
        }

        amortissementRepository.saveAll(amortissements);

        return amortissements.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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

    // Méthode pour récupérer le dernier amortissement
    private Optional<Amortissement> getDernierAmortissement(Long immobilisationId) {
        return amortissementRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .max((a1, a2) -> a1.getDateCalcul().compareTo(a2.getDateCalcul()));
    }

    // Méthode pour convertir Amortissement en DTO
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
