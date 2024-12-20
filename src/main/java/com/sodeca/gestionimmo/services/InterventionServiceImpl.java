package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.InterventionDTO;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Intervention;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.mapper.InterventionMapper;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import com.sodeca.gestionimmo.repository.InterventionRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InterventionServiceImpl implements InterventionService {

    private final InterventionRepository interventionRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final PersonnelRepository personnelRepository;
    private final InterventionMapper mapper;

    public InterventionServiceImpl(InterventionRepository interventionRepository,
                                   ImmobilisationRepository immobilisationRepository,
                                   PersonnelRepository personnelRepository,
                                   InterventionMapper mapper) {
        this.interventionRepository = interventionRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.personnelRepository = personnelRepository;
        this.mapper = mapper;
    }

    @Override
    public InterventionDTO createIntervention(InterventionDTO dto) {
        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        Personnel technicien = null;
        if (dto.getTechnicienId() != null) {
            technicien = personnelRepository.findById(dto.getTechnicienId())
                    .orElseThrow(() -> new RuntimeException("Technicien introuvable"));
        }

        Intervention intervention = mapper.toEntity(dto);
        intervention.setImmobilisation(immobilisation);
        intervention.setTechnicien(technicien);
        intervention.setStatut(StatutIntervention.EN_ATTENTE);

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
                .orElseThrow(() -> new RuntimeException("Intervention introuvable avec l'ID : " + id));

        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        Personnel technicien = null;
        if (dto.getTechnicienId() != null) {
            technicien = personnelRepository.findById(dto.getTechnicienId())
                    .orElseThrow(() -> new RuntimeException("Technicien introuvable"));
        }

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
    public void deleteIntervention(Long id) {
        if (!interventionRepository.existsById(id)) {
            throw new RuntimeException("Intervention introuvable avec l'ID : " + id);
        }
        interventionRepository.deleteById(id);
    }

    @Override
    public InterventionDTO getInterventionById(Long id) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention introuvable avec l'ID : " + id));
        return mapper.toDTO(intervention);
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
    public InterventionDTO terminerIntervention(Long id, String rapport) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention introuvable avec l'ID : " + id));

        if (!intervention.getStatut().equals(StatutIntervention.EN_COURS)) {
            throw new RuntimeException("L'intervention ne peut être terminée car elle n'est pas en cours.");
        }

        intervention.setStatut(StatutIntervention.TERMINEE);
        intervention.setRapport(rapport);

        // Mettre à jour l'état de l'immobilisation
        Immobilisation immobilisation = intervention.getImmobilisation();
        immobilisation.setEtatImmo(EtatImmobilisation.EN_SERVICE);
        immobilisationRepository.save(immobilisation);

        return mapper.toDTO(interventionRepository.save(intervention));
    }
    @Override
    public InterventionDTO updateStatutIntervention(Long id, StatutIntervention statut) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention introuvable avec l'ID : " + id));

        // Mettre à jour le statut de l'intervention
        intervention.setStatut(statut);

        // Mettre à jour l'état de l'immobilisation en fonction du statut de l'intervention
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
                break;
            default:
                throw new RuntimeException("Statut d'intervention inconnu : " + statut);
        }

        // Sauvegarder les modifications
        immobilisationRepository.save(immobilisation);
        interventionRepository.save(intervention);

        return mapper.toDTO(intervention);
    }

    @Override
    public InterventionDTO commencerIntervention(Long id) {
        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention introuvable avec l'ID : " + id));

        if (!intervention.getStatut().equals(StatutIntervention.EN_ATTENTE)) {
            throw new RuntimeException("L'intervention ne peut pas être démarrée car elle n'est pas en attente.");
        }

        intervention.setStatut(StatutIntervention.EN_COURS);
        intervention.setDateExecution(LocalDate.now());

        // Mettre à jour l'état de l'immobilisation
        Immobilisation immobilisation = intervention.getImmobilisation();
        immobilisation.setEtatImmo(EtatImmobilisation.EN_MAINTENANCE);
        immobilisationRepository.save(immobilisation);

        return mapper.toDTO(interventionRepository.save(intervention));
    }

    @Override
    public List<InterventionDTO> getInterventionsByTechnicien(Long technicienId) {
        Personnel technicien = personnelRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien introuvable"));

        return interventionRepository.findByTechnicien(technicien)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}

