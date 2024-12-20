package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.VehiculeDTO;
import com.sodeca.gestionimmo.entity.Vehicule;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final ImmobilisationMapper mapper;

    public VehiculeServiceImpl(VehiculeRepository vehiculeRepository, ImmobilisationMapper mapper) {
        this.vehiculeRepository = vehiculeRepository;
        this.mapper = mapper;
    }

    @Override
    public List<VehiculeDTO> getAllVehicules() {
        return vehiculeRepository.findAll()
                .stream()
                .map(mapper::toVehiculeDTO)
                .toList();
    }

    @Override
    public Optional<VehiculeDTO> getVehiculeById(Long id) {
        return vehiculeRepository.findById(id)
                .map(mapper::toVehiculeDTO);
    }

    @Override
    public VehiculeDTO updateVehicule(Long id, VehiculeDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID : " + id));
        // Mise à jour des champs techniques
        vehicule.setImmatriculation(dto.getImmatriculation());
        vehicule.setMarque(dto.getMarque());
        vehicule.setModele(dto.getModele());
        vehicule.setKilometrage(dto.getKilometrage());
        vehicule.setDateDerniereRevision(dto.getDateDerniereRevision());
        Vehicule updated = vehiculeRepository.save(vehicule);
        return mapper.toVehiculeDTO(updated);
    }
}
