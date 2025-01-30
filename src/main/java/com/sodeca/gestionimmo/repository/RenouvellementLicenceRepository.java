package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.RenouvellementLicence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RenouvellementLicenceRepository extends JpaRepository<RenouvellementLicence, Long> {
    List<RenouvellementLicence> findByLicenceId(Long licenceId);
}
