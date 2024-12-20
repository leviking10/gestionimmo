package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.CessionDTO;
import com.sodeca.gestionimmo.enums.StatutCession;
import com.sodeca.gestionimmo.services.CessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cessions")
public class CessionController {

    private final CessionService cessionService;

    public CessionController(CessionService cessionService) {
        this.cessionService = cessionService;
    }

    /**
     * Créer une nouvelle cession.
     */
    @PostMapping
    public ResponseEntity<CessionDTO> createCession(@RequestBody CessionDTO cessionDTO) {
        CessionDTO createdCession = cessionService.createCession(cessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCession);
    }

    /**
     * Mettre à jour une cession existante.
     */
    @PutMapping("/{cessionId}")
    public ResponseEntity<CessionDTO> updateCession(@PathVariable Long cessionId, @RequestBody CessionDTO cessionDTO) {
        CessionDTO updatedCession = cessionService.updateCession(cessionId, cessionDTO);
        return ResponseEntity.ok(updatedCession);
    }

    /**
     * Supprimer une cession par ID.
     */
    @DeleteMapping("/{cessionId}")
    public ResponseEntity<Void> deleteCession(@PathVariable Long cessionId) {
        cessionService.deleteCession(cessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtenir une cession par son ID.
     */
    @GetMapping("/{cessionId}")
    public ResponseEntity<CessionDTO> getCessionById(@PathVariable Long cessionId) {
        return ResponseEntity.ok(cessionService.getCessionById(cessionId));
    }

    /**
     * Obtenir toutes les cessions.
     */
    @GetMapping
    public ResponseEntity<List<CessionDTO>> getAllCessions() {
        return ResponseEntity.ok(cessionService.getAllCessions());
    }

    /**
     * Obtenir les cessions par statut.
     */
    @GetMapping("/statut")
    public ResponseEntity<List<CessionDTO>> getCessionsByStatut(@RequestParam StatutCession statut) {
        return ResponseEntity.ok(cessionService.getCessionsByStatut(statut));
    }

    /**
     * Obtenir les cessions dans une plage de dates.
     */
    @GetMapping("/dates")
    public ResponseEntity<List<CessionDTO>> getCessionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(cessionService.getCessionsByDateRange(startDate, endDate));
    }

    /**
     * Obtenir les cessions pour une immobilisation spécifique.
     */
    @GetMapping("/immobilisation/{immobilisationId}")
    public ResponseEntity<List<CessionDTO>> getCessionsByImmobilisation(@PathVariable Long immobilisationId) {
        return ResponseEntity.ok(cessionService.getCessionsByImmobilisation(immobilisationId));
    }

    /**
     * Obtenir le nombre de cessions par statut.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getCessionCountByStatut(@RequestParam StatutCession statut) {
        return ResponseEntity.ok(cessionService.getCountByStatut(statut));
    }

    /**
     * Annuler une cession existante pour une immobilisation.
     */
    @PutMapping("/annuler/{immobilisationId}")
    public ResponseEntity<Void> annulerCession(@PathVariable Long immobilisationId) {
        cessionService.annulerCession(immobilisationId);
        return ResponseEntity.ok().build();
    }
}
