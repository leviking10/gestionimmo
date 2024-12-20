package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.services.MouvementStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements-stock")
public class MouvementStockController {

    private final MouvementStockService service;

    public MouvementStockController(MouvementStockService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MouvementStockDTO>> getAllMouvements() {
        return ResponseEntity.ok(service.getAllMouvements());
    }

    @GetMapping("/piece/{pieceId}")
    public ResponseEntity<List<MouvementStockDTO>> getMouvementsByPiece(@PathVariable Long pieceId) {
        try {
            List<MouvementStockDTO> mouvements = service.getMouvementsByPiece(pieceId);
            return ResponseEntity.ok(mouvements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<List<MouvementStockDTO>> getMouvementsByReference(@PathVariable String reference) {
        try {
            List<MouvementStockDTO> mouvements = service.getMouvementsByReference(reference);
            return ResponseEntity.ok(mouvements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
