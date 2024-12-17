package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.TelephoneDTO;
import com.sodeca.gestionimmo.services.TelephoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telephones")
public class TelephoneController {

    private final TelephoneService telephoneService;

    public TelephoneController(TelephoneService telephoneService) {
        this.telephoneService = telephoneService;
    }

    /**
     * Récupérer tous les téléphones.
     */
    @GetMapping
    public List<TelephoneDTO> getAllTelephones() {
        return telephoneService.getAllTelephones();
    }

    /**
     * Récupérer un téléphone par ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TelephoneDTO> getTelephoneById(@PathVariable Long id) {
        return telephoneService.getTelephoneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mettre à jour les champs techniques d'un téléphone.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TelephoneDTO> updateTelephone(@PathVariable Long id, @RequestBody TelephoneDTO dto) {
        return ResponseEntity.ok(telephoneService.updateTelephone(id, dto));
    }
}
