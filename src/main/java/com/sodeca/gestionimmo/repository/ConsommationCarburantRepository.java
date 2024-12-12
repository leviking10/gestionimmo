package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.ConsommationCarburant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsommationCarburantRepository extends JpaRepository<ConsommationCarburant, Long> {
    List<ConsommationCarburant> findByVehiculeIdOrderByDateDesc(Long vehiculeId); // Historique par date
}
