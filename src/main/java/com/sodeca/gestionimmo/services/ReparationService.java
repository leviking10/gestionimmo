package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ReparationDTO;

import java.util.List;

public interface ReparationService {
    ReparationDTO addReparation(ReparationDTO dto);
    List<ReparationDTO> getReparationsByVehicule(Long vehiculeId);
    List<ReparationDTO> getHistoriqueReparations(Long vehiculeId); // Historique
}

