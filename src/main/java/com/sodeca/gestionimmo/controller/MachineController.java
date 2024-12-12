package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.MachineDTO;
import com.sodeca.gestionimmo.dto.OrdinateurDTO;
import com.sodeca.gestionimmo.services.MachineService;
import org.springframework.http.HttpStatus;
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
     *
     * @return Liste des MachineDTO.
     */
    @GetMapping
    public List<MachineDTO> getAllMachines() {
        return machineService.getAllMachines();
    }

    /**
     * Récupérer une machine par ID.
     *
     * @param id ID de la machine.
     * @return La machine si trouvée, sinon 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MachineDTO> getMachineById(@PathVariable Long id) {
        return machineService.getMachineById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer une nouvelle machine.
     *
     * @param dto MachineDTO à créer.
     * @return La machine créée avec un statut 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<MachineDTO> createMachine(@RequestBody MachineDTO dto) {
        MachineDTO createdMachine = machineService.createMachine(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMachine);
    }

    /**
     * Mettre à jour une machine existante.
     *
     * @param id  ID de la machine à mettre à jour.
     * @param dto MachineDTO contenant les nouvelles données.
     * @return La machine mise à jour ou 404 si non trouvée.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MachineDTO> updateMachine(@PathVariable Long id, @RequestBody MachineDTO dto) {
        try {
            MachineDTO updatedMachine = machineService.updateMachine(id, dto);
            return ResponseEntity.ok(updatedMachine);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer une machine par ID.
     *
     * @param id ID de la machine à supprimer.
     * @return Une réponse avec le statut 204 (NO CONTENT) si réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable Long id) {
        try {
            machineService.deleteMachine(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<MachineDTO>> createMachines(@RequestBody List<MachineDTO> dtos) {
        List<MachineDTO> createMachines = dtos.stream()
                .map(machineService::createMachine)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(createMachines);
    }
}
