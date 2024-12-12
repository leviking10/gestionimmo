package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MobilierDTO;

import java.util.List;
import java.util.Optional;

public interface MobilierService {

    /**
     * Récupérer tous les mobiliers.
     *
     * @return Liste des MobilierDTO.
     */
    List<MobilierDTO> getAllMobilier();

    /**
     * Récupérer un mobilier par ID.
     *
     * @param id ID du mobilier.
     * @return MobilierDTO si trouvé.
     */
    Optional<MobilierDTO> getMobilierById(Long id);

    /**
     * Créer un nouveau mobilier.
     *
     * @param dto MobilierDTO à créer.
     * @return MobilierDTO créé.
     */
    MobilierDTO createMobilier(MobilierDTO dto);

    /**
     * Mettre à jour un mobilier existant.
     *
     * @param id  ID du mobilier.
     * @param dto MobilierDTO avec les nouvelles données.
     * @return MobilierDTO mis à jour.
     */
    MobilierDTO updateMobilier(Long id, MobilierDTO dto);

    /**
     * Supprimer un mobilier par ID.
     *
     * @param id ID du mobilier.
     */
    void deleteMobilier(Long id);

    /**
     * Créer plusieurs mobiliers en une seule opération.
     *
     * @param dtos Liste de MobilierDTO à créer.
     * @return Liste des MobilierDTO créés.
     */
    List<MobilierDTO> createMobiliers(List<MobilierDTO> dtos);
}
