package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.CessionDTO;
import com.sodeca.gestionimmo.services.CessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cessions")
public class CessionController {

    private final CessionService cessionService;

    public CessionController(CessionService cessionService) {
        this.cessionService = cessionService;
    }

    /**
     * Créer une cession.
     *
     * @param cessionDTO Les informations de la cession.
     * @return La cession créée.
     */
    @PostMapping
    public ResponseEntity<CessionDTO> createCession(@RequestBody CessionDTO cessionDTO) {
        CessionDTO createdCession = cessionService.createCession(cessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCession);
    }

    /**
     * Mettre à jour une cession existante.
     *
     * @param cessionId  L'identifiant de la cession à mettre à jour.
     * @param cessionDTO Les nouvelles informations de la cession.
     * @return La cession mise à jour.
     */
    @PutMapping("/{cessionId}")
    public ResponseEntity<CessionDTO> updateCession(@PathVariable Long cessionId, @RequestBody CessionDTO cessionDTO) {
        CessionDTO updatedCession = cessionService.updateCession(cessionId, cessionDTO);
        return ResponseEntity.ok(updatedCession);
    }

    /**
     * Supprimer une cession.
     *
     * @param cessionId L'identifiant de la cession à supprimer.
     * @return Réponse avec un statut HTTP 204 (No Content).
     */
    @DeleteMapping("/{cessionId}")
    public ResponseEntity<Void> deleteCession(@PathVariable Long cessionId) {
        cessionService.deleteCession(cessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtenir les cessions pour une immobilisation spécifique.
     *
     * @param immobilisationId L'identifiant de l'immobilisation.
     * @return La liste des cessions associées.
     */
    @GetMapping("/immobilisation/{immobilisationId}")
    public ResponseEntity<List<CessionDTO>> getCessionsByImmobilisation(@PathVariable Long immobilisationId) {
        List<CessionDTO> cessions = cessionService.getCessionsByImmobilisation(immobilisationId);
        return ResponseEntity.ok(cessions);
    }

    /**
     * Annuler une cession.
     *
     * @param immobilisationId ID de l'immobilisation.
     * @return Confirmation de l'annulation.
     */
    @PutMapping("/cession/annuler/{id}")
    public ResponseEntity<Void> annulerCession(@PathVariable("id") Long immobilisationId) {
        cessionService.annulerCession(immobilisationId);
        return ResponseEntity.ok().build();
    }


}
