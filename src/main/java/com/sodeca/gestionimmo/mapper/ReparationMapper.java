package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.ReparationDTO;
import com.sodeca.gestionimmo.entity.Reparation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReparationMapper {
    ReparationDTO toDTO(Reparation entity);
    Reparation toEntity(ReparationDTO dto);
}
