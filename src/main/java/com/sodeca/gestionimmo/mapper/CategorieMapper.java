package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.CategorieDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategorieMapper {
    // Conversion spécifique : Categorie -> CategorieDTO
    CategorieDTO toCategorieDTO(Categorie categorie);

    // Conversion spécifique : CategorieDTO -> Categorie
    Categorie toCategorie(CategorieDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateCategorieFromDto(CategorieDTO dto, @MappingTarget Categorie entity);

    default Categorie fromId(Long id) {
        if (id == null) {
            return null;
        }
        Categorie categorie = new Categorie();
        categorie.setId(id);
        return categorie;
    }
}
