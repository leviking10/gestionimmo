package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AmortissementDTO;

import java.util.List;

public interface AmortissementService {

    List<AmortissementDTO> getAmortissementsByImmobilisation(Long immobilisationId);

    List<AmortissementDTO> generateAmortissementsForImmobilisation(Long immobilisationId, String methode);

    void deleteAmortissement(int id);
    void cancelAmortissement(int id);

}
