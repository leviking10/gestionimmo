package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.*;

import java.util.List;
import java.util.Optional;

public interface ImmobilisationService {
    List<ImmobilisationDTO> getAllImmobilisations();

    ImmobilisationDTO createImmobilisation(ImmobilisationDTO dto);

    Optional<ImmobilisationDTO> getImmobilisationById(Long id);
    List<ImmobilisationDTO> createImmobilisations(List<ImmobilisationDTO> dtos);

    void deleteImmobilisation(Long id);

    ImmobilisationDTO updateImmobilisation(Long id, ImmobilisationDTO dto);
    // Méthodes pour les cessions

    List<ImmobilisationDTO> getImmobilisationsCedees();
    byte[] getQRCodeAsImage(Long id);
    byte[] downloadQRCode(Long id);

    ImmobilisationDTO getImmobilisationByQRCode(String qrCodeData);

}
