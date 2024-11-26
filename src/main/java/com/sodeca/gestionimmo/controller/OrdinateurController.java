package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.OrdinateurDTO;
import com.sodeca.gestionimmo.services.OrdinateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordinateurs")
public class OrdinateurController {

    private final OrdinateurService ordinateurService;

    public OrdinateurController(OrdinateurService ordinateurService) {
        this.ordinateurService = ordinateurService;
    }

    /**
     * Récupérer tous les ordinateurs.
     *
     * @return Liste des OrdinateurDTO.
     */
    @GetMapping
    public List<OrdinateurDTO> getAllOrdinateurs() {
        return ordinateurService.getAllOrdinateurs();
    }

    /**
     * Récupérer un ordinateur par ID.
     *
     * @param id ID de l'ordinateur.
     * @return L'ordinateur si trouvé, sinon 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdinateurDTO> getOrdinateurById(@PathVariable Long id) {
        return ordinateurService.getOrdinateurById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouvel ordinateur.
     *
     * @param dto OrdinateurDTO à créer.
     * @return L'ordinateur créé avec un statut 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<OrdinateurDTO> createOrdinateur(@RequestBody OrdinateurDTO dto) {
        OrdinateurDTO createdOrdinateur = ordinateurService.createOrdinateur(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrdinateur);
    }

    /**
     * Mettre à jour un ordinateur existant.
     *
     * @param id  ID de l'ordinateur à mettre à jour.
     * @param dto OrdinateurDTO contenant les nouvelles données.
     * @return L'ordinateur mis à jour ou 404 si non trouvé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrdinateurDTO> updateOrdinateur(@PathVariable Long id, @RequestBody OrdinateurDTO dto) {
        try {
            OrdinateurDTO updatedOrdinateur = ordinateurService.updateOrdinateur(id, dto);
            return ResponseEntity.ok(updatedOrdinateur);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un ordinateur par ID.
     *
     * @param id ID de l'ordinateur à supprimer.
     * @return Une réponse avec le statut 204 (NO CONTENT) si réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdinateur(@PathVariable Long id) {
        try {
            ordinateurService.deleteOrdinateur(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/ordinateurs/bulk")
    public ResponseEntity<List<OrdinateurDTO>> createOrdinateurs(@RequestBody List<OrdinateurDTO> dtos) {
        List<OrdinateurDTO> createdOrdinateurs = dtos.stream()
                .map(ordinateurService::createOrdinateur)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrdinateurs);
    }
}
