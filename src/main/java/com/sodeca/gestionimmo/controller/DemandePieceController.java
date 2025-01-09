package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.DemandePieceDTO;
import com.sodeca.gestionimmo.services.DemandePieceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
public class DemandePieceController {

    private final DemandePieceService service;

    public DemandePieceController(DemandePieceService service) {
        this.service = service;
    }

    /**
     * Créer une nouvelle demande.
     *
     * @param dto DemandePieceDTO contenant les informations nécessaires.
     * @return La demande créée.
     */
    @PostMapping
    public ResponseEntity<DemandePieceDTO> createDemande(@RequestBody DemandePieceDTO dto) {
        try {
            DemandePieceDTO createdDemande = service.createDemande(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDemande);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Valider une demande existante.
     *
     * @param id ID de la demande à valider.
     * @return La demande validée.
     */
    @PutMapping("/{id}/valider")
    public ResponseEntity<Object> validerDemande(@PathVariable Long id) {
        try {
            DemandePieceDTO validatedDemande = service.validerDemande(id);
            return ResponseEntity.ok(validatedDemande);
        } catch (RuntimeException e) {
            // Retourne un message d'erreur clair dans un objet JSON
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.BAD_REQUEST.value()
            ));
        }
    }
    @PutMapping("/{id}/livrer")
    public ResponseEntity<DemandePieceDTO> livrerDemande(@PathVariable Long id) {
        try {
            DemandePieceDTO deliveredDemande = service.livrerDemande(id);
            return ResponseEntity.ok(deliveredDemande);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Annuler une demande existante.
     *
     * @param id ID de la demande à annuler.
     * @return La demande annulée.
     */
    @PutMapping("/{id}/annuler")
    public ResponseEntity<DemandePieceDTO> annulerDemande(@PathVariable Long id) {
        try {
            DemandePieceDTO cancelledDemande = service.annulerDemande(id);
            return ResponseEntity.ok(cancelledDemande);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Récupérer toutes les demandes.
     *
     * @return Liste de toutes les demandes.
     */
    @GetMapping
    public ResponseEntity<List<DemandePieceDTO>> getAllDemandes() {
        List<DemandePieceDTO> demandes = service.getAllDemandes();
        return ResponseEntity.ok(demandes);
    }

    /**
     * Récupérer les demandes par technicien.
     *
     * @param technicienId ID du technicien.
     * @return Liste des demandes pour ce technicien.
     */
    @GetMapping("/technicien/{technicienId}")
    public ResponseEntity<List<DemandePieceDTO>> getDemandesByTechnicien(@PathVariable Long technicienId) {
        try {
            List<DemandePieceDTO> demandes = service.getDemandesByTechnicien(technicienId);
            return ResponseEntity.ok(demandes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Récupérer les demandes par pièce.
     *
     * @param pieceId ID de la pièce.
     * @return Liste des demandes pour cette pièce.
     */
    @GetMapping("/piece/{pieceId}")
    public ResponseEntity<List<DemandePieceDTO>> getDemandesByPiece(@PathVariable Long pieceId) {
        try {
            List<DemandePieceDTO> demandes = service.getDemandesByPiece(pieceId);
            return ResponseEntity.ok(demandes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Récupérer les demandes non validées.
     *
     * @return Liste des demandes non validées.
     */
    @GetMapping("/non-validees")
    public ResponseEntity<List<DemandePieceDTO>> getDemandesNonValidees() {
        List<DemandePieceDTO> nonValidees = service.getDemandesNonValidees();
        return ResponseEntity.ok(nonValidees);
    }
    @PutMapping("/{id}/rejeter")
    public ResponseEntity<DemandePieceDTO> rejeterDemande(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String commentaire = payload.get("commentaire");
        try {
            DemandePieceDTO rejectedDemande = service.rejeterDemande(id, commentaire);
            return ResponseEntity.ok(rejectedDemande);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
