package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.SignalementDTO;

import java.util.List;

public interface SignalementService {

    SignalementDTO createSignalement(SignalementDTO signalementDTO);

    SignalementDTO updateSignalement(Long signalementId, SignalementDTO signalementDTO);

    void deleteSignalement(Long signalementId);

    List<SignalementDTO> getSignalementsByImmobilisation(Long immobilisationId);

    List<SignalementDTO> getSignalementsByPersonnel(Long personnelId);

    SignalementDTO getSignalementById(Long signalementId);
}
