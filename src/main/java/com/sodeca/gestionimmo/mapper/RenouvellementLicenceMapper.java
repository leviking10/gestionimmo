package com.sodeca.gestionimmo.mapper;
import com.sodeca.gestionimmo.dto.RenouvellementLicenceDTO;
import com.sodeca.gestionimmo.entity.RenouvellementLicence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {LicenceMapper.class})
public interface RenouvellementLicenceMapper {
    // Convertit une entité en DTO
    @Mapping(source = "licence.id", target = "licenceId")
    RenouvellementLicenceDTO toDTO(RenouvellementLicence renouvellementLicence);

    // Convertit un DTO en entité
    @Mapping(source = "licenceId", target = "licence")
    RenouvellementLicence toEntity(RenouvellementLicenceDTO renouvellementLicenceDTO);

    default RenouvellementLicence fromId(Long id) {
        if (id == null) {
            return null;
        }
        RenouvellementLicence renouvellementLicence = new RenouvellementLicence();
        renouvellementLicence.setId(id);
        return renouvellementLicence;
    }
}
