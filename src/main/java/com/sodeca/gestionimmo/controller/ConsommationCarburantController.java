package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.ConsommationCarburantDTO;
import com.sodeca.gestionimmo.dto.ConsommationResumeDTO;
import com.sodeca.gestionimmo.services.ConsommationCarburantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consommation-carburant")
public class ConsommationCarburantController {

    private final ConsommationCarburantService service;

    public ConsommationCarburantController(ConsommationCarburantService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConsommationCarburantDTO> addConsommation(@RequestBody ConsommationCarburantDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addConsommation(dto));
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<List<ConsommationCarburantDTO>> getHistorique(@PathVariable Long vehiculeId) {
        return ResponseEntity.ok(service.getHistoriqueConsommation(vehiculeId));
    }
    @GetMapping("/all")
    public ResponseEntity<List<ConsommationCarburantDTO>> getAllConsommations() {
        return ResponseEntity.ok(service.getAllConsommations());
    }
    @GetMapping("/vehicule/{vehiculeId}/moyenne")
    public ResponseEntity<Double> getConsommationMoyenne(
            @PathVariable Long vehiculeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(service.getConsommationMoyenne(vehiculeId, startDate, endDate));
    }

    @GetMapping("/vehicule/{vehiculeId}/cout-total")
    public ResponseEntity<Double> getCoutTotalConsommation(
            @PathVariable Long vehiculeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(service.getCoutTotalConsommation(vehiculeId, startDate, endDate));
    }

    @GetMapping("/resume")
    public ResponseEntity<List<ConsommationResumeDTO>> getResumeConsommation(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(service.getResumeConsommation(startDate, endDate));
    }
}

