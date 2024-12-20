package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import java.util.List;
public interface MouvementStockService {

    /**
     * Obtenir tous les mouvements de stock.
     *
     * @return Liste des mouvements de stock.
     */
    List<MouvementStockDTO> getAllMouvements();

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
