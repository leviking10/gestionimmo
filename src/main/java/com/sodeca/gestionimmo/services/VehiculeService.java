package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.VehiculeDTO;

import java.util.List;
import java.util.Optional;

public interface VehiculeService {
    /**
     * Récupérer tous les véhicules.
     */
    List<VehiculeDTO> getAllVehicules();

    /**
     * Récupérer un véhicule par ID.
     */
    Optional<VehiculeDTO> getVehiculeById(Long id);

    /**
     * Mettre à jour les champs techniques d'un véhicule.
     */
    VehiculeDTO updateVehicule(Long id, VehiculeDTO dto);
}
