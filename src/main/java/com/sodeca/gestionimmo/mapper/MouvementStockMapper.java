package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.MouvementStockDTO;
import com.sodeca.gestionimmo.entity.MouvementStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PieceDetacheeMapper.class})
public interface MouvementStockMapper {

    // Mapping de l'entité vers le DTO
    @Mapping(source = "piece.id", target = "pieceId")
    @Mapping(source = "piece.reference", target = "referencePiece") // Ajout de referencePiece
    MouvementStockDTO toDTO(MouvementStock mouvement);

    // Mapping du DTO vers l'entité
    @Mapping(source = "pieceId", target = "piece.id")
    @Mapping(source = "referencePiece", target = "piece.reference") // Ajout de referencePiece
    MouvementStock toEntity(MouvementStockDTO dto);
}
