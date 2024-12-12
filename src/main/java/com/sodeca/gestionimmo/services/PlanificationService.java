package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.PlanificationDTO;
import java.util.List;

public interface PlanificationService {
    PlanificationDTO createPlanification(PlanificationDTO planificationDTO);

    PlanificationDTO updatePlanification(Long id, PlanificationDTO planificationDTO);

    void deletePlanification(Long id);

    PlanificationDTO getPlanificationById(Long id);

    List<PlanificationDTO> getPlanificationsByImmobilisation(Long immobilisationId);

    List<PlanificationDTO> getPlanificationsByTechnicien(Long technicienId);

    PlanificationDTO commencerPlanification(Long id);

    PlanificationDTO terminerPlanification(Long id, String rapport);
}
