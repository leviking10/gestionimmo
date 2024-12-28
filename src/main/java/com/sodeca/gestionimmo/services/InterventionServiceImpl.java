package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.InterventionDTO;
import com.sodeca.gestionimmo.entity.*;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.enums.StatutPlanification;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.InterventionMapper;
import com.sodeca.gestionimmo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InterventionServiceImpl implements InterventionService {

    private final InterventionRepository interventionRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final PersonnelRepository personnelRepository;
    private final PlanificationRepository planificationRepository;
    private final SignalementRepository signalementRepository;
    private final InterventionMapper mapper;

    public InterventionServiceImpl(InterventionRepository interventionRepository,
                                   ImmobilisationRepository immobilisationRepository,
                                   PersonnelRepository personnelRepository, PlanificationRepository planificationRepository, SignalementRepository signalementRepository,
                                   InterventionMapper mapper) {
        this.interventionRepository = interventionRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.personnelRepository = personnelRepository;
        this.planificationRepository = planificationRepository;
        this.signalementRepository = signalementRepository;
        this.mapper = mapper;
    }


    @Override
    public List<InterventionDTO> getInterventionsByImmobilisation(Long immobilisationId) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        return interventionRepository.findByImmobilisation(immobilisation)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
    @Override
    public InterventionDTO getInterventionById(Long id) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention introuvable avec l'ID : " + id));
        return mapper.toDTO(intervention);
    }
    @Override
    public InterventionDTO terminerIntervention(Long id, String rapport) {
        // Récupérer l'intervention
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Intervention introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND));

        // Vérifier que l'intervention est en cours
        if (!intervention.getStatut().equals(StatutIntervention.EN_COURS)) {
            throw new BusinessException("L'intervention ne peut être terminée car elle n'est pas en cours.", HttpStatus.BAD_REQUEST);
        }

        // Marquer l'intervention comme terminée
        intervention.setStatut(StatutIntervention.TERMINEE);
        intervention.setRapport(rapport);

        // Mettre à jour l'état de l'immobilisation
        Immobilisation immobilisation = intervention.getImmobilisation();
        immobilisation.setEtatImmo(EtatImmobilisation.EN_SERVICE);
        immobilisationRepository.save(immobilisation);

        // Si l'intervention est liée à une planification, terminer la planification
        Planification planification = intervention.getPlanification();
        if (planification != null && !planification.getStatut().equals(StatutPlanification.TERMINE)) {
            planification.setStatut(StatutPlanification.TERMINE);
            planification.setDateFin(LocalDate.now());
            planificationRepository.save(planification);
        }

        // Marquer le signalement lié comme traité
        if (immobilisation.getEtatImmo() == EtatImmobilisation.SIGNALE) {
            List<Signalement> signalements = signalementRepository.findByImmobilisation(immobilisation);
            for (Signalement signalement : signalements) {
                signalement.setDescription(signalement.getDescription() + " - Traité");
                signalementRepository.save(signalement);
            }
        }

        // Sauvegarder l'intervention
        return mapper.toDTO(interventionRepository.save(intervention));
    }

    @Override
    public void deleteIntervention(Long id) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Intervention introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND));

        // Si l'intervention est liée à un signalement, remettre le signalement et l'immobilisation à l'état initial
        Immobilisation immobilisation = intervention.getImmobilisation();
        if (immobilisation.getEtatImmo() == EtatImmobilisation.SIGNALE) {
            List<Signalement> signalements = signalementRepository.findByImmobilisation(immobilisation);
            for (Signalement signalement : signalements) {
                signalementRepository.delete(signalement); // Supprimer les signalements associés
            }
            immobilisation.setEtatImmo(EtatImmobilisation.EN_SERVICE); // Remettre l'immobilisation à l'état de service
            immobilisationRepository.save(immobilisation);
        }

        // Supprimer l'intervention
        interventionRepository.deleteById(id);
    }

    @Override
    public InterventionDTO commencerIntervention(Long id) {
        // Récupérer l'intervention
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Intervention introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND));

        // Vérifier que l'intervention est en attente
        if (!intervention.getStatut().equals(StatutIntervention.EN_ATTENTE)) {
            throw new BusinessException("L'intervention ne peut pas être démarrée car elle n'est pas en attente.", HttpStatus.BAD_REQUEST);
        }

        // Marquer l'intervention comme en cours
        intervention.setStatut(StatutIntervention.EN_COURS);
        intervention.setDateExecution(LocalDate.now());

        // Mettre à jour l'état de l'immobilisation
        Immobilisation immobilisation = intervention.getImmobilisation();
        immobilisation.setEtatImmo(EtatImmobilisation.EN_MAINTENANCE);
        immobilisationRepository.save(immobilisation);

        // Sauvegarder l'intervention
        return mapper.toDTO(interventionRepository.save(intervention));
    }

    @Override
    public InterventionDTO updateStatutIntervention(Long id, StatutIntervention statut) {
        // Récupérer l'intervention
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Intervention introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND));

        // Mettre à jour le statut de l'intervention
        intervention.setStatut(statut);

        // Mettre à jour l'état de l'immobilisation en fonction du statut
        Immobilisation immobilisation = intervention.getImmobilisation();
        switch (statut) {
            case EN_ATTENTE:
                immobilisation.setEtatImmo(EtatImmobilisation.PLANIFIE);
                break;
            case EN_COURS:
                immobilisation.setEtatImmo(EtatImmobilisation.EN_MAINTENANCE);
                break;
            case TERMINEE:
                immobilisation.setEtatImmo(EtatImmobilisation.EN_SERVICE);

                // Si l'intervention est liée à une planification, terminer la planification
                Planification planification = intervention.getPlanification();
                if (planification != null && !planification.getStatut().equals(StatutPlanification.TERMINE)) {
                    planification.setStatut(StatutPlanification.TERMINE);
                    planification.setDateFin(LocalDate.now());
                    planificationRepository.save(planification);
                }
                break;
            default:
                throw new BusinessException("Statut d'intervention inconnu : " + statut, HttpStatus.BAD_REQUEST);
        }

        // Sauvegarder les modifications
        immobilisationRepository.save(immobilisation);
        interventionRepository.save(intervention);

        return mapper.toDTO(intervention);
    }

    @Override
    public InterventionDTO createIntervention(InterventionDTO dto) {
        // Validation pour planification
        Planification planification = null;
        if (dto.getPlanificationId() != null) {
            planification = planificationRepository.findById(dto.getPlanificationId())
                    .orElseThrow(() -> new BusinessException("Planification introuvable", HttpStatus.NOT_FOUND));

            if (planification.getStatut() == StatutPlanification.TERMINE) {
                throw new BusinessException("Impossible de créer une intervention pour une planification terminée.", HttpStatus.BAD_REQUEST);
            }
        }

        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable", HttpStatus.NOT_FOUND));

        Personnel technicien = null;
        if (dto.getTechnicienId() != null) {
            technicien = personnelRepository.findById(dto.getTechnicienId())
                    .orElseThrow(() -> new BusinessException("Technicien introuvable", HttpStatus.NOT_FOUND));
        }

        // Création de l'intervention
        Intervention intervention = mapper.toEntity(dto);
        intervention.setImmobilisation(immobilisation);
        intervention.setTechnicien(technicien);
        intervention.setPlanification(planification);
        intervention.setStatut(StatutIntervention.EN_ATTENTE);

        // Mettre à jour l'état de l'immobilisation
        immobilisation.setEtatImmo(EtatImmobilisation.EN_MAINTENANCE);
        immobilisationRepository.save(immobilisation);

        Intervention savedIntervention = interventionRepository.save(intervention);
        return mapper.toDTO(savedIntervention);
    }

    @Override
    public List<InterventionDTO> getAllInterventions() {
        return interventionRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public InterventionDTO updateIntervention(Long id, InterventionDTO dto) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Intervention introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND));

        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable", HttpStatus.NOT_FOUND));

        Personnel technicien = null;
        if (dto.getTechnicienId() != null) {
            technicien = personnelRepository.findById(dto.getTechnicienId())
                    .orElseThrow(() -> new BusinessException("Technicien introuvable", HttpStatus.NOT_FOUND));
        }

        // Mise à jour des champs
        intervention.setImmobilisation(immobilisation);
        intervention.setTechnicien(technicien);
        intervention.setDescription(dto.getDescription());
        intervention.setDatePlanification(dto.getDatePlanification());
        intervention.setDateExecution(dto.getDateExecution());
        intervention.setRapport(dto.getRapport());
        intervention.setStatut(dto.getStatut());

        Intervention updatedIntervention = interventionRepository.save(intervention);
        return mapper.toDTO(updatedIntervention);
    }
    @Override
    public List<InterventionDTO> getInterventionsByTechnicien(Long technicienId) {
        Personnel technicien = personnelRepository.findById(technicienId)
                .orElseThrow(() -> new BusinessException("Technicien introuvable", HttpStatus.NOT_FOUND));

        return interventionRepository.findByTechnicien(technicien)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }


}



