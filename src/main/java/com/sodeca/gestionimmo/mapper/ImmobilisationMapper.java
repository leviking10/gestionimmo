package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.*;
import com.sodeca.gestionimmo.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {CategorieMapper.class})
public interface ImmobilisationMapper {

    // Mapping d'une entité Immobilisation vers un DTO avec récupération dynamique de la durée d'amortissement
    @Mapping(source = "categorie.id", target = "categorieId")
    @Mapping(expression = "java(immobilisation.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    ImmobilisationDTO toDTO(Immobilisation immobilisation);

    // Mapping d'un DTO Immobilisation vers une entité
    @Mapping(source = "categorieId", target = "categorie")
    Immobilisation toEntity(ImmobilisationDTO dto);

    // Conversion spécifique : Telephone -> TelephoneDTO avec durée d'amortissement dynamique
    @Mapping(source = "categorie.id", target = "categorieId")
    @Mapping(expression = "java(telephone.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    TelephoneDTO toTelephoneDTO(Telephone telephone);

    // Conversion spécifique : TelephoneDTO -> Telephone
    @Mapping(source = "categorieId", target = "categorie")
    Telephone toTelephone(TelephoneDTO dto);

    // Conversion spécifique : Ordinateur -> OrdinateurDTO avec durée d'amortissement dynamique
    @Mapping(source = "categorie.id", target = "categorieId")
    @Mapping(expression = "java(ordinateur.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    OrdinateurDTO toOrdinateurDTO(Ordinateur ordinateur);

    // Conversion spécifique : OrdinateurDTO -> Ordinateur
    @Mapping(source = "categorieId", target = "categorie")
    Ordinateur toOrdinateur(OrdinateurDTO dto);

    // Conversion spécifique : Vehicule -> VehiculeDTO avec durée d'amortissement dynamique
    @Mapping(source = "categorie.id", target = "categorieId")
    @Mapping(expression = "java(vehicule.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    VehiculeDTO toVehiculeDTO(Vehicule vehicule);

    // Conversion spécifique : VehiculeDTO -> Vehicule
    @Mapping(source = "categorieId", target = "categorie")
    Vehicule toVehicule(VehiculeDTO dto);

    // Conversion spécifique : Machine -> MachineDTO avec durée d'amortissement dynamique
    @Mapping(source = "categorie.id", target = "categorieId")
    @Mapping(expression = "java(machine.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    MachineDTO toMachineDTO(Machine machine);

    // Conversion spécifique : MachineDTO -> Machine
    @Mapping(source = "categorieId", target = "categorie")
    Machine toMachine(MachineDTO dto);

    // Conversion spécifique : Mobilier -> MobilierDTO avec durée d'amortissement dynamique
    @Mapping(source = "categorie.id", target = "categorieId")
    @Mapping(expression = "java(mobilier.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    MobilierDTO toMobilierDTO(Mobilier mobilier);

    // Conversion spécifique : MobilierDTO -> Mobilier
    @Mapping(source = "categorieId", target = "categorie")
    Mobilier toMobilier(MobilierDTO dto);

    // Conversion polymorphique : Entité -> DTO
    default ImmobilisationDTO toPolymorphicDTO(Immobilisation immobilisation) {
        if (immobilisation instanceof Telephone) {
            return toTelephoneDTO((Telephone) immobilisation);
        } else if (immobilisation instanceof Ordinateur) {
            return toOrdinateurDTO((Ordinateur) immobilisation);
        } else if (immobilisation instanceof Vehicule) {
            return toVehiculeDTO((Vehicule) immobilisation);
        } else if (immobilisation instanceof Machine) {
            return toMachineDTO((Machine) immobilisation);
        } else if (immobilisation instanceof Mobilier) {
            return toMobilierDTO((Mobilier) immobilisation);
        } else {
            return toDTO(immobilisation);
        }
    }
    // Méthode pour mapper un Long vers une Immobilisation
    @Named("fromId")
    default Immobilisation fromId(Long id) {
        if (id == null) {
            return null;
        }
        Immobilisation immobilisation = new Immobilisation();
        immobilisation.setId(id);
        return immobilisation;
    }

    // Conversion polymorphique : DTO -> Entité
    default Immobilisation toPolymorphicEntity(ImmobilisationDTO dto) {
        if (dto instanceof TelephoneDTO) {
            return toTelephone((TelephoneDTO) dto);
        } else if (dto instanceof OrdinateurDTO) {
            return toOrdinateur((OrdinateurDTO) dto);
        } else if (dto instanceof VehiculeDTO) {
            return toVehicule((VehiculeDTO) dto);
        } else if (dto instanceof MachineDTO) {
            return toMachine((MachineDTO) dto);
        } else if (dto instanceof MobilierDTO) {
            return toMobilier((MobilierDTO) dto);
        } else {
            return toEntity(dto);
        }
    }
}
