package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.SignalementDTO;
import com.sodeca.gestionimmo.services.SignalementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signalements")
public class SignalementController {

    private final SignalementService signalementService;

    public SignalementController(SignalementService signalementService) {
        this.signalementService = signalementService;
    }

    // **********************************
    // GESTION DES SIGNALEMENTS
    // **********************************

    /**
     * Récupérer tous les signalements pour une immobilisation donnée.
     *
     * @param immobilisationId ID de l'immobilisation.
     * @return Liste des SignalementDTO.
     */
    @GetMapping("/immobilisation/{immobilisationId}")
    public List<SignalementDTO> getSignalementsByImmobilisation(@PathVariable Long immobilisationId) {
        return signalementService.getSignalementsByImmobilisation(immobilisationId);
    }

    /**
     * Récupérer tous les signalements faits par un membre du personnel donné.
     *
     * @param personnelId ID du personnel.
     * @return Liste des SignalementDTO.
     */
    @GetMapping("/personnel/{personnelId}")
    public List<SignalementDTO> getSignalementsByPersonnel(@PathVariable Long personnelId) {
        return signalementService.getSignalementsByPersonnel(personnelId);
    }

    /**
     * Récupérer un signalement par son ID.
     *
     * @param id ID du signalement.
     * @return SignalementDTO si trouvé, sinon 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SignalementDTO> getSignalementById(@PathVariable Long id) {
        try {
            SignalementDTO signalement = signalementService.getSignalementById(id);
            return ResponseEntity.ok(signalement);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Créer un nouveau signalement.
     *
     * @param signalementDTO SignalementDTO contenant les informations.
     * @return SignalementDTO créé avec statut 201.
     */
    @PostMapping
    public ResponseEntity<SignalementDTO> createSignalement(@RequestBody SignalementDTO signalementDTO) {
        SignalementDTO createdSignalement = signalementService.createSignalement(signalementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSignalement);
    }

    /**
     * Mettre à jour un signalement existant.
     *
     * @param id             ID du signalement à mettre à jour.
     * @param signalementDTO SignalementDTO contenant les nouvelles informations.
     * @return SignalementDTO mis à jour, ou 404 si non trouvé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SignalementDTO> updateSignalement(@PathVariable Long id, @RequestBody SignalementDTO signalementDTO) {
        try {
            SignalementDTO updatedSignalement = signalementService.updateSignalement(id, signalementDTO);
            return ResponseEntity.ok(updatedSignalement);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un signalement par son ID.
     *
     * @param id ID du signalement à supprimer.
     * @return Réponse avec statut 204 si réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSignalement(@PathVariable Long id) {
        try {
            signalementService.deleteSignalement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
