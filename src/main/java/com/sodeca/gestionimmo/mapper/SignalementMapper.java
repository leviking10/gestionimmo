package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.SignalementDTO;
import com.sodeca.gestionimmo.entity.Signalement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PersonnelMapper.class, ImmobilisationMapper.class})
public interface SignalementMapper {

    /**
     * Mapping d'une entité Signalement vers un DTO SignalementDTO.
     *
     * @param signalement L'entité Signalement.
     * @return Le DTO correspondant.
     */
    @Mapping(source = "personnel.id", target = "personnelId")
    @Mapping(source = "immobilisation.id", target = "immobilisationId")
    SignalementDTO toDTO(Signalement signalement);

    /**
     * Mapping d'un DTO SignalementDTO vers une entité Signalement.
     *
     * @param dto Le DTO SignalementDTO.
     * @return L'entité correspondante.
     */
    @Mapping(target = "personnel", ignore = true) // Gestion associée au niveau du service
    @Mapping(target = "immobilisation", ignore = true) // Gestion associée au niveau du service
    Signalement toEntity(SignalementDTO dto);
}
