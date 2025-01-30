package com.sodeca.gestionimmo;

import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutAffectation;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GestionimmoApplicationTests {

    @Test
    void contextLoads() {
    }
    @Autowired
    private ImmobilisationMapper immobilisationMapper;
/*
    @Test
    public void testMapperToDTOAndToEntity() {
        // Créer une entité d'exemple
        Immobilisation immobilisation = new Immobilisation();
        immobilisation.setEtatImmo(EtatImmobilisation.EN_SERVICE);
        immobilisation.setStatut(StatutAffectation.DISPONIBLE);

        // Initialiser la catégorie
        Categorie categorie = new Categorie();
        categorie.setCategorie("Matériel Informatique");
        categorie.setDureeAmortissement(5);
        immobilisation.setCategorie(categorie);

        // Tester la conversion vers DTO
        ImmobilisationDTO dto = immobilisationMapper.toDTO(immobilisation);
        System.out.println("DTO généré : " + dto);

        // Vérifications
        assertNotNull(dto);
        assertEquals("En service", dto.getEtatImmobilisation().getLabel());
        assertEquals(StatutAffectation.DISPONIBLE, dto.getStatutAffectation());
        assertEquals(5, dto.getDureeAmortissement());

        // Tester la conversion vers Entity
        Immobilisation entity = immobilisationMapper.toEntity(dto);
        System.out.println("Entity générée : " + entity);

        // Vérifications
        assertNotNull(entity);
        assertEquals(EtatImmobilisation.EN_SERVICE, entity.getEtatImmo());
        assertEquals(StatutAffectation.DISPONIBLE, entity.getStatut());
    }
*/
}
