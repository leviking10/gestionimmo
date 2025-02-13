package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.dto.SituationAmortissementDTO;

import java.util.List;
public interface AmortissementService {

    List<AmortissementDTO> getAmortissementsByImmobilisation(Long immobilisationId);

    List<AmortissementDTO> generateAmortissementsForImmobilisation(Long immobilisationId, String methode);

    void deleteAmortissement(int id);

    void cancelAmortissement(int id);

    SituationAmortissementDTO getSituationAmortissementsAvecCumul(Long immobilisationId, String date);

    List<AmortissementDTO> getAllAmortissements();
    List<AmortissementDTO> getFilteredAmortissements(String categorie, String methode, String etat, String periode);
}
