package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ReparationDTO;
import com.sodeca.gestionimmo.entity.Reparation;
import com.sodeca.gestionimmo.entity.Vehicule;
import com.sodeca.gestionimmo.mapper.ReparationMapper;
import com.sodeca.gestionimmo.repository.ReparationRepository;
import com.sodeca.gestionimmo.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReparationServiceImpl implements ReparationService {

    private final ReparationRepository repository;
    private final VehiculeRepository vehiculeRepository;
    private final ReparationMapper mapper;

    public ReparationServiceImpl(ReparationRepository repository,
                                 VehiculeRepository vehiculeRepository,
                                 ReparationMapper mapper) {
        this.repository = repository;
        this.vehiculeRepository = vehiculeRepository;
        this.mapper = mapper;
    }

    @Override
    public ReparationDTO addReparation(ReparationDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        Reparation reparation = mapper.toEntity(dto);
        reparation.setVehicule(vehicule);
        return mapper.toDTO(repository.save(reparation));
    }

    @Override
    public List<ReparationDTO> getReparationsByVehicule(Long vehiculeId) {
        return repository.findByVehiculeIdOrderByDateDesc(vehiculeId).stream()
                .map(mapper::toDTO)
                .toList();
    }
    @Override
    public List<ReparationDTO> getAllReparations() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ReparationDTO> getReparationsByType(String typeReparation) {
        return repository.findByTypeReparation(typeReparation).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ReparationDTO> getReparationsByFournisseur(String fournisseur) {
        return repository.findByFournisseur(fournisseur).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ReparationDTO> getReparationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateBetween(startDate, endDate).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ReparationDTO updateReparation(Long id, ReparationDTO dto) {
        Reparation reparation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réparation introuvable"));
        reparation.setDate(dto.getDate());
        reparation.setDescription(dto.getDescription());
        reparation.setCout(dto.getCout());
        reparation.setFournisseur(dto.getFournisseur());
        reparation.setKilometrage(dto.getKilometrage());
        reparation.setTypeReparation(dto.getTypeReparation());
        return mapper.toDTO(repository.save(reparation));
    }
    @Override
    public ReparationDTO getReparationById(Long id) {
        Reparation reparation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réparation introuvable avec l'ID : " + id));
        return mapper.toDTO(reparation);
    }

    @Override
    public void deleteReparation(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ReparationDTO> getHistoriqueReparations(Long vehiculeId) {
        return getReparationsByVehicule(vehiculeId);
    }
}
