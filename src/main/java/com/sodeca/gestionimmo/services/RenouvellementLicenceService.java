package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.RenouvellementLicenceDTO;

import java.time.LocalDate;
import java.util.List;

public interface RenouvellementLicenceService {

    RenouvellementLicenceDTO createRenouvellement(RenouvellementLicenceDTO renouvellementLicenceDTO);

    List<RenouvellementLicenceDTO> getRenouvellementsByLicenceId(Long licenceId);

    void deleteRenouvellement(Long id);
}
