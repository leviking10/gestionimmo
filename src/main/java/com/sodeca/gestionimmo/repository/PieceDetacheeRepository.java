package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.PieceDetachee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PieceDetacheeRepository extends JpaRepository<PieceDetachee, Long> {
    Optional<PieceDetachee> findByReference(String reference);

}
