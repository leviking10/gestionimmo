package com.sodeca.gestionimmo.services;
import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.dto.MachineDTO;

import java.util.List;
import java.util.Optional;

public interface MachineService {

    List<MachineDTO> getAllMachines();

    Optional<MachineDTO> getMachineById(Long id);

    MachineDTO createMachine(MachineDTO dto);

    MachineDTO updateMachine(Long id, MachineDTO dto);

    void deleteMachine(Long id);

    List<MachineDTO> createMachines(List<ImmobilisationDTO> dtos);
}
