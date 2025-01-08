package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.entity.ApiResponse;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.services.AmortissementService;
import com.sodeca.gestionimmo.services.ImmobilisationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/immobilisations")
public class ImmobilisationController {

    private final ImmobilisationService immobilisationService;
    private final AmortissementService amortissementService;

    public ImmobilisationController(ImmobilisationService immobilisationService, AmortissementService amortissementService) {
        this.immobilisationService = immobilisationService;
        this.amortissementService = amortissementService;
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
    public ResponseEntity<String> updateEtat(@PathVariable Long id, @RequestParam String etat) {
        EtatImmobilisation etatImmobilisation;
        try {
            etatImmobilisation = EtatImmobilisation.fromLabelOrName(etat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Etat invalide : " + etat);
        }

        immobilisationService.updateEtat(id, etatImmobilisation);
        return ResponseEntity.ok().build();
    }


    /**
     * Importer un fichier Excel ou CSV pour créer des immobilisations.
     *
     * @param file Le fichier à importer.
     * @return Liste des immobilisations créées.
     */


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<List<ImmobilisationDTO>>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Vérification si le fichier est vide
            if (file.isEmpty()) {
                ApiResponse<List<ImmobilisationDTO>> response = new ApiResponse<>(
                        "Le fichier est vide. Veuillez fournir un fichier valide.", null, HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Appel au service pour traiter le fichier
            List<ImmobilisationDTO> importedImmobilisations = immobilisationService.importImmobilisationsFromFile(file);

            // Retourner la liste des immobilisations importées
            ApiResponse<List<ImmobilisationDTO>> response = new ApiResponse<>(
                    "Importation réussie.", importedImmobilisations, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (BusinessException e) {
            // Gérer les erreurs métier spécifiques
            ApiResponse<List<ImmobilisationDTO>> response = new ApiResponse<>(
                    e.getMessage(), null, e.getStatus().value());
            return ResponseEntity.status(e.getStatus()).body(response);

        } catch (Exception e) {
            // Gérer les erreurs inattendues
            ApiResponse<List<ImmobilisationDTO>> response = new ApiResponse<>(
                    "Une erreur interne est survenue : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /** liste des immobilisations cédées
     *
     *
     */
    @GetMapping("/ceded")
    public List<ImmobilisationDTO> getImmobilisationsCedees() {
        return immobilisationService.getImmobilisationsCedees();
    }

}
