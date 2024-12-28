package com.sodeca.gestionimmo.repository;

import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.enums.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    List<MouvementStock> findByPiece(PieceDetachee piece);
    List<MouvementStock> findByTypeMouvement(TypeMouvement typeMouvement);
    List<MouvementStock> findByTypeMouvementAndDateMouvementBetween(TypeMouvement type, LocalDateTime startDate, LocalDateTime endDate);
}
