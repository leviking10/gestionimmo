package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    List<MouvementStock> findByPiece(PieceDetachee piece);
}
