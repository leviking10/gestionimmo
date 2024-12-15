package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.dto.OrdinateurDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Ordinateur;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.OrdinateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdinateurServiceImpl implements OrdinateurService {

    private final OrdinateurRepository ordinateurRepository;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationMapper mapper;
    private final ImmobilisationServiceImpl immobilisationService;

    public OrdinateurServiceImpl(OrdinateurRepository ordinateurRepository, CategorieRepository categorieRepository, ImmobilisationMapper mapper, ImmobilisationServiceImpl immobilisationService) {
        this.ordinateurRepository = ordinateurRepository;
        this.categorieRepository = categorieRepository;
        this.mapper = mapper;
        this.immobilisationService = immobilisationService;
    }

    @Override
    public List<OrdinateurDTO> getAllOrdinateurs() {
        return ordinateurRepository.findAll()
                .stream()
                .map(mapper::toOrdinateurDTO)
                .toList();
    }

    @Override
    public Optional<OrdinateurDTO> getOrdinateurById(Long id) {
        return ordinateurRepository.findById(id)
                .map(mapper::toOrdinateurDTO);
    }

    @Override
    public OrdinateurDTO createOrdinateur(OrdinateurDTO dto) {
        // Convertir DTO en entité
        Ordinateur ordinateur = mapper.toOrdinateur(dto);

        // Récupérer la catégorie associée par sa désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        // Assigner la catégorie
        ordinateur.setCategorie(categorie);

        // Générer et assigner le QR Code via ImmobilisationServiceImpl
        immobilisationService.generateAndAssignQRCode(ordinateur, dto);

        // Sauvegarder l'ordinateur dans la base de données
        Ordinateur saved = ordinateurRepository.save(ordinateur);
        return mapper.toOrdinateurDTO(saved);
    }

    @Override
    public OrdinateurDTO updateOrdinateur(Long id, OrdinateurDTO dto) {
        // Récupérer l'ordinateur existant
        Ordinateur ordinateur = ordinateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordinateur non trouvé avec l'ID : " + id));
        // Mettre à jour les champs spécifiques à Ordinateur
        ordinateur.setMarque(dto.getMarque());
        ordinateur.setModele(dto.getModele());
        ordinateur.setProcesseur(dto.getProcesseur());
        ordinateur.setRam(dto.getRam());
        ordinateur.setDisqueDur(dto.getDisqueDur());
        ordinateur.setOs(dto.getOs());
        ordinateur.setEtat(dto.getEtat());
        ordinateur.setNumeroSerie(dto.getNumeroSerie());
        ordinateur.setDateMiseEnService(dto.getDateMiseEnService());
        ordinateur.setType(dto.getType());
        // Mettre à jour les champs communs de la classe mère
        ordinateur.setDesignation(dto.getDesignation());
        ordinateur.setLocalisation(dto.getLocalisation());
        ordinateur.setValeurAcquisition(dto.getValeurAcquisition());
        ordinateur.setDateAcquisition(dto.getDateAcquisition());

        // Récupérer la catégorie associée par sa désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        // Assigner la catégorie
        ordinateur.setCategorie(categorie);

        // Sauvegarder les modifications
        Ordinateur updated = ordinateurRepository.save(ordinateur);

        // Retourner le DTO
        return mapper.toOrdinateurDTO(updated);
    }

    @Override
    public void deleteOrdinateur(Long id) {
        if (!ordinateurRepository.existsById(id)) {
            throw new RuntimeException("Ordinateur non trouvé avec l'ID : " + id);
        }
        ordinateurRepository.deleteById(id);
    }

    @Override
    public List<OrdinateurDTO> createOrdinateurs(List<ImmobilisationDTO> dtos) {
        // Filtrer les DTOs pour ne garder que les instances d'OrdinateurDTO
        List<OrdinateurDTO> ordinateurDTOs = dtos.stream()
                .filter(dto -> dto instanceof OrdinateurDTO)
                .map(dto -> (OrdinateurDTO) dto)
                .toList();

        // Convertir les DTOs en entités
        List<Ordinateur> ordinateurs = ordinateurDTOs.stream().map(dto -> {
            // Convertir DTO en entité Ordinateur
            Ordinateur ordinateur = mapper.toOrdinateur(dto);

            // Récupérer la catégorie associée par sa désignation
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

            // Vérifier si la catégorie est active
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }

            // Assigner la catégorie
            ordinateur.setCategorie(categorie);

            // Générer et assigner un QR Code
            immobilisationService.generateAndAssignQRCode(ordinateur, dto);

            return ordinateur;
        }).toList();

        // Sauvegarder toutes les entités
        List<Ordinateur> savedOrdinateurs = ordinateurRepository.saveAll(ordinateurs);

        // Convertir les entités sauvegardées en DTOs
        return savedOrdinateurs.stream()
                .map(mapper::toOrdinateurDTO)
                .toList();
    }
}
