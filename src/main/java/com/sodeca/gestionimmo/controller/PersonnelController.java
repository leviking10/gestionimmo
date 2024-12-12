package com.sodeca.gestionimmo.controller;

import com.sodeca.gestionimmo.dto.CategorieDTO;
import com.sodeca.gestionimmo.dto.PersonnelDTO;
import com.sodeca.gestionimmo.services.PersonnelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    // **********************************
    // GESTION DU PERSONNEL
    // **********************************

    /**
     * Récupérer tous les membres du personnel.
     *
     * @return Liste des PersonnelDTO.
     */
    @GetMapping
    public List<PersonnelDTO> getAllPersonnel() {
        return personnelService.getAllPersonnel();
    }

    /**
     * Récupérer un membre du personnel par son ID.
     *
     * @param id ID du personnel.
     * @return PersonnelDTO si trouvé, sinon 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonnelDTO> getPersonnelById(@PathVariable Long id) {
        try {
            PersonnelDTO personnel = personnelService.getPersonnelById(id);
            return ResponseEntity.ok(personnel);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupérer un membre du personnel par son matricule.
     *
     * @param matricule Matricule du personnel.
     * @return PersonnelDTO si trouvé, sinon 404.
     */
    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<PersonnelDTO> getPersonnelByMatricule(@PathVariable String matricule) {
        try {
            PersonnelDTO personnel = personnelService.getPersonnelByMatricule(matricule);
            return ResponseEntity.ok(personnel);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupérer un membre du personnel par son téléphone.
     *
     * @param telephone Numéro de téléphone du personnel.
     * @return PersonnelDTO si trouvé, sinon 404.
     */
    @GetMapping("/telephone/{telephone}")
    public ResponseEntity<PersonnelDTO> getPersonnelByTelephone(@PathVariable String telephone) {
        try {
            PersonnelDTO personnel = personnelService.getPersonnelByTelephone(telephone);
            return ResponseEntity.ok(personnel);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Créer un nouveau membre du personnel.
     *
     * @param personnelDTO PersonnelDTO contenant les informations.
     * @return PersonnelDTO créé avec statut 201.
     */
    @PostMapping
    public ResponseEntity<PersonnelDTO> createPersonnel(@RequestBody PersonnelDTO personnelDTO) {
        PersonnelDTO createdPersonnel = personnelService.createPersonnel(personnelDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPersonnel);
    }

    /**
     * Mettre à jour un membre du personnel existant.
     *
     * @param id           ID du personnel à mettre à jour.
     * @param personnelDTO PersonnelDTO contenant les nouvelles informations.
     * @return PersonnelDTO mis à jour, ou 404 si non trouvé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonnelDTO> updatePersonnel(@PathVariable Long id, @RequestBody PersonnelDTO personnelDTO) {
        try {
            PersonnelDTO updatedPersonnel = personnelService.updatePersonnel(id, personnelDTO);
            return ResponseEntity.ok(updatedPersonnel);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un membre du personnel par son ID.
     *
     * @param id ID du personnel à supprimer.
     * @return Réponse avec statut 204 si réussi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonnel(@PathVariable Long id) {
        try {
            personnelService.deletePersonnel(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/bulk")
    public ResponseEntity<List<PersonnelDTO>> createPersonnels(@RequestBody List<PersonnelDTO> personnelDTOS) {
        List<PersonnelDTO> createPersonnels = personnelService.createPersonnels(personnelDTOS);
        return ResponseEntity.status(HttpStatus.CREATED).body(createPersonnels);
    }
    @PutMapping("/activationmultiple")
    public ResponseEntity<String> activationForMultiple(@RequestBody List<Long> ids) {
        try {
            personnelService.activationForMultiple(ids);
            return ResponseEntity.ok("Les statuts des personnels ont été mis à jour avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<List<PersonnelDTO>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Lecture et validation du fichier
            List<PersonnelDTO> personnels = personnelService.readFile(file);

            // Création des personnels dans la base de données
            List<PersonnelDTO> createdPersonnels = personnelService.createPersonnels(personnels);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPersonnels);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PutMapping("/{id}/activation")
    public ResponseEntity<String> activation(@PathVariable Long id) {
        try {
            personnelService.desactiverOuActiverPersonnel(id);
            return ResponseEntity.ok("Le statut du personnel a été mis à jour avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
