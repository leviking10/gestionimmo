package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.SituationAmortissementDTO;
import com.sodeca.gestionimmo.services.AmortissementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amortissements")
public class AmortissementController {

    private final AmortissementService amortissementService;

    public AmortissementController(AmortissementService amortissementService) {
        this.amortissementService = amortissementService;
    }

    /**
     * Récupérer tous les amortissements pour une immobilisation donnée.
     *
     * @param immobilisationId L'ID de l'immobilisation.
     * @return Liste des AmortissementDTO.
     */
    @GetMapping("/immobilisation/{immobilisationId}")
    public ResponseEntity<List<AmortissementDTO>> getAmortissementsByImmobilisation(@PathVariable Long immobilisationId) {
        try {
            List<AmortissementDTO> amortissements = amortissementService.getAmortissementsByImmobilisation(immobilisationId);
            return ResponseEntity.ok(amortissements);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Générer automatiquement des amortissements pour une immobilisation selon une méthode.
     *
     * @param immobilisationId L'ID de l'immobilisation.
     * @param methode          La méthode d'amortissement ("Linéaire" ou "Dégressif").
     * @return Liste des AmortissementDTO générés.
     */
    @PostMapping("/generate/{immobilisationId}")
    public ResponseEntity<?> generateAmortissements(
            @PathVariable Long immobilisationId,
            @RequestParam String methode) {
        try {
            List<AmortissementDTO> amortissements = amortissementService.generateAmortissementsForImmobilisation(immobilisationId, methode);
            return ResponseEntity.ok(amortissements);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Supprimer un amortissement par ID.
     *
     * @param id L'ID de l'amortissement à supprimer.
     * @return Une réponse avec le statut 204 (NO CONTENT) si réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmortissement(@PathVariable int id) {
        try {
            amortissementService.deleteAmortissement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Annuler un amortissement.
     *
     * @param id L'ID de l'amortissement à annuler.
     * @return Une réponse avec le statut 200 (OK) si réussi.
     */
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelAmortissement(@PathVariable int id) {
        try {
            amortissementService.cancelAmortissement(id);
            return ResponseEntity.ok("Amortissement annulé avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    /**
     * Récupérer la situation des amortissements à une date donnée pour une immobilisation.
     *
     * @param immobilisationId L'ID de l'immobilisation.
     * @param date             La date jusqu'à laquelle récupérer les amortissements.
     * @return Liste des AmortissementDTO.
     */
    /**
     * Récupérer la situation des amortissements à une date donnée avec le cumul des montants amortis.
     *
     * @param immobilisationId L'ID de l'immobilisation.
     * @param date             La date jusqu'à laquelle récupérer les amortissements.
     * @return SituationAmortissementDTO contenant la liste des amortissements et le cumul.
     */
    @GetMapping("/immobilisation/{immobilisationId}/situation-cumul")
    public ResponseEntity<SituationAmortissementDTO> getSituationAmortissementsAvecCumul(
            @PathVariable Long immobilisationId,
            @RequestParam String date) {
        try {
            SituationAmortissementDTO situation = amortissementService.getSituationAmortissementsAvecCumul(immobilisationId, date);
            return ResponseEntity.ok(situation);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
