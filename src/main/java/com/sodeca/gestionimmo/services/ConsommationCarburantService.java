package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ConsommationCarburantDTO;

import java.util.List;

public interface ConsommationCarburantService {
    ConsommationCarburantDTO addConsommation(ConsommationCarburantDTO dto);
    List<ConsommationCarburantDTO> getConsommationsByVehicule(Long vehiculeId);
    List<ConsommationCarburantDTO> getHistoriqueConsommation(Long vehiculeId); // Historique
}

