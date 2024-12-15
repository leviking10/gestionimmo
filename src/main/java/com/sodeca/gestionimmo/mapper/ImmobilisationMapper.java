package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.*;
import com.sodeca.gestionimmo.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {CategorieMapper.class})
public interface ImmobilisationMapper {

    // Mapping d'une entité Immobilisation vers un DTO avec récupération de la désignation et de la durée d'amortissement
    @Mapping(source = "categorie.categorie", target = "categorieDesignation") // Désignation de la catégorie
    @Mapping(expression = "java(immobilisation.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    ImmobilisationDTO toDTO(Immobilisation immobilisation);

    // Mapping d'un DTO Immobilisation vers une entité avec récupération de la catégorie par sa désignation
    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation") // Récupération par désignation
    Immobilisation toEntity(ImmobilisationDTO dto);

    // Conversion spécifique : Telephone -> TelephoneDTO
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(telephone.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    TelephoneDTO toTelephoneDTO(Telephone telephone);

    // Conversion spécifique : TelephoneDTO -> Telephone
    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Telephone toTelephone(TelephoneDTO dto);

    // Conversion spécifique : Ordinateur -> OrdinateurDTO
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(ordinateur.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    OrdinateurDTO toOrdinateurDTO(Ordinateur ordinateur);

    // Conversion spécifique : OrdinateurDTO -> Ordinateur
    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Ordinateur toOrdinateur(OrdinateurDTO dto);

    // Conversion spécifique : Vehicule -> VehiculeDTO
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(vehicule.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    VehiculeDTO toVehiculeDTO(Vehicule vehicule);

    // Conversion spécifique : VehiculeDTO -> Vehicule
    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Vehicule toVehicule(VehiculeDTO dto);

    // Conversion spécifique : Machine -> MachineDTO
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(machine.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    MachineDTO toMachineDTO(Machine machine);

    // Conversion spécifique : MachineDTO -> Machine
    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Machine toMachine(MachineDTO dto);

    // Conversion spécifique : Mobilier -> MobilierDTO
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(mobilier.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    MobilierDTO toMobilierDTO(Mobilier mobilier);

    // Conversion spécifique : MobilierDTO -> Mobilier
    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
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

    // Méthode utilitaire pour mapper une désignation de catégorie vers une entité Categorie
    @Named("fromDesignation")
    default Categorie fromDesignation(String designation) {
        if (designation == null) {
            return null;
        }
        Categorie categorie = new Categorie();
        categorie.setCategorie(designation);
        return categorie;
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
}
