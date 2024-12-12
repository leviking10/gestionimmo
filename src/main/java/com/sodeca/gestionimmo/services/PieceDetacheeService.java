package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.PieceDetacheeDTO;
import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.dto.InventaireDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PieceDetacheeService {
    PieceDetacheeDTO createPiece(PieceDetacheeDTO dto);
    PieceDetacheeDTO updatePiece(Long id, PieceDetacheeDTO dto);
    void deletePiece(Long id);
    PieceDetacheeDTO getPieceById(Long id);
    List<PieceDetacheeDTO> getAllPieces();
    List<InventaireDTO> getInventaire(); // Obtenir l'inventaire


    MouvementStockDTO validerDemande(Long demandeId);
    List<MouvementStockDTO> getMouvementsByPiece(Long pieceId);

    // Importation des pièces depuis un fichier
    List<PieceDetacheeDTO> importPiecesFromFile(MultipartFile file) throws IOException;

}
