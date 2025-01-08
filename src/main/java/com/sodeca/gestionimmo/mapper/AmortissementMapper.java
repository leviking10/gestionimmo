package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.AmortissementDTO;
import com.sodeca.gestionimmo.entity.Amortissement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AmortissementMapper {

    /**
     * Conversion de l'entité Amortissement en DTO.
     */
    @Mapping(source = "immobilisation.id", target = "idImmobilisation")
    AmortissementDTO toDTO(Amortissement amortissement);

    /**
     * Conversion du DTO AmortissementDTO en entité Amortissement.
     */
    @Mapping(source = "idImmobilisation", target = "immobilisation.id")
    Amortissement toEntity(AmortissementDTO dto);

    List<AmortissementDTO> toDTOList(List<Amortissement> amortissements);
}
