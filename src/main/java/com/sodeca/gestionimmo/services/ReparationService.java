package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ReparationDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReparationService {
    ReparationDTO addReparation(ReparationDTO dto);
    List<ReparationDTO> getReparationsByVehicule(Long vehiculeId);

    List<ReparationDTO> getAllReparations();

    List<ReparationDTO> getReparationsByType(String typeReparation);

    List<ReparationDTO> getReparationsByFournisseur(String fournisseur);

    List<ReparationDTO> getReparationsByDateRange(LocalDate startDate, LocalDate endDate);

    ReparationDTO updateReparation(Long id, ReparationDTO dto);

    ReparationDTO getReparationById(Long id);

    void deleteReparation(Long id);

    List<ReparationDTO> getHistoriqueReparations(Long vehiculeId); // Historique
}

