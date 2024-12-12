package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.MachineDTO;
import com.sodeca.gestionimmo.dto.MobilierDTO;
import com.sodeca.gestionimmo.services.MobilierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobiliers")
public class MobilierController {

    private final MobilierService mobilierService;

    public MobilierController(MobilierService mobilierService) {
        this.mobilierService = mobilierService;
    }

    /**
     * Récupérer tous les mobiliers.
     *
     * @return Liste des MobilierDTO.
     */
    @GetMapping
    public List<MobilierDTO> getAllMobiliers() {
        return mobilierService.getAllMobilier();
    }

    /**
     * Récupérer un mobilier par ID.
     *
     * @param id ID du mobilier.
     * @return Le mobilier si trouvé, sinon 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MobilierDTO> getMobilierById(@PathVariable Long id) {
        return mobilierService.getMobilierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouveau mobilier.
     *
     * @param dto MobilierDTO à créer.
     * @return Le mobilier créé avec un statut 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<MobilierDTO> createMobilier(@RequestBody MobilierDTO dto) {
        MobilierDTO createdMobilier = mobilierService.createMobilier(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMobilier);
    }

    /**
     * Mettre à jour un mobilier existant.
     *
     * @param id  ID du mobilier à mettre à jour.
     * @param dto MobilierDTO contenant les nouvelles données.
     * @return Le mobilier mis à jour ou 404 si non trouvé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MobilierDTO> updateMobilier(@PathVariable Long id, @RequestBody MobilierDTO dto) {
        try {
            MobilierDTO updatedMobilier = mobilierService.updateMobilier(id, dto);
            return ResponseEntity.ok(updatedMobilier);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un mobilier par ID.
     *
     * @param id ID du mobilier à supprimer.
     * @return Une réponse avec le statut 204 (NO CONTENT) si réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMobilier(@PathVariable Long id) {
        try {
            mobilierService.deleteMobilier(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<MobilierDTO>> createMobiliers(@RequestBody List<MobilierDTO> dtos) {
        List<MobilierDTO> createMobiliers = dtos.stream()
                .map(mobilierService::createMobilier)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(createMobiliers);
    }
}
