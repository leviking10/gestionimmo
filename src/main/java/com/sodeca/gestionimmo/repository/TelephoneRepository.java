package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Telephone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelephoneRepository extends JpaRepository<Telephone, Long> {
}