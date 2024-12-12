package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.ReparationDTO;
import com.sodeca.gestionimmo.services.ReparationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
