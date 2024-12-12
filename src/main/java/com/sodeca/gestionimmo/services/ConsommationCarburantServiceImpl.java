package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ConsommationCarburantDTO;
import com.sodeca.gestionimmo.entity.ConsommationCarburant;
import com.sodeca.gestionimmo.entity.Vehicule;
import com.sodeca.gestionimmo.mapper.ConsommationCarburantMapper;
import com.sodeca.gestionimmo.repository.ConsommationCarburantRepository;
import com.sodeca.gestionimmo.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsommationCarburantServiceImpl implements ConsommationCarburantService {

    private final ConsommationCarburantRepository repository;
    private final VehiculeRepository vehiculeRepository;
    private final ConsommationCarburantMapper mapper;

    public ConsommationCarburantServiceImpl(ConsommationCarburantRepository repository,
                                            VehiculeRepository vehiculeRepository,
                                            ConsommationCarburantMapper mapper) {
        this.repository = repository;
        this.vehiculeRepository = vehiculeRepository;
        this.mapper = mapper;
    }

    @Override
    public ConsommationCarburantDTO addConsommation(ConsommationCarburantDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        ConsommationCarburant consommation = mapper.toEntity(dto);
        consommation.setVehicule(vehicule);
        return mapper.toDTO(repository.save(consommation));
    }

    @Override
    public List<ConsommationCarburantDTO> getConsommationsByVehicule(Long vehiculeId) {
        return repository.findByVehiculeIdOrderByDateDesc(vehiculeId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ConsommationCarburantDTO> getHistoriqueConsommation(Long vehiculeId) {
        return getConsommationsByVehicule(vehiculeId);
    }
}
