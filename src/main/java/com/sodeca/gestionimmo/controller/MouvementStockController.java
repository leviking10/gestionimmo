package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.enums.TypeMouvement;
import com.sodeca.gestionimmo.services.MouvementStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mouvements-stock")
public class MouvementStockController {

    private final MouvementStockService service;

    public MouvementStockController(MouvementStockService service) {
        this.service = service;
    }

    /**
     * Récupérer tous les mouvements de stock
     */
    @GetMapping
    public ResponseEntity<List<MouvementStockDTO>> getAllMouvements() {
        return ResponseEntity.ok(service.getAllMouvements());
    }

    /**
     * Récupérer les mouvements d'une pièce par ID
     */
    @GetMapping("/piece/{pieceId}")
    public ResponseEntity<List<MouvementStockDTO>> getMouvementsByPiece(@PathVariable Long pieceId) {
        try {
            List<MouvementStockDTO> mouvements = service.getMouvementsByPiece(pieceId);
            return ResponseEntity.ok(mouvements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Récupérer les mouvements d'une pièce par référence
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<List<MouvementStockDTO>> getMouvementsByReference(@PathVariable String reference) {
        try {
            List<MouvementStockDTO> mouvements = service.getMouvementsByReference(reference);
            return ResponseEntity.ok(mouvements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Créer un mouvement de stock
     */
    @PostMapping
    public ResponseEntity<MouvementStockDTO> createMouvement(@RequestBody MouvementStockDTO mouvementDTO) {
        try {
            MouvementStockDTO createdMouvement = service.createMouvement(mouvementDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMouvement);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Recherche avancée des mouvements
     */
    @GetMapping("/search")
    public ResponseEntity<List<MouvementStockDTO>> searchMouvements(
            @RequestParam(required = false) TypeMouvement type,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            List<MouvementStockDTO> mouvements = service.searchMouvements(type, startDate, endDate);
            return ResponseEntity.ok(mouvements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
