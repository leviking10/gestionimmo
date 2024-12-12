package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.DotationDTO;
import com.sodeca.gestionimmo.entity.Dotation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DotationMapper {
    DotationDTO toDTO(Dotation dotation);
    Dotation toEntity(DotationDTO dto);
}
