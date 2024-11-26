package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.CessionDTO;
import com.sodeca.gestionimmo.entity.Cession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ImmobilisationMapper.class})
public interface CessionMapper {

    // Mapping d'une entité Cession vers un DTO
    @Mapping(source = "immobilisation.id", target = "id")
    CessionDTO toDTO(Cession cession);

    // Mapping d'un DTO Cession vers une entité
    @Mapping(target = "immobilisation", ignore = true) // L'immobilisation sera associée au niveau du service
    Cession toEntity(CessionDTO dto);
}
