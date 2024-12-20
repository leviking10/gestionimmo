package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.InterventionDTO;
import com.sodeca.gestionimmo.enums.StatutIntervention;

import java.util.List;

public interface InterventionService {
    InterventionDTO createIntervention(InterventionDTO interventionDTO);
    InterventionDTO updateIntervention(Long id, InterventionDTO interventionDTO);
    void deleteIntervention(Long id);
    InterventionDTO getInterventionById(Long id);
    List<InterventionDTO> getInterventionsByImmobilisation(Long immobilisationId);
    List<InterventionDTO> getInterventionsByTechnicien(Long technicienId);
    InterventionDTO commencerIntervention(Long id);
    InterventionDTO terminerIntervention(Long id, String rapport);
    List<InterventionDTO> getAllInterventions();

    InterventionDTO updateStatutIntervention(Long id, StatutIntervention statut);

}
