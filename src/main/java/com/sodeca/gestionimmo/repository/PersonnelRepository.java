package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    /**
     * Trouver un membre du personnel par son numéro de téléphone.
     *
     * @param telephone Le numéro de téléphone.
     * @return Un Optional contenant le personnel correspondant, ou vide si introuvable.
     */
    Optional<Personnel> findByTelephone(String telephone);

    /**
     * Trouver tous les membres du personnel actifs.
     *
     * @return Liste des membres actifs.
     */
    List<Personnel> findByActifTrue();

    /**
     * Trouver tous les membres d'un département donné.
     *
     * @param departement Le nom du département.
     * @return Liste des membres appartenant à ce département.
     */
    List<Personnel> findByDepartement(String departement);
/**
     * Trouver tous les membres d'un service donné.
     *
     * @param service Le nom du service.
     * @return Liste des membres appartenant à ce service.
     */

    List<Personnel> findByService(String service);
    /**
     * Vérifier si un matricule existe déjà dans la base.
     *
     * @param matricule Le matricule unique.
     * @return True si le matricule existe, sinon False.
     */
    boolean existsByMatricule(String matricule);
    /**
     * Trouver un membre du personnel par son matricule unique.
     *
     * @param matricule Le matricule unique.
     * @return Un Optional contenant le personnel correspondant, ou vide si introuvable.
     */
    Optional<Personnel> findByMatricule(String matricule);

}
