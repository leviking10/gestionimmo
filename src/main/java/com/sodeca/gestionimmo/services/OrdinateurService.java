package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.OrdinateurDTO;

import java.util.List;
import java.util.Optional;

public interface OrdinateurService {

    /**
     * Récupérer tous les ordinateurs.
     *
     * @return Liste des OrdinateurDTO.
     */
    List<OrdinateurDTO> getAllOrdinateurs();

    /**
     * Récupérer un ordinateur par son identifiant.
     *
     * @param id Identifiant de l'ordinateur.
     * @return OrdinateurDTO s'il existe.
     */
    Optional<OrdinateurDTO> getOrdinateurById(Long id);

    /**
     * Mettre à jour les détails techniques d'un ordinateur.
     *
     * @param id  Identifiant de l'ordinateur.
     * @param dto DTO contenant les informations techniques à mettre à jour.
     * @return OrdinateurDTO mis à jour.
     */
    OrdinateurDTO updateOrdinateur(Long id, OrdinateurDTO dto);

    /**
     * Récupérer une liste d'ordinateurs selon des critères spécifiques.
     *
     * @return Liste des OrdinateurDTO.
     */
    List<OrdinateurDTO> getOrdinateursByCriteria();
}
