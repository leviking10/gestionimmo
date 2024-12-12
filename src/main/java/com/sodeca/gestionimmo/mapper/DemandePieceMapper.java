package com.sodeca.gestionimmo.mapper;
import com.sodeca.gestionimmo.dto.DemandePieceDTO;
import com.sodeca.gestionimmo.entity.DemandePiece;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PersonnelMapper.class, PieceDetacheeMapper.class})
public interface DemandePieceMapper {
    @Mapping(source = "piece.id", target = "pieceId")
    @Mapping(source = "technicien.id", target = "technicienId")
    DemandePieceDTO toDTO(DemandePiece demande);

    @Mapping(source = "pieceId", target = "piece")
    @Mapping(source = "technicienId", target = "technicien")
    DemandePiece toEntity(DemandePieceDTO dto);
}
