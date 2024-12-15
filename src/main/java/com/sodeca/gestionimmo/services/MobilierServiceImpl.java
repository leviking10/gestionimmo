package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MobilierDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Mobilier;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.MobilierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MobilierServiceImpl implements MobilierService {

    private final MobilierRepository mobilierRepository;
    private final ImmobilisationMapper mapper;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationServiceImpl immobilisationService;

    public MobilierServiceImpl(
            MobilierRepository mobilierRepository,
            ImmobilisationMapper mapper,
            CategorieRepository categorieRepository,
            ImmobilisationServiceImpl immobilisationService
    ) {
        this.mobilierRepository = mobilierRepository;
        this.mapper = mapper;
        this.categorieRepository = categorieRepository;
        this.immobilisationService = immobilisationService;
    }

    @Override
    public List<MobilierDTO> getAllMobilier() {
        return mobilierRepository.findAll()
                .stream()
                .map(mapper::toMobilierDTO)
                .toList();
    }

    @Override
    public Optional<MobilierDTO> getMobilierById(Long id) {
        return mobilierRepository.findById(id)
                .map(mapper::toMobilierDTO);
    }

    @Override
    public MobilierDTO createMobilier(MobilierDTO dto) {
        Mobilier mobilier = mapper.toMobilier(dto);

        // Validation de la catégorie par désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérification si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        mobilier.setCategorie(categorie);

        // Génération du QR Code
        immobilisationService.generateAndAssignQRCode(mobilier, dto);

        // Sauvegarde
        Mobilier saved = mobilierRepository.save(mobilier);
        return mapper.toMobilierDTO(saved);
    }

    @Override
    public MobilierDTO updateMobilier(Long id, MobilierDTO dto) {
        Mobilier mobilier = mobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mobilier non trouvé avec l'ID : " + id));

        // Mise à jour des champs spécifiques
        mobilier.setTypeMobilier(dto.getTypeMobilier());
        mobilier.setMateriau(dto.getMateriau());
        // Mise à jour des champs communs
        mobilier.setDesignation(dto.getDesignation());
        mobilier.setDateAcquisition(dto.getDateAcquisition());
        mobilier.setValeurAcquisition(dto.getValeurAcquisition());
        mobilier.setLocalisation(dto.getLocalisation());

        // Validation de la catégorie par désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérification si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        mobilier.setCategorie(categorie);

        // Sauvegarde
        Mobilier updated = mobilierRepository.save(mobilier);
        return mapper.toMobilierDTO(updated);
    }

    @Override
    public void deleteMobilier(Long id) {
        if (!mobilierRepository.existsById(id)) {
            throw new RuntimeException("Mobilier non trouvé avec l'ID : " + id);
        }
        mobilierRepository.deleteById(id);
    }

    @Override
    public List<MobilierDTO> createMobiliers(List<MobilierDTO> dtos) {
        List<Mobilier> mobiliers = dtos.stream().map(dto -> {
            Mobilier mobilier = mapper.toMobilier(dto);

            // Validation de la catégorie par désignation
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

            // Vérification si la catégorie est active
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }

            mobilier.setCategorie(categorie);

            // Génération du QR Code
            immobilisationService.generateAndAssignQRCode(mobilier, dto);

            return mobilier;
        }).toList();

        List<Mobilier> savedMobiliers = mobilierRepository.saveAll(mobiliers);

        return savedMobiliers.stream()
                .map(mapper::toMobilierDTO)
                .toList();
    }
}
