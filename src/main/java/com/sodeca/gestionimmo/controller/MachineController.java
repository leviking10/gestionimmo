package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.MachineDTO;
import com.sodeca.gestionimmo.services.MachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    /**
     * Récupérer toutes les machines.
     */
    @GetMapping
    public List<MachineDTO> getAllMachines() {
        return machineService.getAllMachines();
    }

    /**
     * Récupérer une machine par ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MachineDTO> getMachineById(@PathVariable Long id) {
        return machineService.getMachineById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mettre à jour les champs techniques d'une machine.
     */
    @PutMapping("/{id}/details")
    public ResponseEntity<MachineDTO> updateMachine(@PathVariable Long id, @RequestBody MachineDTO dto) {
        return ResponseEntity.ok(machineService.updateMachine(id, dto));
    }
}
