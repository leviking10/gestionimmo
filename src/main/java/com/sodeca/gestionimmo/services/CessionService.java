package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.CessionDTO;
import com.sodeca.gestionimmo.enums.StatutCession;

import java.time.LocalDate;
import java.util.List;

public interface CessionService {
    CessionDTO createCession(CessionDTO cessionDTO);

    CessionDTO updateCession(Long cessionId, CessionDTO cessionDTO);

    void deleteCession(Long cessionId);

    List<CessionDTO> getCessionsByImmobilisation(Long immobilisationId);

    List<CessionDTO> getAllCessions();

    List<CessionDTO> getCessionsByStatut(StatutCession statut);

    CessionDTO getCessionById(Long cessionId);

    Long getCountByStatut(StatutCession statut);

    List<CessionDTO> getCessionsByDateRange(LocalDate startDate, LocalDate endDate);

    void annulerCession(Long immobilisationId);
}
