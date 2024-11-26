package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.*;
import com.sodeca.gestionimmo.services.ImmobilisationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public List<ImmobilisationDTO> getAllImmobilisations() {
        return immobilisationService.getAllImmobilisations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImmobilisationDTO> getImmobilisationById(@PathVariable Long id) {
        return immobilisationService.getImmobilisationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping//pour créer une immobilisation
    public ResponseEntity<ImmobilisationDTO> createImmobilisation(@RequestBody ImmobilisationDTO dto) {
        ImmobilisationDTO createdImmobilisation = immobilisationService.createImmobilisation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImmobilisation);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ImmobilisationDTO> updateImmobilisation(@PathVariable Long id, @RequestBody ImmobilisationDTO dto) {
        try {
            ImmobilisationDTO updatedImmobilisation = immobilisationService.updateImmobilisation(id, dto);
            return ResponseEntity.ok(updatedImmobilisation);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/bulk")//pour créer plusieurs immobilisations
    public ResponseEntity<List<ImmobilisationDTO>> createImmobilisations(@RequestBody List<ImmobilisationDTO> dtos) {
        List<ImmobilisationDTO> createdImmobilisations = immobilisationService.createImmobilisations(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImmobilisations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImmobilisation(@PathVariable Long id) {
        immobilisationService.deleteImmobilisation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/qrcode/{id}")//pour récupérer le QR Code d'une immobilisation
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
    @GetMapping("/immobilisations/downloadqrcode/{id}")
    public ResponseEntity<byte[]> downloadQRCode(@PathVariable Long id) {
        byte[] qrCodeBytes = immobilisationService.downloadQRCode(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"immobilisation_" + id + "_qrcode.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCodeBytes);
    }
    @PostMapping("/immobilisations/qrcode")
    public ResponseEntity<ImmobilisationDTO> getImmobilisationByQRCode(@RequestBody String qrCodeData) {
        ImmobilisationDTO immobilisationDTO = immobilisationService.getImmobilisationByQRCode(qrCodeData);
        return ResponseEntity.ok(immobilisationDTO);
    }

}
