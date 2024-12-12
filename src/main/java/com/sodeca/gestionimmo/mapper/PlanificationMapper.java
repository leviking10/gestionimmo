package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.PlanificationDTO;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.entity.Planification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ImmobilisationMapper.class, PersonnelMapper.class})
public interface PlanificationMapper {

    // Mapping d'une entité Planification vers un DTO
    @Mapping(source = "immobilisation.id", target = "immobilisationId")
    @Mapping(source = "techniciens", target = "technicienIds", qualifiedByName = "techniciensToIds")
    PlanificationDTO toDTO(Planification planification);

    // Mapping d'un DTO Planification vers une entité
    @Mapping(source = "immobilisationId", target = "immobilisation", qualifiedByName = "fromId")
    @Mapping(source = "technicienIds", target = "techniciens", qualifiedByName = "idsToTechniciens")
    Planification toEntity(PlanificationDTO dto);

    // Conversion des techniciens vers leurs IDs
    @Named("techniciensToIds")
    default List<Long> techniciensToIds(List<Personnel> techniciens) {
        return techniciens != null ? techniciens.stream()
                .map(Personnel::getId)
                .toList() : null;
    }

    // Conversion des IDs vers une liste de techniciens
    @Named("idsToTechniciens")
    default List<Personnel> idsToTechniciens(List<Long> ids) {
        return ids != null ? ids.stream()
                .map(id -> {
                    Personnel personnel = new Personnel();
                    personnel.setId(id);
                    return personnel;
                }).toList() : null;
    }
}
