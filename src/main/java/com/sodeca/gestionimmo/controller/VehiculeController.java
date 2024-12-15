package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.MachineDTO;
import com.sodeca.gestionimmo.dto.VehiculeDTO;
import com.sodeca.gestionimmo.services.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    /**
     * Récupérer tous les véhicules.
     *
     * @return Liste des VehiculeDTO.
     */
    @GetMapping
    public List<VehiculeDTO> getAllVehicules() {
        return vehiculeService.getAllVehicules();
    }

    /**
     * Récupérer un véhicule par ID.
     *
     * @param id ID du véhicule.
     * @return Le véhicule si trouvé, sinon 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDTO> getVehiculeById(@PathVariable Long id) {
        return vehiculeService.getVehiculeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouveau véhicule.
     *
     * @param dto VehiculeDTO à créer.
     * @return Le véhicule créé avec un statut 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<VehiculeDTO> createVehicule(@RequestBody VehiculeDTO dto) {
        VehiculeDTO createdVehicule = vehiculeService.createVehicule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicule);
    }

    /**
     * Mettre à jour un véhicule existant.
     *
     * @param id  ID du véhicule à mettre à jour.
     * @param dto VehiculeDTO contenant les nouvelles données.
     * @return Le véhicule mis à jour ou 404 si non trouvé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDTO> updateVehicule(@PathVariable Long id, @RequestBody VehiculeDTO dto) {
        try {
            VehiculeDTO updatedVehicule = vehiculeService.updateVehicule(id, dto);
            return ResponseEntity.ok(updatedVehicule);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un véhicule par ID.
     *
     * @param id ID du véhicule à supprimer.
     * @return Une réponse avec le statut 204 (NO CONTENT) si réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable Long id) {
        try {
            vehiculeService.deleteVehicule(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/bulk")
    public ResponseEntity<List<VehiculeDTO>> createVehicules(@RequestBody List<VehiculeDTO> dtos) {
        List<VehiculeDTO> createVehicules = dtos.stream()
                .map(vehiculeService::createVehicule)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(createVehicules);
    }
}
