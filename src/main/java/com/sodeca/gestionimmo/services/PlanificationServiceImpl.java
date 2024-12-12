package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.PlanificationDTO;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Planification;
import com.sodeca.gestionimmo.enums.StatutPlanification;
import com.sodeca.gestionimmo.mapper.PlanificationMapper;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import com.sodeca.gestionimmo.repository.PlanificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanificationServiceImpl implements PlanificationService {

    private final PlanificationRepository planificationRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final PersonnelRepository personnelRepository;
    private final PlanificationMapper planificationMapper;

    public PlanificationServiceImpl(PlanificationRepository planificationRepository,
                                    ImmobilisationRepository immobilisationRepository,
                                    PersonnelRepository personnelRepository,
                                    PlanificationMapper planificationMapper) {
        this.planificationRepository = planificationRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.personnelRepository = personnelRepository;
        this.planificationMapper = planificationMapper;
    }

    @Override
    public PlanificationDTO createPlanification(PlanificationDTO dto) {
        validateDatesOnCreate(dto);

        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + dto.getImmobilisationId()));

        List<Personnel> techniciens = mapTechnicienIdsToEntities(dto.getTechnicienIds());

        Planification planification = planificationMapper.toEntity(dto);
        planification.setImmobilisation(immobilisation);
        planification.setTechniciens(techniciens);
        planification.setStatut(StatutPlanification.PLANIFIEE);
        planification.setDateFin(null); // Date de fin non renseignée au départ

        Planification savedPlanification = planificationRepository.save(planification);
        return planificationMapper.toDTO(savedPlanification);
    }

    @Override
    public PlanificationDTO updatePlanification(Long id, PlanificationDTO dto) {
        validateDatesOnUpdate(dto);

        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planification introuvable avec l'ID : " + id));

        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + dto.getImmobilisationId()));

        List<Personnel> techniciens = mapTechnicienIdsToEntities(dto.getTechnicienIds());

        planification.setDescription(dto.getDescription());
        planification.setPriorite(dto.getPriorite());
        planification.setStatut(dto.getStatut());
        planification.setDateDebut(dto.getDateDebut());
        planification.setImmobilisation(immobilisation);
        planification.setTechniciens(techniciens);

        Planification updatedPlanification = planificationRepository.save(planification);
        return planificationMapper.toDTO(updatedPlanification);
    }

    @Override
    public PlanificationDTO getPlanificationById(Long id) {
        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planification introuvable avec l'ID : " + id));
        return planificationMapper.toDTO(planification);
    }

    @Override
    public List<PlanificationDTO> getPlanificationsByImmobilisation(Long immobilisationId) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + immobilisationId));

        return planificationRepository.findByImmobilisation(immobilisation).stream()
                .map(planificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanificationDTO> getPlanificationsByTechnicien(Long technicienId) {
        Personnel technicien = personnelRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien introuvable avec l'ID : " + technicienId));

        return planificationRepository.findByTechniciensContaining(technicien).stream()
                .map(planificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePlanification(Long id) {
        if (!planificationRepository.existsById(id)) {
            throw new RuntimeException("Planification introuvable avec l'ID : " + id);
        }
        planificationRepository.deleteById(id);
    }

    public PlanificationDTO commencerPlanification(Long id) {
        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planification introuvable avec l'ID : " + id));

        if (!planification.getStatut().equals(StatutPlanification.PLANIFIEE)) {
            throw new RuntimeException("La planification doit être dans l'état PLANIFIEE pour commencer.");
        }

        planification.setStatut(StatutPlanification.EN_COURS);
        Planification updatedPlanification = planificationRepository.save(planification);

        return planificationMapper.toDTO(updatedPlanification);
    }

    public PlanificationDTO terminerPlanification(Long id, String rapport) {
        Planification planification = planificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planification introuvable avec l'ID : " + id));

        if (!planification.getStatut().equals(StatutPlanification.EN_COURS)) {
            throw new RuntimeException("La planification doit être dans l'état EN_COURS pour être terminée.");
        }

        planification.setStatut(StatutPlanification.TERMINEE);
        planification.setDateFin(LocalDate.now()); // Mise à jour de la date de fin
        planification.setRapport(rapport); // Ajout du rapport

        Planification updatedPlanification = planificationRepository.save(planification);
        return planificationMapper.toDTO(updatedPlanification);
    }

    private void validateDatesOnCreate(PlanificationDTO dto) {
        if (dto.getDateFin() != null) {
            throw new RuntimeException("La date de fin ne doit pas être renseignée lors de la création de la planification.");
        }

        if (dto.getDateDebut() != null && dto.getDateDebut().isBefore(LocalDate.now())) {
            throw new RuntimeException("La date de début ne peut pas être dans le passé.");
        }
    }

    private void validateDatesOnUpdate(PlanificationDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null && dto.getDateDebut().isAfter(dto.getDateFin())) {
            throw new RuntimeException("La date de début ne peut pas être après la date de fin.");
        }
    }

    private List<Personnel> mapTechnicienIdsToEntities(List<Long> ids) {
        return ids.stream()
                .map(id -> personnelRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Technicien introuvable avec l'ID : " + id)))
                .collect(Collectors.toList());
    }
}
