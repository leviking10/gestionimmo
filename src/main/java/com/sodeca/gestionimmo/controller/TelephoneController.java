package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.TelephoneDTO;
import com.sodeca.gestionimmo.services.TelephoneService;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<TelephoneDTO> getAllTelephones() {
        return telephoneService.getAllTelephones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TelephoneDTO> getTelephoneById(@PathVariable Long id) {
        return telephoneService.getTelephoneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TelephoneDTO> createTelephone(@RequestBody TelephoneDTO dto) {
        TelephoneDTO createdTelephone = telephoneService.createTelephone(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTelephone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TelephoneDTO> updateTelephone(@PathVariable Long id, @RequestBody TelephoneDTO dto) {
        try {
            TelephoneDTO updatedTelephone = telephoneService.updateTelephone(id, dto);
            return ResponseEntity.ok(updatedTelephone);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTelephone(@PathVariable Long id) {
        try {
            telephoneService.deleteTelephone(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/telephones/bulk")
    public ResponseEntity<List<TelephoneDTO>> createTelephones(@RequestBody List<TelephoneDTO> dtos) {
        List<TelephoneDTO> createTelephones = dtos.stream()
                .map(telephoneService::createTelephone)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(createTelephones);
    }
}
