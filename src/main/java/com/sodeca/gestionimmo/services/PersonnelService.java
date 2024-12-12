package com.sodeca.gestionimmo.services;
import com.sodeca.gestionimmo.dto.PersonnelDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PersonnelService {

    PersonnelDTO createPersonnel(PersonnelDTO personnelDTO);

    PersonnelDTO updatePersonnel(Long personnelId, PersonnelDTO personnelDTO);

    void deletePersonnel(Long personnelId);

    List<PersonnelDTO> getAllPersonnel();

    PersonnelDTO getPersonnelById(Long personnelId);

    PersonnelDTO getPersonnelByMatricule(String matricule);

    PersonnelDTO getPersonnelByTelephone(String telephone);

    /**
     * Créer une liste de personnels.
     *
     * @param dtos Liste des PersonnelDTO à créer.
     * @return Liste des OrdinateurDTO créés.
     */
    List<PersonnelDTO> createPersonnels(List<PersonnelDTO> dtos);

    List<PersonnelDTO> readFile(MultipartFile file) throws IOException;
    void desactiverOuActiverPersonnel(Long id);
    void activationForMultiple(List<Long> ids);
}
