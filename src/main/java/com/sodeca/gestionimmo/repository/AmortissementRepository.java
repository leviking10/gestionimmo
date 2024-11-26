package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Amortissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmortissementRepository extends JpaRepository<Amortissement, Integer> {
    List<Amortissement> findByImmobilisationId(Long immobilisationId);
}
