package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.PieceDetacheeDTO;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PieceDetacheeMapper {
    PieceDetacheeDTO toDTO(PieceDetachee piece);
    PieceDetachee toEntity(PieceDetacheeDTO dto);

    default PieceDetachee fromId(Long id) {
        if (id == null) {
            return null;
        }
        PieceDetachee pieceDetachee = new PieceDetachee();
        pieceDetachee.setId(id);
        return pieceDetachee;
    }
}
