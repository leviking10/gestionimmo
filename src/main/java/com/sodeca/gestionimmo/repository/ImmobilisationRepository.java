package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Immobilisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImmobilisationRepository extends JpaRepository<Immobilisation, Long> {
}
