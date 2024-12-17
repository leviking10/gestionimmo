package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.TelephoneDTO;

import java.util.List;
import java.util.Optional;

public interface TelephoneService {

    /**
     * Récupérer tous les téléphones.
     */
    List<TelephoneDTO> getAllTelephones();

    /**
     * Récupérer un téléphone par ID.
     */
    Optional<TelephoneDTO> getTelephoneById(Long id);

    /**
     * Mettre à jour les champs techniques d'un téléphone.
     */
    TelephoneDTO updateTelephone(Long id, TelephoneDTO dto);
}
