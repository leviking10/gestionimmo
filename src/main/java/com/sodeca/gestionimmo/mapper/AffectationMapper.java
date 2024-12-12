package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.AffectationDTO;
import com.sodeca.gestionimmo.entity.Affectation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AffectationMapper {
    @Mapping(source = "immobilisation.id", target = "immobilisationId")
    @Mapping(source = "personnel.id", target = "personnelId")
    AffectationDTO toDTO(Affectation affectation);

    @Mapping(target = "immobilisation", ignore = true) // Association à faire dans le service
    @Mapping(target = "personnel", ignore = true)
    Affectation toEntity(AffectationDTO dto);
}
