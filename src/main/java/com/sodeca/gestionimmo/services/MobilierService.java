package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MobilierDTO;

import java.util.List;
import java.util.Optional;

public interface MobilierService {

    /**
     * Récupérer tous les mobiliers.
     */
    List<MobilierDTO> getAllMobilier();

    /**
     * Récupérer un mobilier par ID.
     */
    Optional<MobilierDTO> getMobilierById(Long id);

    /**
     * Mettre à jour les champs techniques d'un mobilier.
     */
    MobilierDTO updateMobilier(Long id, MobilierDTO dto);
}
