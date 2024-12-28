package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.SignalementDTO;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Intervention;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Signalement;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.enums.TypeIntervention;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.SignalementMapper;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import com.sodeca.gestionimmo.repository.InterventionRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import com.sodeca.gestionimmo.repository.SignalementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SignalementServiceImpl implements SignalementService {

    private final SignalementRepository signalementRepository;
    private final PersonnelRepository personnelRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final SignalementMapper signalementMapper;
    private final InterventionRepository interventionRepository;

    public SignalementServiceImpl(SignalementRepository signalementRepository,
                                  PersonnelRepository personnelRepository,
                                  ImmobilisationRepository immobilisationRepository,
                                  SignalementMapper signalementMapper, InterventionRepository interventionRepository) {
        this.signalementRepository = signalementRepository;
        this.personnelRepository = personnelRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.signalementMapper = signalementMapper;
        this.interventionRepository = interventionRepository;
    }
    @Override
    @Transactional
    public void cancelSignalement(Long signalementId) {
        log.info("Annulation du signalement ID {}", signalementId);

        // Valider et récupérer le signalement
        Signalement signalement = validateSignalement(signalementId);

        // Récupérer l'immobilisation associée
        Immobilisation immobilisation = signalement.getImmobilisation();

        // Vérifier l'état de l'immobilisation pour éviter des conflits
        if (!immobilisation.getEtatImmo().equals(EtatImmobilisation.SIGNALE)) {
            throw new BusinessException("Impossible d'annuler un signalement pour une immobilisation non signalée.");
        }

        // Mettre à jour l'état de l'immobilisation
        immobilisation.setEtatImmo(EtatImmobilisation.EN_SERVICE); // Ou autre état approprié
        immobilisationRepository.save(immobilisation);

        // Supprimer l'intervention associée au signalement
        interventionRepository.deleteByImmobilisationAndTypeAndStatut(
                immobilisation, TypeIntervention.CORRECTIVE, StatutIntervention.EN_ATTENTE
        );

        // Supprimer le signalement
        signalementRepository.deleteById(signalementId);

        log.info("Signalement ID {} annulé avec succès", signalementId);
    }

    @Override
    public SignalementDTO createSignalement(SignalementDTO signalementDTO) {
        // Vérification et récupération du personnel
        Personnel personnel = personnelRepository.findById(signalementDTO.getPersonnelId())
                .orElseThrow(() -> new BusinessException("Personnel introuvable"));

        // Vérification et récupération de l'immobilisation
        Immobilisation immobilisation = immobilisationRepository.findById(signalementDTO.getImmobilisationId())
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable"));

        // Vérifier si l'immobilisation est déjà signalée
        if (immobilisation.getEtatImmo() == EtatImmobilisation.SIGNALE) {
            throw new BusinessException("L'immobilisation est déjà signalée en panne.");
        }

        // Mettre à jour l'état de l'immobilisation
        immobilisation.setEtatImmo(EtatImmobilisation.SIGNALE);
        immobilisationRepository.save(immobilisation);

        // Créer le signalement
        Signalement signalement = signalementMapper.toEntity(signalementDTO);
        signalement.setPersonnel(personnel);
        signalement.setImmobilisation(immobilisation);
        signalement.setDateSignalement(LocalDateTime.now());
        signalement.setDescription(signalementDTO.getDescription());
        Signalement savedSignalement = signalementRepository.save(signalement);

        // Créer automatiquement une intervention associée
        createInterventionForSignalement(immobilisation, savedSignalement);

        return signalementMapper.toDTO(savedSignalement);
    }

    // Méthode privée pour créer l'intervention
    private void createInterventionForSignalement(Immobilisation immobilisation, Signalement signalement) {
        Intervention intervention = new Intervention();
        intervention.setImmobilisation(immobilisation);
        intervention.setType(TypeIntervention.CORRECTIVE);
        intervention.setStatut(StatutIntervention.EN_ATTENTE);
        intervention.setDatePlanification(LocalDate.now()); // Date actuelle comme date de planification
        intervention.setDescription("Intervention générée automatiquement pour le signalement ID: " + signalement.getId());
        intervention.setPlanification(null); // Pas de planification associée dans ce cas
        interventionRepository.save(intervention);
    }

    @Override
    public List<SignalementDTO> getAllSignalements() {
        log.info("Récupération de tous les signalements");
        return signalementRepository.findAll()
                .stream()
                .map(signalementMapper::toDTO)
                .toList();
    }

    @Override
    public List<SignalementDTO> getSignalementsByDateRange(String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        log.info("Récupération des signalements entre {} et {}", start, end);
        return signalementRepository.findByDateSignalementBetween(start, end)
                .stream()
                .map(signalementMapper::toDTO)
                .toList();
    }

    @Override
    public SignalementDTO markSignalementAsTraite(Long id) {
        log.info("Marquage du signalement ID {} comme traité", id);
        Signalement signalement = validateSignalement(id);
        signalement.setDescription(signalement.getDescription() + " - Traité");
        signalementRepository.save(signalement);
        return signalementMapper.toDTO(signalement);
    }

    @Override
    public Long getSignalementCount() {
        log.info("Récupération du nombre total de signalements");
        return signalementRepository.count();
    }

    @Override
    public SignalementDTO updateSignalement(Long signalementId, SignalementDTO signalementDTO) {
        log.info("Mise à jour du signalement ID {}", signalementId);
        Signalement signalement = validateSignalement(signalementId);

        Personnel personnel = validatePersonnel(signalementDTO.getPersonnelId());
        Immobilisation immobilisation = validateImmobilisation(signalementDTO.getImmobilisationId());

        signalement.setPersonnel(personnel);
        signalement.setImmobilisation(immobilisation);
        signalement.setDescription(signalementDTO.getDescription());

        Signalement updatedSignalement = signalementRepository.save(signalement);
        return signalementMapper.toDTO(updatedSignalement);
    }

    @Override
    public void deleteSignalement(Long signalementId) {
        log.info("Suppression du signalement ID {}", signalementId);
        if (!signalementRepository.existsById(signalementId)) {
            log.warn("Signalement ID {} introuvable", signalementId);
            throw new BusinessException("Signalement introuvable.");
        }
        signalementRepository.deleteById(signalementId);
    }

    @Override
    public List<SignalementDTO> getSignalementsByImmobilisation(Long immobilisationId) {
        log.info("Récupération des signalements pour l'immobilisation ID {}", immobilisationId);
        Immobilisation immobilisation = validateImmobilisation(immobilisationId);
        return signalementRepository.findByImmobilisation(immobilisation)
                .stream()
                .map(signalementMapper::toDTO)
                .toList();
    }

    @Override
    public List<SignalementDTO> getSignalementsByPersonnel(Long personnelId) {
        log.info("Récupération des signalements pour le personnel ID {}", personnelId);
        Personnel personnel = validatePersonnel(personnelId);
        return signalementRepository.findByPersonnel(personnel)
                .stream()
                .map(signalementMapper::toDTO)
                .toList();
    }

    @Override
    public SignalementDTO getSignalementById(Long signalementId) {
        log.info("Récupération du signalement ID {}", signalementId);
        Signalement signalement = validateSignalement(signalementId);
        return signalementMapper.toDTO(signalement);
    }

    // Méthodes de validation
    private Signalement validateSignalement(Long signalementId) {
        return signalementRepository.findById(signalementId)
                .orElseThrow(() -> new BusinessException("Signalement introuvable."));
    }

    private Personnel validatePersonnel(Long personnelId) {
        return personnelRepository.findById(personnelId)
                .orElseThrow(() -> new BusinessException("Personnel introuvable."));
    }

    private Immobilisation validateImmobilisation(Long immobilisationId) {
        return immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable."));
    }
}
