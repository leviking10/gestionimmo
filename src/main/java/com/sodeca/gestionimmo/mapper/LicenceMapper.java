package com.sodeca.gestionimmo.mapper;
import com.sodeca.gestionimmo.dto.LicenceDTO;
import com.sodeca.gestionimmo.entity.Licence;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LicenceMapper {
    // Convertit une entité en DTO
    LicenceDTO toDTO(Licence licence);

    // Convertit un DTO en entité
    Licence toEntity(LicenceDTO licenceDTO);

    default Licence fromId(Long id) {
        if (id == null) {
            return null;
        }
        Licence licence = new Licence();
        licence.setId(id);
        return licence;
    }
}