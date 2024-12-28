package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.PlanificationDTO;
import com.sodeca.gestionimmo.services.PlanificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planifications")
public class PlanificationController {

    private final PlanificationService planificationService;

    public PlanificationController(PlanificationService planificationService) {
        this.planificationService = planificationService;
    }

    @PostMapping
    public ResponseEntity<PlanificationDTO> createPlanification(@RequestBody PlanificationDTO dto) {
        PlanificationDTO createdPlanification = planificationService.createPlanification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlanification);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanificationDTO> updatePlanification(@PathVariable Long id, @RequestBody PlanificationDTO dto) {
        PlanificationDTO updatedPlanification = planificationService.updatePlanification(id, dto);
        return ResponseEntity.ok(updatedPlanification);
    }
    @GetMapping
    public ResponseEntity<List<PlanificationDTO>> getAllPlanifications() {
        // Appelle la méthode du service pour récupérer toutes les planifications
        List<PlanificationDTO> planifications = planificationService.getAllPlanifications();
        return ResponseEntity.ok(planifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanificationDTO> getPlanificationById(@PathVariable Long id) {
        return ResponseEntity.ok(planificationService.getPlanificationById(id));
    }

    @GetMapping("/immobilisation/{immobilisationId}")
    public ResponseEntity<List<PlanificationDTO>> getPlanificationsByImmobilisation(@PathVariable Long immobilisationId) {
        return ResponseEntity.ok(planificationService.getPlanificationsByImmobilisation(immobilisationId));
    }

    @GetMapping("/technicien/{technicienId}")
    public ResponseEntity<List<PlanificationDTO>> getPlanificationsByTechnicien(@PathVariable Long technicienId) {
        return ResponseEntity.ok(planificationService.getPlanificationsByTechnicien(technicienId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanification(@PathVariable Long id) {
        planificationService.deletePlanification(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<PlanificationDTO> commencerPlanification(@PathVariable Long id) {
        PlanificationDTO startedPlanification = planificationService.commencerPlanification(id);
        return ResponseEntity.ok(startedPlanification);
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<PlanificationDTO> terminerPlanification(@PathVariable Long id, @RequestParam String rapport) {
        PlanificationDTO finishedPlanification = planificationService.terminerPlanification(id, rapport);
        return ResponseEntity.ok(finishedPlanification);
    }
}
