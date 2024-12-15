package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.VehiculeDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Vehicule;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final ImmobilisationMapper mapper;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationServiceImpl immobilisationService;

    public VehiculeServiceImpl(VehiculeRepository vehiculeRepository, ImmobilisationMapper mapper, CategorieRepository categorieRepository, ImmobilisationServiceImpl immobilisationService) {
        this.vehiculeRepository = vehiculeRepository;
        this.mapper = mapper;
        this.categorieRepository = categorieRepository;
        this.immobilisationService = immobilisationService;
    }

    @Override
    public List<VehiculeDTO> getAllVehicules() {
        return vehiculeRepository.findAll()
                .stream()
                .map(mapper::toVehiculeDTO)
                .toList();
    }

    @Override
    public Optional<VehiculeDTO> getVehiculeById(Long id) {
        return vehiculeRepository.findById(id)
                .map(mapper::toVehiculeDTO);
    }

    @Override
    public VehiculeDTO createVehicule(VehiculeDTO dto) {
        // Convertir DTO en entité
        Vehicule vehicule = mapper.toVehicule(dto);

        // Récupérer la catégorie associée par sa désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        // Assigner la catégorie
        vehicule.setCategorie(categorie);

        // Générer et assigner le QR Code via ImmobilisationServiceImpl
        immobilisationService.generateAndAssignQRCode(vehicule, dto);

        // Sauvegarder le véhicule dans la base de données
        Vehicule saved = vehiculeRepository.save(vehicule);
        return mapper.toVehiculeDTO(saved);
    }

    @Override
    public VehiculeDTO updateVehicule(Long id, VehiculeDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID : " + id));

        // Mise à jour des champs spécifiques à Véhicule
        vehicule.setMarque(dto.getMarque());
        vehicule.setModele(dto.getModele());
        vehicule.setImmatriculation(dto.getImmatriculation());
        vehicule.setKilometrage(dto.getKilometrage());
        vehicule.setDateDerniereRevision(dto.getDateDerniereRevision());
        vehicule.setDateMiseEnService(dto.getDateMiseEnService());

        // Mise à jour des champs communs de la classe mère
        vehicule.setDesignation(dto.getDesignation());
        vehicule.setDateAcquisition(dto.getDateAcquisition());
        vehicule.setLocalisation(dto.getLocalisation());
        vehicule.setValeurAcquisition(dto.getValeurAcquisition());

        // Récupérer la catégorie associée par sa désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        // Assigner la catégorie
        vehicule.setCategorie(categorie);

        Vehicule updated = vehiculeRepository.save(vehicule);
        return mapper.toVehiculeDTO(updated);
    }

    @Override
    public void deleteVehicule(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new RuntimeException("Véhicule non trouvé avec l'ID : " + id);
        }
        vehiculeRepository.deleteById(id);
    }
    @Override
    public List<VehiculeDTO> createVehicules(List<VehiculeDTO> dtos) {
        List<Vehicule> vehicules = dtos.stream().map(dto -> {
            Vehicule vehicule = mapper.toVehicule(dto);

            // Validation de la catégorie par désignation
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

            // Vérification si la catégorie est active
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }

            vehicule.setCategorie(categorie);

            // Génération du QR Code
            immobilisationService.generateAndAssignQRCode(vehicule, dto);

            return vehicule;
        }).toList();

        List<Vehicule> savedVehicules = vehiculeRepository.saveAll(vehicules);

        return savedVehicules.stream()
                .map(mapper::toVehiculeDTO)
                .toList();
    }
}
