package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MachineDTO;

import java.util.List;
import java.util.Optional;

public interface MachineService {

    /**
     * Récupérer toutes les machines.
     */
    List<MachineDTO> getAllMachines();

    /**
     * Récupérer une machine par ID.
     */
    Optional<MachineDTO> getMachineById(Long id);

    /**
     * Mettre à jour les champs techniques d'une machine.
     */
    MachineDTO updateMachine(Long id, MachineDTO dto);
}
