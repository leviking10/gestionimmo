package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.InterventionDTO;
import com.sodeca.gestionimmo.entity.Intervention;
import org.mapstruct.*;
@Mapper(componentModel = "spring", uses = {ImmobilisationMapper.class, PersonnelMapper.class})
public interface InterventionMapper {

    @Mapping(source = "immobilisation.id", target = "immobilisationId")
    @Mapping(source = "technicien.id", target = "technicienId")
    InterventionDTO toDTO(Intervention intervention);

    @Mapping(source = "immobilisationId", target = "immobilisation")
    @Mapping(source = "technicienId", target = "technicien")
    Intervention toEntity(InterventionDTO dto);

    @Named("fromId")
    default Intervention fromId(Long id) {
        if (id == null) {
            return null;
        }
        Intervention intervention = new Intervention();
        intervention.setId(id);
        return intervention;
    }
}

