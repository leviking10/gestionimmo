package com.sodeca.gestionimmo.repository;

import com.netflix.appinfo.ApplicationInfoManager;
import com.sodeca.gestionimmo.entity.Immobilisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImmobilisationRepository extends JpaRepository<Immobilisation, Long> {
    Optional<Immobilisation> findByCodeImmo(String codeImmo);

    Optional<Immobilisation> findTopByOrderByCodeImmoDesc(); // Récupère le dernier code existant
}
