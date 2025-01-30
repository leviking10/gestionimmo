package com.sodeca.gestionimmo.controller;
import com.sodeca.gestionimmo.dto.AffectationDTO;
import com.sodeca.gestionimmo.services.AffectationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/affectations")
public class AffectationController {
    private final AffectationService affectationService;
    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @PostMapping("/liberer/{immobilisationId}")
    public ResponseEntity<Void> libererImmobilisation(@PathVariable Long immobilisationId) {
        affectationService.retournerImmobilisation(immobilisationId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<AffectationDTO> updateAffectation(
            @PathVariable Long id,
            @RequestBody AffectationDTO affectationDTO
    ) {
        AffectationDTO updatedAffectation = affectationService.updateAffectation(id, affectationDTO);
        return ResponseEntity.ok(updatedAffectation);
    }

    @PostMapping
    public ResponseEntity<AffectationDTO> createAffectation(@RequestBody AffectationDTO dto) {
        AffectationDTO created = affectationService.createAffectation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping("/immobilisation/historique/{immobilisationId}")
    public ResponseEntity<List<AffectationDTO>> getHistoriqueByImmobilisation(@PathVariable Long immobilisationId) {
        List<AffectationDTO> historique = affectationService.getHistoriqueByImmobilisation(immobilisationId);
        return ResponseEntity.ok(historique);
    }
    @GetMapping("/personnel/{personnelId}")
    public ResponseEntity<List<AffectationDTO>> getAffectationsByPersonnel(@PathVariable Long personnelId) {
        return ResponseEntity.ok(affectationService.getAffectationsByPersonnel(personnelId));
    }
    @GetMapping("/immobilisation/{immobilisationId}")
    public ResponseEntity<List<AffectationDTO>> getAffectationsByImmobilisation(@PathVariable Long immobilisationId) {
        return ResponseEntity.ok(affectationService.getAffectationsByImmobilisation(immobilisationId));
    }
    @GetMapping("/historique")
    public ResponseEntity<List<AffectationDTO>> getHistoriqueAffectations() {
        return ResponseEntity.ok(affectationService.getHistoriqueAffectations());
    }
}
