package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.DotationDTO;
import com.sodeca.gestionimmo.services.DotationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dotations")
public class DotationController {

    private final DotationService dotationService;

    public DotationController(DotationService dotationService) {
        this.dotationService = dotationService;
    }

    /**
     * Créer une nouvelle dotation.
     *
     * @param dto DotationDTO contenant les informations nécessaires.
     * @return La dotation créée.
     */
    @PostMapping
    public ResponseEntity<DotationDTO> createDotation(@RequestBody DotationDTO dto) {
        try {
            DotationDTO createdDotation = dotationService.createDotation(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDotation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Récupérer toutes les dotations.
     *
     * @return Liste de toutes les dotations.
     */
    @GetMapping
    public ResponseEntity<List<DotationDTO>> getAllDotations() {
        List<DotationDTO> dotations = dotationService.getAllDotations();
        return ResponseEntity.ok(dotations);
    }

    /**
     * Récupérer les dotations par pièce détachée.
     *
     * @param pieceId L'ID de la pièce détachée.
     * @return Liste des dotations pour cette pièce.
     */
    @GetMapping("/piece/{pieceId}")
    public ResponseEntity<List<DotationDTO>> getDotationsByPiece(@PathVariable Long pieceId) {
        try {
            List<DotationDTO> dotations = dotationService.getDotationsByPiece(pieceId);
            return ResponseEntity.ok(dotations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Récupérer les dotations par technicien.
     *
     * @param technicienId L'ID du technicien.
     * @return Liste des dotations pour ce technicien.
     */
    @GetMapping("/technicien/{technicienId}")
    public ResponseEntity<List<DotationDTO>> getDotationsByTechnicien(@PathVariable Long technicienId) {
        try {
            List<DotationDTO> dotations = dotationService.getDotationsByTechnicien(technicienId);
            return ResponseEntity.ok(dotations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Récupérer les dotations par plage de dates.
     *
     * @param startDate Date de début (YYYY-MM-DD).
     * @param endDate   Date de fin (YYYY-MM-DD).
     * @return Liste des dotations dans la plage de dates spécifiée.
     */
    @GetMapping("/dates")
    public ResponseEntity<List<DotationDTO>> getDotationsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        try {
            List<DotationDTO> dotations = dotationService.getDotationsByDateRange(startDate, endDate);
            return ResponseEntity.ok(dotations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
