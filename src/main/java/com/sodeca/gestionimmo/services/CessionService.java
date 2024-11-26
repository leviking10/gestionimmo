package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.CessionDTO;

import java.util.List;

public interface CessionService {
    CessionDTO createCession(CessionDTO cessionDTO);

    CessionDTO updateCession(Long cessionId, CessionDTO cessionDTO);

    void deleteCession(Long cessionId);

    List<CessionDTO> getCessionsByImmobilisation(Long immobilisationId);

    void annulerCession(Long immobilisationId);
}
