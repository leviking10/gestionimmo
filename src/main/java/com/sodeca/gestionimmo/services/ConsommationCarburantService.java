package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ConsommationCarburantDTO;
import com.sodeca.gestionimmo.dto.ConsommationResumeDTO;

import java.util.List;

public interface ConsommationCarburantService {
    ConsommationCarburantDTO addConsommation(ConsommationCarburantDTO dto);
    List<ConsommationCarburantDTO> getConsommationsByVehicule(Long vehiculeId);
    List<ConsommationCarburantDTO> getHistoriqueConsommation(Long vehiculeId); // Historique

    Double getConsommationMoyenne(Long vehiculeId, String startDate, String endDate);

    Double getCoutTotalConsommation(Long vehiculeId, String startDate, String endDate);

    List<ConsommationResumeDTO> getResumeConsommation(String startDate, String endDate);

    List<ConsommationCarburantDTO> getAllConsommations();
}

