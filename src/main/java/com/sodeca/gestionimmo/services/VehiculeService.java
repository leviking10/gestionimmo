package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.VehiculeDTO;

import java.util.List;
import java.util.Optional;

public interface VehiculeService {

    /**
     * Récupérer tous les véhicules.
     *
     * @return Liste des VehiculeDTO.
     */
    List<VehiculeDTO> getAllVehicules();

    /**
     * Récupérer un véhicule par ID.
     *
     * @param id ID du véhicule.
     * @return Un Optional contenant le VehiculeDTO si trouvé.
     */
    Optional<VehiculeDTO> getVehiculeById(Long id);

    /**
     * Créer un nouveau véhicule.
     *
     * @param dto VehiculeDTO à créer.
     * @return Le VehiculeDTO créé.
     */
    VehiculeDTO createVehicule(VehiculeDTO dto);

    /**
     * Mettre à jour un véhicule existant.
     *
     * @param id  ID du véhicule à mettre à jour.
     * @param dto VehiculeDTO contenant les nouvelles données.
     * @return Le VehiculeDTO mis à jour.
     */
    VehiculeDTO updateVehicule(Long id, VehiculeDTO dto);

    /**
     * Supprimer un véhicule par ID.
     *
     * @param id ID du véhicule à supprimer.
     */
    void deleteVehicule(Long id);
}
