package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    ImmobilisationDTO getImmobilisationByCode(String codeImmo);
    byte[] getQRCodeAsImage(Long id);

    byte[] downloadQRCode(Long id);

    List<ImmobilisationDTO> getImmobilisationsCedees();

    ImmobilisationDTO getImmobilisationByQRCode(String qrCodeData);
    void updateEtat(Long immobilisationId, EtatImmobilisation nouvelEtat);

    List<ImmobilisationDTO> importImmobilisationsFromFile(MultipartFile file) throws IOException;
}
