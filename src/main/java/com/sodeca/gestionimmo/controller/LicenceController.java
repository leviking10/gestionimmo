package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.LicenceDTO;
import com.sodeca.gestionimmo.services.LicenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/licences")
@RequiredArgsConstructor
public class LicenceController {

    private final LicenceService licenceService;

    // Créer une nouvelle licence
    @PostMapping
    public ResponseEntity<LicenceDTO> createLicence(@RequestBody LicenceDTO licenceDTO) {
        LicenceDTO createdLicence = licenceService.createLicence(licenceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLicence);
    }

    // Mettre à jour une licence existante
    @PutMapping("/{id}")
    public ResponseEntity<LicenceDTO> updateLicence(@PathVariable Long id, @RequestBody LicenceDTO licenceDTO) {
        LicenceDTO updatedLicence = licenceService.updateLicence(id, licenceDTO);
        return ResponseEntity.ok(updatedLicence);
    }

    // Récupérer une licence par ID
    @GetMapping("/{id}")
    public ResponseEntity<LicenceDTO> getLicenceById(@PathVariable Long id) {
        LicenceDTO licence = licenceService.getLicenceById(id);
        return ResponseEntity.ok(licence);
    }

    // Récupérer toutes les licences
    @GetMapping
    public ResponseEntity<List<LicenceDTO>> getAllLicences() {
        List<LicenceDTO> licences = licenceService.getAllLicences();
        return ResponseEntity.ok(licences);
    }

    // Supprimer une licence
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicence(@PathVariable Long id) {
        licenceService.deleteLicence(id);
        return ResponseEntity.noContent().build();
    }

    // Rechercher des licences par nom
    @GetMapping("/search")
    public ResponseEntity<List<LicenceDTO>> searchLicencesByName(
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "fournisseur", required = false) String fournisseur) {
        if (nom != null) {
            return ResponseEntity.ok(licenceService.getLicencesByNom(nom));
        } else if (fournisseur != null) {
            return ResponseEntity.ok(licenceService.getLicencesByFournisseur(fournisseur));
        }
        return ResponseEntity.badRequest().build();
    }

    // Récupérer les licences expirées
    @GetMapping("/expired")
    public ResponseEntity<List<LicenceDTO>> getExpiredLicences() {
        List<LicenceDTO> expiredLicences = licenceService.getExpiredLicences();
        return ResponseEntity.ok(expiredLicences);
    }
}
