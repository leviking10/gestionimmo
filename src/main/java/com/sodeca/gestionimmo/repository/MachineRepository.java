package com.sodeca.gestionimmo.repository;
import com.sodeca.gestionimmo.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    // Ajoutez des requêtes personnalisées si nécessaire
}