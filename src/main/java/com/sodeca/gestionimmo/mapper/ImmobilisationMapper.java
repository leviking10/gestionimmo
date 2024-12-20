package com.sodeca.gestionimmo.mapper;

import com.sodeca.gestionimmo.dto.*;
import com.sodeca.gestionimmo.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {CategorieMapper.class})
public interface ImmobilisationMapper {

    // Conversion générique pour Immobilisation -> ImmobilisationDTO
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(immobilisation.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    @Mapping(source = "type", target = "type")
    ImmobilisationDTO toDTO(Immobilisation immobilisation);

    // Conversion générique pour ImmobilisationDTO -> Immobilisation
    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    @Mapping(source = "type", target = "type")
    Immobilisation toEntity(ImmobilisationDTO dto);

    // Conversion spécifique pour Ordinateur
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(ordinateur.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    OrdinateurDTO toOrdinateurDTO(Ordinateur ordinateur);

    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Ordinateur toOrdinateur(OrdinateurDTO dto);

    // Conversion spécifique pour Telephone
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(telephone.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    TelephoneDTO toTelephoneDTO(Telephone telephone);

    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Telephone toTelephone(TelephoneDTO dto);

    // Conversion spécifique pour Vehicule
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(vehicule.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    VehiculeDTO toVehiculeDTO(Vehicule vehicule);

    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Vehicule toVehicule(VehiculeDTO dto);

    // Conversion spécifique pour Machine
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(machine.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    MachineDTO toMachineDTO(Machine machine);

    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Machine toMachine(MachineDTO dto);

    // Conversion spécifique pour Mobilier
    @Mapping(source = "categorie.categorie", target = "categorieDesignation")
    @Mapping(expression = "java(mobilier.getCategorie().getDureeAmortissement())", target = "dureeAmortissement")
    MobilierDTO toMobilierDTO(Mobilier mobilier);

    @Mapping(source = "categorieDesignation", target = "categorie", qualifiedByName = "fromDesignation")
    Mobilier toMobilier(MobilierDTO dto);

    // Conversion polymorphique : Immobilisation -> ImmobilisationDTO
    default ImmobilisationDTO toPolymorphicDTO(Immobilisation immobilisation) {
        if (immobilisation == null) {
            return null;
        }

        if (immobilisation instanceof Ordinateur ordinateur) {
            return toOrdinateurDTO(ordinateur);
        } else if (immobilisation instanceof Telephone telephone) {
            return toTelephoneDTO(telephone);
        } else if (immobilisation instanceof Vehicule vehicule) {
            return toVehiculeDTO(vehicule);
        } else if (immobilisation instanceof Machine machine) {
            return toMachineDTO(machine);
        } else if (immobilisation instanceof Mobilier mobilier) {
            return toMobilierDTO(mobilier);
        }

        return toDTO(immobilisation);
    }

    // Conversion polymorphique : ImmobilisationDTO -> Immobilisation
    default Immobilisation toPolymorphicEntity(ImmobilisationDTO dto) {
        if (dto == null || dto.getType() == null) {
            throw new IllegalArgumentException("DTO ou type d'immobilisation invalide");
        }

        // Crée l'entité appropriée en fonction du type
        Immobilisation entity;
        switch (dto.getType()) {
            case ORDINATEUR:
                entity = new Ordinateur();
                break;
            case TELEPHONE:
                entity = new Telephone();
                break;
            case VEHICULE:
                entity = new Vehicule();
                break;
            case MACHINE:
                entity = new Machine();
                break;
            case MOBILIER:
                entity = new Mobilier();
                break;
            default:
                throw new IllegalArgumentException("Type d'immobilisation non pris en charge : " + dto.getType());
        }

        // Copie les champs communs
        entity.setId(dto.getId());
        entity.setCodeImmo(dto.getCodeImmo());
        entity.setDesignation(dto.getDesignation());
        entity.setCategorie(new Categorie()); // Assurez-vous que `Categorie` est correctement mappé
        entity.getCategorie().setCategorie(dto.getCategorieDesignation());
        entity.setDateAcquisition(dto.getDateAcquisition());
        entity.setValeurAcquisition(dto.getValeurAcquisition());
        entity.setLocalisation(dto.getLocalisation());
        entity.setQrCode(dto.getQrCode());
        entity.setDateMiseEnService(dto.getDateMiseEnService());
        entity.setStatut(dto.getStatutAffectation());
        entity.setEtatImmo(dto.getEtatImmobilisation());
        entity.setTypeAmortissement(dto.getTypeAmortissement());
        entity.setCreatedDate(null); // Si nécessaire, laissez Hibernate gérer
        entity.setLastModifiedDate(null);

        return entity;
    }


    // Méthode utilitaire : Désignation de catégorie -> Entité Categorie
    @Named("fromDesignation")
    default Categorie fromDesignation(String designation) {
        if (designation == null) {
            return null;
        }
        Categorie categorie = new Categorie();
        categorie.setCategorie(designation);
        return categorie;
    }
    default Immobilisation fromId(Long id) {
        if (id == null) {
            return null;
        }
        Immobilisation immobilisation = new Immobilisation();
        immobilisation.setId(id);
        return immobilisation;
    }
}
