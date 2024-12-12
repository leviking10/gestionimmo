package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.InterventionDTO;
import com.sodeca.gestionimmo.enums.StatutIntervention;
import com.sodeca.gestionimmo.services.InterventionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @PostMapping
    public ResponseEntity<InterventionDTO> createIntervention(@RequestBody InterventionDTO dto) {
        InterventionDTO createdIntervention = interventionService.createIntervention(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIntervention);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterventionDTO> updateIntervention(@PathVariable Long id, @RequestBody InterventionDTO dto) {
        InterventionDTO updatedIntervention = interventionService.updateIntervention(id, dto);
        return ResponseEntity.ok(updatedIntervention);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntervention(@PathVariable Long id) {
        interventionService.deleteIntervention(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionDTO> getInterventionById(@PathVariable Long id) {
        InterventionDTO intervention = interventionService.getInterventionById(id);
        return ResponseEntity.ok(intervention);
    }
    @PutMapping("/{id}/commencer")
    public ResponseEntity<InterventionDTO> commencerIntervention(@PathVariable Long id) {
        return ResponseEntity.ok(interventionService.commencerIntervention(id));
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<InterventionDTO> terminerIntervention(@PathVariable Long id, @RequestBody String rapport) {
        return ResponseEntity.ok(interventionService.terminerIntervention(id, rapport));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<InterventionDTO> updateStatut(@PathVariable Long id, @RequestParam StatutIntervention statut) {
        return ResponseEntity.ok(interventionService.updateStatutIntervention(id, statut));
    }

    @GetMapping("/immobilisation/{immobilisationId}")
    public ResponseEntity<List<InterventionDTO>> getInterventionsByImmobilisation(@PathVariable Long immobilisationId) {
        List<InterventionDTO> interventions = interventionService.getInterventionsByImmobilisation(immobilisationId);
        return ResponseEntity.ok(interventions);
    }

    @GetMapping("/technicien/{technicienId}")
    public ResponseEntity<List<InterventionDTO>> getInterventionsByTechnicien(@PathVariable Long technicienId) {
        List<InterventionDTO> interventions = interventionService.getInterventionsByTechnicien(technicienId);
        return ResponseEntity.ok(interventions);
    }
}
