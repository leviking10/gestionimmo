package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.PieceDetacheeDTO;
import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.dto.InventaireDTO;
import com.sodeca.gestionimmo.services.PieceDetacheeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pieces-detachees")
public class PieceDetacheeController {

    private final PieceDetacheeService service;

    public PieceDetacheeController(PieceDetacheeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PieceDetacheeDTO> createPiece(@RequestBody PieceDetacheeDTO dto) {
        return ResponseEntity.ok(service.createPiece(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PieceDetacheeDTO> updatePiece(@PathVariable Long id, @RequestBody PieceDetacheeDTO dto) {
        return ResponseEntity.ok(service.updatePiece(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePiece(@PathVariable Long id) {
        service.deletePiece(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approvisionnement")
    public ResponseEntity<MouvementStockDTO> approvisionnerPiece(
            @PathVariable Long id,
            @RequestParam int quantite,
            @RequestParam(required = false) String commentaire) {
        MouvementStockDTO mouvementDTO = service.approvisionnement(id, quantite, commentaire);
        return ResponseEntity.status(HttpStatus.CREATED).body(mouvementDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PieceDetacheeDTO> getPieceById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPieceById(id));
    }

    @GetMapping
    public ResponseEntity<List<PieceDetacheeDTO>> getAllPieces() {
        return ResponseEntity.ok(service.getAllPieces());
    }

    @GetMapping("/inventaire")
    public ResponseEntity<List<InventaireDTO>> getInventaire() {
        return ResponseEntity.ok(service.getInventaire());
    }

    @GetMapping("/{id}/mouvements")
    public ResponseEntity<List<MouvementStockDTO>> getMouvementsByPiece(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMouvementsByPiece(id));
    }

    @PostMapping(value = "/upload-pieces", consumes = "multipart/form-data")
    public ResponseEntity<List<PieceDetacheeDTO>> importPieces(@RequestParam("file") MultipartFile file) {
        try {
            List<PieceDetacheeDTO> importedPieces = service.importPiecesFromFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(importedPieces);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping(value = "/upload-approvisionnements", consumes = "multipart/form-data")
    public ResponseEntity<List<MouvementStockDTO>> importApprovisionnements(@RequestParam("file") MultipartFile file) {
        try {
            List<MouvementStockDTO> importedAppro = service.importApprovisionnements(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(importedAppro);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
