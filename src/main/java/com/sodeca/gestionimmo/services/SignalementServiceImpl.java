package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.SignalementDTO;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Signalement;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.mapper.SignalementMapper;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import com.sodeca.gestionimmo.repository.SignalementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SignalementServiceImpl implements SignalementService {

    private final SignalementRepository signalementRepository;
    private final PersonnelRepository personnelRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final SignalementMapper signalementMapper;

    public SignalementServiceImpl(SignalementRepository signalementRepository,
                                  PersonnelRepository personnelRepository,
                                  ImmobilisationRepository immobilisationRepository,
                                  SignalementMapper signalementMapper) {
        this.signalementRepository = signalementRepository;
        this.personnelRepository = personnelRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.signalementMapper = signalementMapper;
    }

    @Override
    public SignalementDTO createSignalement(SignalementDTO signalementDTO) {
        Personnel personnel = personnelRepository.findById(signalementDTO.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel introuvable"));

        Immobilisation immobilisation = immobilisationRepository.findById(signalementDTO.getImmobilisationId())
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        // Vérifier si l'immobilisation est déjà signalée en panne
        if (immobilisation.getEtatImmo() == EtatImmobilisation.EN_PANNE) {
            throw new RuntimeException("L'immobilisation est déjà signalée en panne.");
        }

        // Mettre à jour l'état de l'immobilisation à EN_PANNE
        immobilisation.setEtatImmo(EtatImmobilisation.EN_PANNE);
        immobilisationRepository.save(immobilisation);

        Signalement signalement = signalementMapper.toEntity(signalementDTO);
        signalement.setPersonnel(personnel);
        signalement.setImmobilisation(immobilisation);

        Signalement savedSignalement = signalementRepository.save(signalement);
        return signalementMapper.toDTO(savedSignalement);
    }

    @Override
    public SignalementDTO updateSignalement(Long signalementId, SignalementDTO signalementDTO) {
        Signalement signalement = signalementRepository.findById(signalementId)
                .orElseThrow(() -> new RuntimeException("Signalement introuvable avec l'ID : " + signalementId));

        Personnel personnel = personnelRepository.findById(signalementDTO.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel introuvable"));

        Immobilisation immobilisation = immobilisationRepository.findById(signalementDTO.getImmobilisationId())
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        signalement.setPersonnel(personnel);
        signalement.setImmobilisation(immobilisation);
        signalement.setDescription(signalementDTO.getDescription());

        Signalement updatedSignalement = signalementRepository.save(signalement);
        return signalementMapper.toDTO(updatedSignalement);
    }

    @Override
    public void deleteSignalement(Long signalementId) {
        if (!signalementRepository.existsById(signalementId)) {
            throw new RuntimeException("Signalement introuvable avec l'ID : " + signalementId);
        }
        signalementRepository.deleteById(signalementId);
    }

    @Override
    public List<SignalementDTO> getSignalementsByImmobilisation(Long immobilisationId) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        return signalementRepository.findByImmobilisation(immobilisation)
                .stream()
                .map(signalementMapper::toDTO)
                .toList();
    }

    @Override
    public List<SignalementDTO> getSignalementsByPersonnel(Long personnelId) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel introuvable"));

        return signalementRepository.findByPersonnel(personnel)
                .stream()
                .map(signalementMapper::toDTO)
                .toList();
    }

    @Override
    public SignalementDTO getSignalementById(Long signalementId) {
        Signalement signalement = signalementRepository.findById(signalementId)
                .orElseThrow(() -> new RuntimeException("Signalement introuvable avec l'ID : " + signalementId));
        return signalementMapper.toDTO(signalement);
    }

}
