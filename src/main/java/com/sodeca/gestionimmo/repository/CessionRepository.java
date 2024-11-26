package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Cession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CessionRepository extends JpaRepository<Cession, Long> {
    List<Cession> findByImmobilisationId(Long immobilisationId);
}
