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
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
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
        ordinateur.setUtilisateur(dto.getUtilisateur());
        ordinateur.setDateMiseEnService(dto.getDateMiseEnService());
        ordinateur.setType(dto.getType());

        // Mettre à jour les champs communs de la classe mère
        ordinateur.setDesignation(dto.getDesignation());
        ordinateur.setLocalisation(dto.getLocalisation());
        ordinateur.setValeurAcquisition(dto.getValeurAcquisition());

        ordinateur.setDateAcquisition(dto.getDateAcquisition());

        // Convertir categorieId en entité Categorie
        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + dto.getCategorieId()));
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
        // Vérifier que tous les DTO sont des instances d'OrdinateurDTO
        List<OrdinateurDTO> ordinateurDTOs = dtos.stream()
                .filter(dto -> dto instanceof OrdinateurDTO)
                .map(dto -> (OrdinateurDTO) dto)
                .collect(Collectors.toList());

        // Convertir les DTOs en entités
        List<Ordinateur> ordinateurs = ordinateurDTOs.stream().map(dto -> {
            // Convertir DTO en entité Ordinateur
            Ordinateur ordinateur = mapper.toOrdinateur(dto);

            // Récupérer la catégorie associée
            Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + dto.getCategorieId()));
            ordinateur.setCategorie(categorie);

            // Générer et assigner un QR Code
            immobilisationService.generateAndAssignQRCode(ordinateur, dto);

            return ordinateur;
        }).collect(Collectors.toList());

        // Sauvegarder toutes les entités
        List<Ordinateur> savedOrdinateurs = ordinateurRepository.saveAll(ordinateurs);

        // Convertir les entités sauvegardées en DTOs
        return savedOrdinateurs.stream()
                .map(mapper::toOrdinateurDTO)
                .collect(Collectors.toList());
    }

}
