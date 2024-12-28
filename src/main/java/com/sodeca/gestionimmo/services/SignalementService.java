package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.SignalementDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SignalementService {

    @Transactional
    void cancelSignalement(Long signalementId);

    SignalementDTO createSignalement(SignalementDTO signalementDTO);

    List<SignalementDTO> getAllSignalements();

    List<SignalementDTO> getSignalementsByDateRange(String startDate, String endDate);

    SignalementDTO markSignalementAsTraite(Long id);

    Long getSignalementCount();

    SignalementDTO updateSignalement(Long signalementId, SignalementDTO signalementDTO);

    void deleteSignalement(Long signalementId);

    List<SignalementDTO> getSignalementsByImmobilisation(Long immobilisationId);

    List<SignalementDTO> getSignalementsByPersonnel(Long personnelId);

    SignalementDTO getSignalementById(Long signalementId);
}
