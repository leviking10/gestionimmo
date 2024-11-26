package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.dto.TelephoneDTO;

import java.util.List;
import java.util.Optional;

public interface TelephoneService {

    /**
     * Récupérer tous les téléphones.
     *
     * @return Liste des TelephoneDTO.
     */
    List<TelephoneDTO> getAllTelephones();

    /**
     * Récupérer un téléphone par ID.
     *
     * @param id ID du téléphone.
     * @return Un Optional contenant le TelephoneDTO si trouvé, sinon vide.
     */
    Optional<TelephoneDTO> getTelephoneById(Long id);

    /**
     * Créer un nouveau téléphone.
     *
     * @param dto TelephoneDTO à créer.
     * @return Le TelephoneDTO créé.
     */
    TelephoneDTO createTelephone(TelephoneDTO dto);

    /**
     * Mettre à jour un téléphone existant.
     *
     * @param id  ID du téléphone à mettre à jour.
     * @param dto TelephoneDTO contenant les nouvelles données.
     * @return Le TelephoneDTO mis à jour.
     */
    TelephoneDTO updateTelephone(Long id, TelephoneDTO dto);

    /**
     * Supprimer un téléphone par ID.
     *
     * @param id ID du téléphone à supprimer.
     */
    void deleteTelephone(Long id);

    List<TelephoneDTO> createTelephones(List<ImmobilisationDTO> dtos);
}
