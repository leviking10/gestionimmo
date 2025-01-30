package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.LicenceDTO;

import java.util.List;

public interface LicenceService {

    LicenceDTO createLicence(LicenceDTO licenceDTO);

    LicenceDTO updateLicence(Long id, LicenceDTO licenceDTO);

    LicenceDTO getLicenceById(Long id);

    List<LicenceDTO> getAllLicences();

    void deleteLicence(Long id);

    List<LicenceDTO> getLicencesByNom(String nom);

    List<LicenceDTO> getLicencesByFournisseur(String fournisseur);

    List<LicenceDTO> getExpiredLicences();
}
