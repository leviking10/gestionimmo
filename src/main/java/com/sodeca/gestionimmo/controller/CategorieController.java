package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.CategorieDTO;
import com.sodeca.gestionimmo.services.CategorieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @PostMapping
    public ResponseEntity<CategorieDTO> createCategorie(@RequestBody CategorieDTO categorieDTO) {
        return ResponseEntity.ok(categorieService.createCategorie(categorieDTO));
    }

    @GetMapping
    public ResponseEntity<List<CategorieDTO>> getAllCategories() {
        return ResponseEntity.ok(categorieService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategorieById(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.getCategorieById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> updateCategorie(@PathVariable Long id, @RequestBody CategorieDTO categorieDTO) {
        return ResponseEntity.ok(categorieService.updateCategorie(id, categorieDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CategorieDTO>> createCategories(@RequestBody List<CategorieDTO> categorieDTOs) {
        List<CategorieDTO> createdCategories = categorieService.createCategories(categorieDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategories);
    }
}
