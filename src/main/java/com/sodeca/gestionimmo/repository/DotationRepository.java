package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.Dotation;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotationRepository extends JpaRepository<Dotation, Long> {
    List<Dotation> findByTechnicien(Personnel technicien);
    List<Dotation> findByPiece(PieceDetachee piece);
}
