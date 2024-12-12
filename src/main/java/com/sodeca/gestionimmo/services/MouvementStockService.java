package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
public interface MouvementStockService {

    /**
     * Enregistrer un mouvement de stock.
     *
     * @param dto Mouvement de stock à enregistrer.
     * @return Mouvement de stock enregistré.
     */
    MouvementStockDTO enregistrerMouvement(MouvementStockDTO dto);

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
    List<MouvementStockDTO> importStockMovements(MultipartFile file) throws IOException;
}
