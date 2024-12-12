package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.ConsommationCarburantDTO;
import com.sodeca.gestionimmo.entity.ConsommationCarburant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsommationCarburantMapper {
    ConsommationCarburantDTO toDTO(ConsommationCarburant entity);
    ConsommationCarburant toEntity(ConsommationCarburantDTO dto);
}
