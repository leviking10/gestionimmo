package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.entity.MouvementStock;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.mapper.MouvementStockMapper;
import com.sodeca.gestionimmo.repository.MouvementStockRepository;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MouvementStockServiceImpl implements MouvementStockService {

    private final MouvementStockRepository mouvementRepository;
    private final PieceDetacheeRepository pieceRepository;
    private final MouvementStockMapper mouvementMapper;

    public MouvementStockServiceImpl(MouvementStockRepository mouvementRepository,
                                     PieceDetacheeRepository pieceRepository,
                                     MouvementStockMapper mouvementMapper) {
        this.mouvementRepository = mouvementRepository;
        this.pieceRepository = pieceRepository;
        this.mouvementMapper = mouvementMapper;
    }

    @Override
    public List<MouvementStockDTO> getAllMouvements() {
        return mouvementRepository.findAll().stream()
                .map(mouvementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MouvementStockDTO> getMouvementsByPiece(Long pieceId) {
        PieceDetachee piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec l'ID : " + pieceId));
        return mouvementRepository.findByPiece(piece).stream()
                .map(mouvementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MouvementStockDTO> getMouvementsByReference(String reference) {
        PieceDetachee piece = pieceRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable avec la référence : " + reference));
        return mouvementRepository.findByPiece(piece).stream()
                .map(mouvementMapper::toDTO)
                .collect(Collectors.toList());
    }
}
