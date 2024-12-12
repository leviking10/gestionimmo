package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.ConsommationCarburantDTO;
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
}
