package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.RenouvellementLicenceDTO;
import com.sodeca.gestionimmo.services.RenouvellementLicenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/renouvellements")
@RequiredArgsConstructor
public class RenouvellementLicenceController {

    private final RenouvellementLicenceService renouvellementLicenceService;

    // Créer un nouveau renouvellement
    @PostMapping
    public ResponseEntity<RenouvellementLicenceDTO> createRenouvellement(
            @RequestBody RenouvellementLicenceDTO renouvellementDTO) {
        RenouvellementLicenceDTO createdRenouvellement = renouvellementLicenceService.createRenouvellement(renouvellementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRenouvellement);
    }

    // Récupérer les renouvellements par licence ID
    @GetMapping("/by-licence/{licenceId}")
    public ResponseEntity<List<RenouvellementLicenceDTO>> getRenouvellementsByLicenceId(@PathVariable Long licenceId) {
        List<RenouvellementLicenceDTO> renouvellements = renouvellementLicenceService.getRenouvellementsByLicenceId(licenceId);
        return ResponseEntity.ok(renouvellements);
    }

    // Supprimer un renouvellement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRenouvellement(@PathVariable Long id) {
        renouvellementLicenceService.deleteRenouvellement(id);
        return ResponseEntity.noContent().build();
    }
}
