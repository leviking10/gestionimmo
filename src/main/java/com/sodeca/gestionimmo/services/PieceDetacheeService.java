package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.PieceDetacheeDTO;
import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.dto.InventaireDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PieceDetacheeService {


    List<PieceDetacheeDTO> importPiecesFromFile(MultipartFile file) throws IOException;

    PieceDetacheeDTO createPiece(PieceDetacheeDTO dto);

    PieceDetacheeDTO updatePiece(Long id, PieceDetacheeDTO dto);

    void deletePiece(Long id);

    PieceDetacheeDTO getPieceById(Long id);

    List<PieceDetacheeDTO> getAllPieces();

    List<MouvementStockDTO> getMouvementsByPiece(Long pieceId);

    List<MouvementStockDTO> importApprovisionnements(MultipartFile file) throws IOException;

    MouvementStockDTO approvisionnement(Long pieceId, int quantite, String commentaire);

    MouvementStockDTO validerDemande(Long demandeId);

    List<InventaireDTO> getInventaire();
}
