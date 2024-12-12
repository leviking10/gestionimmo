package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.DemandePieceDTO;
import com.sodeca.gestionimmo.services.DemandePieceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
public class DemandePieceController {

    private final DemandePieceService service;

    public DemandePieceController(DemandePieceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DemandePieceDTO> createDemande(@RequestBody DemandePieceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDemande(dto));
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<DemandePieceDTO> validerDemande(@PathVariable Long id) {
        return ResponseEntity.ok(service.validerDemande(id));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<DemandePieceDTO> annulerDemande(@PathVariable Long id) {
        return ResponseEntity.ok(service.annulerDemande(id));
    }

    @GetMapping
    public ResponseEntity<List<DemandePieceDTO>> getAllDemandes() {
        return ResponseEntity.ok(service.getAllDemandes());
    }

    @GetMapping("/technicien/{technicienId}")
    public ResponseEntity<List<DemandePieceDTO>> getDemandesByTechnicien(@PathVariable Long technicienId) {
        return ResponseEntity.ok(service.getDemandesByTechnicien(technicienId));
    }

    @GetMapping("/piece/{pieceId}")
    public ResponseEntity<List<DemandePieceDTO>> getDemandesByPiece(@PathVariable Long pieceId) {
        return ResponseEntity.ok(service.getDemandesByPiece(pieceId));
    }

    @GetMapping("/non-validees")
    public ResponseEntity<List<DemandePieceDTO>> getDemandesNonValidees() {
        return ResponseEntity.ok(service.getDemandesNonValidees());
    }
}
