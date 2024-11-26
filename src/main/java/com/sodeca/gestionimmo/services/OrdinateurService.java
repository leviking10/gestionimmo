package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
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
     * Récupérer un ordinateur par ID.
     *
     * @param id ID de l'ordinateur.
     * @return Un Optional contenant le OrdinateurDTO si trouvé.
     */
    Optional<OrdinateurDTO> getOrdinateurById(Long id);

    /**
     * Créer un nouvel ordinateur.
     *
     * @param dto OrdinateurDTO à créer.
     * @return Le OrdinateurDTO créé.
     */
    OrdinateurDTO createOrdinateur(OrdinateurDTO dto);

    /**
     * Mettre à jour un ordinateur existant.
     *
     * @param id  ID de l'ordinateur à mettre à jour.
     * @param dto OrdinateurDTO contenant les nouvelles données.
     * @return Le OrdinateurDTO mis à jour.
     */
    OrdinateurDTO updateOrdinateur(Long id, OrdinateurDTO dto);

    /**
     * Supprimer un ordinateur par ID.
     *
     * @param id ID de l'ordinateur à supprimer.
     */
    void deleteOrdinateur(Long id);
/**
     * Créer une liste d'ordinateurs.
     *
     * @param dtos Liste des OrdinateurDTO à créer.
     * @return Liste des OrdinateurDTO créés.
     */
    List<OrdinateurDTO> createOrdinateurs(List<ImmobilisationDTO> dtos);
}
