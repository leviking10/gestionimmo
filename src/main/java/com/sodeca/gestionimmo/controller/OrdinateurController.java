package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.OrdinateurDTO;
import com.sodeca.gestionimmo.services.OrdinateurService;
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
     * Récupérer la liste des ordinateurs.
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
     * @param id Identifiant de l'ordinateur.
     * @return OrdinateurDTO si trouvé, 404 sinon.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdinateurDTO> getOrdinateurById(@PathVariable Long id) {
        return ordinateurService.getOrdinateurById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mettre à jour les champs techniques d'un ordinateur existant.
     *
     * @param id  Identifiant de l'ordinateur.
     * @param dto DTO contenant les informations à mettre à jour.
     * @return OrdinateurDTO mis à jour.
     */
    @PutMapping("/ordinateurs/{id}")
    public ResponseEntity<OrdinateurDTO> updateOrdinateur(@PathVariable Long id, @RequestBody OrdinateurDTO dto) {
        return ResponseEntity.ok(ordinateurService.updateOrdinateur(id, dto));
    }
}
