package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.services.AmortissementService;
import com.sodeca.gestionimmo.services.ImmobilisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/immobilisations")
public class ImmobilisationController {

    private final ImmobilisationService immobilisationService;

    public ImmobilisationController(ImmobilisationService immobilisationService) {
        this.immobilisationService = immobilisationService;
    }

    // **********************************
    // GESTION DES IMMOBILISATIONS
    // **********************************

    // Récupérer toutes les immobilisations
    @GetMapping
    public List<ImmobilisationDTO> getAllImmobilisations() {
        return immobilisationService.getAllImmobilisations();
    }

    // Récupérer une immobilisation par ID
    @GetMapping("/{id}")
    public ResponseEntity<ImmobilisationDTO> getImmobilisationById(@PathVariable Long id) {
        return immobilisationService.getImmobilisationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Autowired
    private AmortissementService amortissementService;

    @GetMapping("/{id}/amortissements")
    public ResponseEntity<List<AmortissementDTO>> getAmortissementsByImmobilisation(@PathVariable Long id) {
        List<AmortissementDTO> amortissements = amortissementService.getAmortissementsByImmobilisation(id);
        return ResponseEntity.ok(amortissements);
    }
    // Récupérer une immobilisation par son codeImmo
    @GetMapping("/code/{codeImmo}")
    public ResponseEntity<ImmobilisationDTO> getImmobilisationByCode(@PathVariable String codeImmo) {
        ImmobilisationDTO immobilisation = immobilisationService.getImmobilisationByCode(codeImmo);
        return ResponseEntity.ok(immobilisation);
    }

    // Créer une immobilisation
    @PostMapping
    public ResponseEntity<ImmobilisationDTO> createImmobilisation(@RequestBody ImmobilisationDTO dto) {
        ImmobilisationDTO createdImmobilisation = immobilisationService.createImmobilisation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImmobilisation);
    }

    // Mettre à jour une immobilisation
    @PutMapping("/{id}")
    public ResponseEntity<ImmobilisationDTO> updateImmobilisation(@PathVariable Long id, @RequestBody ImmobilisationDTO dto) {
        try {
            ImmobilisationDTO updatedImmobilisation = immobilisationService.updateImmobilisation(id, dto);
            return ResponseEntity.ok(updatedImmobilisation);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer plusieurs immobilisations en une seule requête
    @PostMapping("/bulk")
    public ResponseEntity<List<ImmobilisationDTO>> createImmobilisations(@RequestBody List<ImmobilisationDTO> dtos) {
        List<ImmobilisationDTO> createdImmobilisations = immobilisationService.createImmobilisations(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImmobilisations);
    }

    // Supprimer une immobilisation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImmobilisation(@PathVariable Long id) {
        immobilisationService.deleteImmobilisation(id);
        return ResponseEntity.noContent().build();
    }

    // **********************************
    // GESTION DES QR CODES
    // **********************************

    // Afficher le QR Code d'une immobilisation
    @GetMapping("/qrcode/{id}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) {
        try {
            byte[] qrCodeBytes = immobilisationService.getQRCodeAsImage(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeBytes);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Télécharger le QR Code d'une immobilisation
    @GetMapping("/downloadqrcode/{id}")
    public ResponseEntity<byte[]> downloadQRCode(@PathVariable Long id) {
        byte[] qrCodeBytes = immobilisationService.downloadQRCode(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"immobilisation_" + id + "_qrcode.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCodeBytes);
    }

    // Rechercher une immobilisation via QR Code
    @PostMapping("/qrcode")
    public ResponseEntity<ImmobilisationDTO> getImmobilisationByQRCode(@RequestBody String qrCodeData) {
        ImmobilisationDTO immobilisationDTO = immobilisationService.getImmobilisationByQRCode(qrCodeData.trim());
        return ResponseEntity.ok(immobilisationDTO);
    }

    // **********************************
    // GESTION DES ÉTATS D'IMMOBILISATIONS
    // **********************************

    // Mettre à jour l'état d'une immobilisation
    @PutMapping("/etat/{id}")
    public ResponseEntity<Void> updateEtat(
            @PathVariable Long id,
            @RequestParam EtatImmobilisation etat) {
        immobilisationService.updateEtat(id, etat);
        return ResponseEntity.ok().build();
    }

    /**
     * Importer un fichier Excel ou CSV pour créer des immobilisations.
     *
     * @param file Le fichier à importer.
     * @return Liste des immobilisations créées.
     */



    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Vérification si le fichier est vide
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Le fichier est vide. Veuillez fournir un fichier valide.");
            }

            // Appel au service pour traiter le fichier
            List<ImmobilisationDTO> importedImmobilisations = immobilisationService.importImmobilisationsFromFile(file);

            // Retourner la liste des immobilisations importées
            return ResponseEntity.ok(importedImmobilisations);
        } catch (RuntimeException e) {
            // Gérer les erreurs spécifiques de runtime
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Gérer les autres erreurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de l'importation : " + e.getMessage());
        }
    }


}
