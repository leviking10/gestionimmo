package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MachineDTO;
import com.sodeca.gestionimmo.entity.Machine;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;
    private final ImmobilisationMapper mapper;

    public MachineServiceImpl(MachineRepository machineRepository, ImmobilisationMapper mapper) {
        this.machineRepository = machineRepository;
        this.mapper = mapper;
    }

    @Override
    public List<MachineDTO> getAllMachines() {
        return machineRepository.findAll()
                .stream()
                .map(mapper::toMachineDTO)
                .toList();
    }

    @Override
    public Optional<MachineDTO> getMachineById(Long id) {
        return machineRepository.findById(id)
                .map(mapper::toMachineDTO);
    }

    @Override
    public MachineDTO updateMachine(Long id, MachineDTO dto) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine non trouvée avec l'ID : " + id));

        // Mise à jour des champs techniques spécifiques à Machine
        machine.setPuissance(dto.getPuissance());
        machine.setFabricant(dto.getFabricant());
        machine.setNumeroSerie(dto.getNumeroSerie());
        // Sauvegarde et retour
        Machine updated = machineRepository.save(machine);
        return mapper.toMachineDTO(updated);
    }
}
