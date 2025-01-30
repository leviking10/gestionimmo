package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.PlanificationDTO;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Intervention;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Planification;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.enums.StatutPlanification;
import com.sodeca.gestionimmo.enums.TypeIntervention;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.PlanificationMapper;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import com.sodeca.gestionimmo.repository.InterventionRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import com.sodeca.gestionimmo.repository.PlanificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlanificationServiceImpl implements PlanificationService {

    private final PlanificationRepository planificationRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final PersonnelRepository personnelRepository;
    private final InterventionRepository interventionRepository;
    private final PlanificationMapper planificationMapper;

    public PlanificationServiceImpl(PlanificationRepository planificationRepository,
                                    ImmobilisationRepository immobilisationRepository,
                                    PersonnelRepository personnelRepository, InterventionRepository interventionRepository,
                                    PlanificationMapper planificationMapper) {
        this.planificationRepository = planificationRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.personnelRepository = personnelRepository;
        this.interventionRepository = interventionRepository;
        this.planificationMapper = planificationMapper;
    }

    @Override
    public List<PlanificationDTO> getAllPlanifications() {
        return planificationRepository.findAll()
                .stream()
                .map(planificationMapper::toDTO)
                .toList();
    }

    @Override
    public PlanificationDTO createPlanification(PlanificationDTO dto) {
        validateDatesOnCreate(dto);

        // Récupérer l'immobilisation
        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new BusinessException("L'immobilisation introuvable avec l'ID : " + dto.getImmobilisationId(), HttpStatus.NOT_FOUND));

        // Récupérer les techniciens
        List<Personnel> techniciens = mapTechnicienIdsToEntities(dto.getTechnicienIds());

        // Créer la planification
        Planification planification = planificationMapper.toEntity(dto);
        planification.setImmobilisation(immobilisation);
        planification.setTechniciens(techniciens);
        planification.setStatut(StatutPlanification.PLANIFIE);
        planification.setDateFin(null);

        Planification savedPlanification = planificationRepository.save(planification);

        // Créer automatiquement une intervention associée
        createInterventionForPlanification(savedPlanification);

        return planificationMapper.toDTO(savedPlanification);
    }
    private void createInterventionForPlanification(Planification planification) {
        Intervention intervention = new Intervention();
        intervention.setPlanification(planification);
        intervention.setImmobilisation(planification.getImmobilisation());
        intervention.setStatut(StatutIntervention.EN_ATTENTE);
        intervention.setType(TypeIntervention.PREVENTIVE);
        intervention.setDescription("Intervention générée automatiquement pour la planification ID: " + planification.getId());
        intervention.setDatePlanification(planification.getDateDebut());
        interventionRepository.save(intervention);
    }


    @Override
    public PlanificationDTO updatePlanification(Long id, PlanificationDTO dto) {
        validateDatesOnUpdate(dto);

        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Planification introuvable ", HttpStatus.NOT_FOUND));

        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable avec l'ID : " + dto.getImmobilisationId(), HttpStatus.NOT_FOUND));

        List<Personnel> techniciens = mapTechnicienIdsToEntities(dto.getTechnicienIds());
        planification.setDescription(dto.getDescription());
        planification.setPriorite(dto.getPriorite());
        planification.setStatut(dto.getStatut());
        planification.setDateDebut(dto.getDateDebut());
        planification.setImmobilisation(immobilisation);
        planification.setTechniciens(techniciens);
       planification.setDateFin(dto.getDateFin());

        Planification updatedPlanification = planificationRepository.save(planification);
        return planificationMapper.toDTO(updatedPlanification);
    }

    @Override
    public PlanificationDTO getPlanificationById(Long id) {
        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Planification introuvable", HttpStatus.NOT_FOUND));
        return planificationMapper.toDTO(planification);
    }

    @Override
    public List<PlanificationDTO> getPlanificationsByImmobilisation(Long immobilisationId) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable avec l'ID : " + immobilisationId, HttpStatus.NOT_FOUND));

        return planificationRepository.findByImmobilisation(immobilisation)
                .stream()
                .map(planificationMapper::toDTO)
                .toList();
    }

    @Override
    public List<PlanificationDTO> getPlanificationsByTechnicien(Long technicienId) {
        Personnel technicien = personnelRepository.findById(technicienId)
                .orElseThrow(() -> new BusinessException("Technicien introuvable avec l'ID : " + technicienId, HttpStatus.NOT_FOUND));

        return planificationRepository.findByTechniciensContaining(technicien)
                .stream()
                .map(planificationMapper::toDTO)
                .toList();
    }

    @Override
    public void deletePlanification(Long id) {
        if (!planificationRepository.existsById(id)) {
            throw new BusinessException("Planification introuvable ", HttpStatus.NOT_FOUND);
        }
        planificationRepository.deleteById(id);
    }

    public PlanificationDTO commencerPlanification(Long id) {
        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Planification introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND));

        if (!planification.getStatut().equals(StatutPlanification.PLANIFIE)) {
            throw new BusinessException("La planification doit être dans l'état PLANIFIEE pour commencer.", HttpStatus.BAD_REQUEST);
        }

        planification.setStatut(StatutPlanification.EN_COURS);
        Planification updatedPlanification = planificationRepository.save(planification);

        return planificationMapper.toDTO(updatedPlanification);
    }

    public PlanificationDTO terminerPlanification(Long id, String rapport) {
        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Planification introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND));

        if (!planification.getStatut().equals(StatutPlanification.EN_COURS)) {
            throw new BusinessException("La planification doit être dans l'état EN_COURS pour être terminée.", HttpStatus.BAD_REQUEST);
        }

        planification.setStatut(StatutPlanification.TERMINE);
        planification.setDateFin(LocalDate.now());
        planification.setRapport(rapport);

        // Mise à jour des interventions associées
        List<Intervention> interventions = interventionRepository.findByPlanification(planification);
        for (Intervention intervention : interventions) {
            if (intervention.getStatut().equals(StatutIntervention.EN_ATTENTE) ||
                    intervention.getStatut().equals(StatutIntervention.EN_COURS)) {
                intervention.setStatut(StatutIntervention.TERMINEE);
                interventionRepository.save(intervention);
            }
        }

        // Mise à jour de l'état de l'immobilisation
        Immobilisation immobilisation = planification.getImmobilisation();
        if (immobilisation != null && immobilisation.getEtatImmo().equals(EtatImmobilisation.PLANIFIE)) {
            immobilisation.setEtatImmo(EtatImmobilisation.EN_SERVICE);
            immobilisationRepository.save(immobilisation);
        }

        Planification updatedPlanification = planificationRepository.save(planification);
        return planificationMapper.toDTO(updatedPlanification);
    }

    private void validateDatesOnCreate(PlanificationDTO dto) {
        if (dto.getDateFin() != null) {
            throw new BusinessException("La date de fin ne doit pas être renseignée lors de la création de la planification.", HttpStatus.BAD_REQUEST);
        }

        if (dto.getDateDebut() != null && dto.getDateDebut().isBefore(LocalDate.now())) {
            throw new BusinessException("La date de début ne peut pas être dans le passé.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateDatesOnUpdate(PlanificationDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null && dto.getDateDebut().isAfter(dto.getDateFin())) {
            throw new BusinessException("La date de début ne peut pas être après la date de fin.", HttpStatus.BAD_REQUEST);
        }
    }

    private List<Personnel> mapTechnicienIdsToEntities(List<Long> ids) {
        return ids.stream()
                .map(id -> personnelRepository.findById(id)
                        .orElseThrow(() -> new BusinessException("Technicien introuvable avec l'ID : " + id, HttpStatus.NOT_FOUND)))
                .toList();
    }
}
