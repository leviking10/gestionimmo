package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.MobilierDTO;
import com.sodeca.gestionimmo.services.MobilierService;
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
     * Récupérer la liste des mobiliers.
     */
    @GetMapping
    public List<MobilierDTO> getAllMobiliers() {
        return mobilierService.getAllMobilier();
    }

    /**
     * Récupérer un mobilier par ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MobilierDTO> getMobilierById(@PathVariable Long id) {
        return mobilierService.getMobilierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mettre à jour les champs techniques d'un mobilier.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MobilierDTO> updateMobilier(@PathVariable Long id, @RequestBody MobilierDTO dto) {
        return ResponseEntity.ok(mobilierService.updateMobilier(id, dto));
    }
}
