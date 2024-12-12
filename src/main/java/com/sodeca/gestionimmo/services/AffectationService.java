package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AffectationDTO;

import java.util.List;

public interface AffectationService {
    AffectationDTO createAffectation(AffectationDTO dto);
    AffectationDTO updateAffectation(Long id, AffectationDTO dto);
    void deleteAffectation(Long id);
    void retournerImmobilisation(Long immobilisationId);
    List<AffectationDTO> getAffectationsByPersonnel(Long personnelId);
    List<AffectationDTO> getHistoriqueByImmobilisation(Long immobilisationId);

    List<AffectationDTO> getAffectationsByImmobilisation(Long immobilisationId);

    List<AffectationDTO> getHistoriqueAffectations();
}
