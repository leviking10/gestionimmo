package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.OrdinateurDTO;
import com.sodeca.gestionimmo.entity.Ordinateur;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.OrdinateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdinateurServiceImpl implements OrdinateurService {

    private final OrdinateurRepository ordinateurRepository;
    private final ImmobilisationMapper mapper;

    public OrdinateurServiceImpl(OrdinateurRepository ordinateurRepository, ImmobilisationMapper mapper) {
        this.ordinateurRepository = ordinateurRepository;
        this.mapper = mapper;
    }

    /**
     * Récupérer tous les ordinateurs.
     */
    @Override
    public List<OrdinateurDTO> getAllOrdinateurs() {
        return ordinateurRepository.findAll()
                .stream()
                .map(mapper::toOrdinateurDTO)
                .toList();
    }

    /**
     * Récupérer un ordinateur par ID.
     */
    @Override
    public Optional<OrdinateurDTO> getOrdinateurById(Long id) {
        return ordinateurRepository.findById(id)
                .map(mapper::toOrdinateurDTO);
    }

    /**
     * Mettre à jour les champs techniques spécifiques d'un ordinateur.
     */
    @Override
    public OrdinateurDTO updateOrdinateur(Long id, OrdinateurDTO dto) {
        // Récupérer l'ordinateur existant
        Ordinateur existingOrdinateur = ordinateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordinateur non trouvé avec l'ID : " + id));

        // Mettre à jour les champs spécifiques
        existingOrdinateur.setMarque(dto.getMarque());
        existingOrdinateur.setModele(dto.getModele());
        existingOrdinateur.setProcesseur(dto.getProcesseur());
        existingOrdinateur.setRam(dto.getRam());
        existingOrdinateur.setDisqueDur(dto.getDisqueDur());
        existingOrdinateur.setOs(dto.getOs());
        existingOrdinateur.setEtat(dto.getEtat());
        existingOrdinateur.setNumeroSerie(dto.getNumeroSerie());

        // Sauvegarder et retourner le DTO mis à jour
        Ordinateur updatedOrdinateur = ordinateurRepository.save(existingOrdinateur);
        return mapper.toOrdinateurDTO(updatedOrdinateur);
    }

    /**
     * Récupérer une liste d'ordinateurs pour des besoins spécifiques (par exemple, rapports).
     */
    @Override
    public List<OrdinateurDTO> getOrdinateursByCriteria() {
        return ordinateurRepository.findAll()
                .stream()
                .map(mapper::toOrdinateurDTO)
                .toList();
    }
}
