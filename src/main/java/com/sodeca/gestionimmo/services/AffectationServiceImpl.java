package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AffectationDTO;
import com.sodeca.gestionimmo.entity.Affectation;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.enums.StatutAffectation;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.AffectationMapper;
import com.sodeca.gestionimmo.repository.AffectationRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffectationServiceImpl implements AffectationService {

    private final AffectationRepository affectationRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final PersonnelRepository personnelRepository;
    private final AffectationMapper mapper;

    public AffectationServiceImpl(AffectationRepository affectationRepository,
                                  ImmobilisationRepository immobilisationRepository,
                                  PersonnelRepository personnelRepository,
                                  AffectationMapper mapper) {
        this.affectationRepository = affectationRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.personnelRepository = personnelRepository;
        this.mapper = mapper;
    }
    @Override
    public AffectationDTO updateAffectation(Long id, AffectationDTO dto) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation introuvable avec l'ID : " + id));

        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable  : "));

        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel introuvable avec l'ID : " + dto.getPersonnelId()));

        affectation.setImmobilisation(immobilisation);
        affectation.setPersonnel(personnel);
        affectation.setDateAffectation(dto.getDateAffectation());
        affectation.setDateRetour(dto.getDateRetour());
        Affectation updated = affectationRepository.save(affectation);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteAffectation(Long id) {
        affectationRepository.deleteById(id);
    }
    @Override
    public List<AffectationDTO> getHistoriqueByImmobilisation(Long immobilisationId) {
        return affectationRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<AffectationDTO> getAffectationsByPersonnel(Long personnelId) {
        return affectationRepository.findByPersonnelId(personnelId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<AffectationDTO> getAffectationsByImmobilisation(Long immobilisationId) {
        return affectationRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<AffectationDTO> getHistoriqueAffectations() {
        return affectationRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public AffectationDTO createAffectation(AffectationDTO dto) {
        Immobilisation immobilisation = immobilisationRepository.findById(dto.getImmobilisationId())
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable avec l'ID : " + dto.getImmobilisationId()));

        // Vérifier si l'immobilisation est disponible
        if (immobilisation.getAffectation() == StatutAffectation.AFFECTE) {
            throw new BusinessException("L'immobilisation est déjà affectée. Veuillez la libérer avant de la réaffecter.");
        }

        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new BusinessException("Personnel introuvable avec l'ID : " + dto.getPersonnelId()));

        Affectation affectation = mapper.toEntity(dto);
        affectation.setImmobilisation(immobilisation);
        affectation.setPersonnel(personnel);
        affectation.setDateAffectation(dto.getDateAffectation());
        // Marquer l'immobilisation comme affectée
        immobilisation.setAffectation(StatutAffectation.AFFECTE);
        immobilisationRepository.save(immobilisation);

        Affectation saved = affectationRepository.save(affectation);
        return mapper.toDTO(saved);
    }

    @Override
    public void retournerImmobilisation(Long immobilisationId) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + immobilisationId));

        // Vérifier si l'immobilisation est actuellement affectée
        if (immobilisation.getAffectation() == StatutAffectation.DISPONIBLE) {
            throw new BusinessException("L'immobilisation est déjà disponible.");
        }
        // Libérer l'immobilisation
        immobilisation.setAffectation(StatutAffectation.DISPONIBLE);
        immobilisationRepository.save(immobilisation);
    }
}

