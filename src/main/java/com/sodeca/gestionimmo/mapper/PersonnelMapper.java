package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.PersonnelDTO;
import com.sodeca.gestionimmo.entity.Personnel;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PersonnelMapper {

    /**
     * Mapping d'une entité Personnel vers un DTO PersonnelDTO.
     *
     * @param personnel L'entité Personnel.
     * @return Le DTO correspondant.
     */
    PersonnelDTO toDTO(Personnel personnel);

    /**
     * Mapping d'un DTO PersonnelDTO vers une entité Personnel.
     *
     * @param dto Le DTO PersonnelDTO.
     * @return L'entité correspondante.
     */
    Personnel toEntity(PersonnelDTO dto);
    default Personnel fromId(Long id) {
        if (id == null) return null;
        Personnel personnel = new Personnel();
        personnel.setId(id);
        return personnel;
    }

}
