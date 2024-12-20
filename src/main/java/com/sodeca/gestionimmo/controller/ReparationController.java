package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.ReparationDTO;
import com.sodeca.gestionimmo.services.ReparationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reparations")
public class ReparationController {

    private final ReparationService service;

    public ReparationController(ReparationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReparationDTO> addReparation(@RequestBody ReparationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addReparation(dto));
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<List<ReparationDTO>> getHistorique(@PathVariable Long vehiculeId) {
        return ResponseEntity.ok(service.getHistoriqueReparations(vehiculeId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReparationDTO> getReparationById(@PathVariable Long id) {
        ReparationDTO reparation = service.getReparationById(id);
        return ResponseEntity.ok(reparation);
    }

    @GetMapping
    public ResponseEntity<List<ReparationDTO>> getAllReparations() {
        return ResponseEntity.ok(service.getAllReparations());
    }
    @GetMapping("/type")
    public ResponseEntity<List<ReparationDTO>> getReparationsByType(@RequestParam String typeReparation) {
        return ResponseEntity.ok(service.getReparationsByType(typeReparation));
    }
    @GetMapping("/fournisseur")
    public ResponseEntity<List<ReparationDTO>> getReparationsByFournisseur(@RequestParam String fournisseur) {
        return ResponseEntity.ok(service.getReparationsByFournisseur(fournisseur));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ReparationDTO> updateReparation(@PathVariable Long id, @RequestBody ReparationDTO dto) {
        return ResponseEntity.ok(service.updateReparation(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReparation(@PathVariable Long id) {
        service.deleteReparation(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/dates")
    public ResponseEntity<List<ReparationDTO>> getReparationsByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(service.getReparationsByDateRange(startDate, endDate));
    }

}
