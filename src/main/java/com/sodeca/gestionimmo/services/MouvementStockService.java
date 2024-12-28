package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.enums.TypeMouvement;

import java.time.LocalDateTime;
import java.util.List;
public interface MouvementStockService {

    /**
     * Obtenir tous les mouvements de stock.
     *
     * @return Liste des mouvements de stock.
     */
    List<MouvementStockDTO> getAllMouvements();


    List<MouvementStockDTO> searchMouvements(TypeMouvement type, LocalDateTime startDate, LocalDateTime endDate);

    MouvementStockDTO createMouvement(MouvementStockDTO dto);

    /**
     * Obtenir les mouvements de stock pour une pièce donnée.
     *
     * @param pieceId ID de la pièce.
     * @return Liste des mouvements de stock associés à la pièce.
     */
    List<MouvementStockDTO> getMouvementsByPiece(Long pieceId);

    /**
     * Obtenir les mouvements de stock pour une pièce donnée via sa référence.
     *
     * @param reference Référence de la pièce.
     * @return Liste des mouvements de stock associés à la pièce.
     */
    List<MouvementStockDTO> getMouvementsByReference(String reference);
}
