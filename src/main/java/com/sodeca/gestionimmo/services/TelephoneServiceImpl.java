package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.dto.TelephoneDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Telephone;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.TelephoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TelephoneServiceImpl implements TelephoneService {

    private final TelephoneRepository telephoneRepository;
    private final ImmobilisationMapper mapper;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationServiceImpl immobilisationService;

    public TelephoneServiceImpl(TelephoneRepository telephoneRepository, ImmobilisationMapper mapper, CategorieRepository categorieRepository, ImmobilisationServiceImpl immobilisationService) {
        this.telephoneRepository = telephoneRepository;
        this.mapper = mapper;
        this.categorieRepository = categorieRepository;
        this.immobilisationService = immobilisationService;
    }

    /**
     * Récupérer tous les téléphones.
     *
     * @return Liste des TelephoneDTO.
     */
    @Override
    public List<TelephoneDTO> getAllTelephones() {
        return telephoneRepository.findAll()
                .stream()
                .map(mapper::toTelephoneDTO)
                .toList();
    }

    /**
     * Récupérer un téléphone par ID.
     *
     * @param id ID du téléphone.
     * @return Un Optional contenant le TelephoneDTO si trouvé.
     */
    @Override
    public Optional<TelephoneDTO> getTelephoneById(Long id) {
        return telephoneRepository.findById(id)
                .map(mapper::toTelephoneDTO);
    }

    /**
     * Créer un nouveau téléphone.
     *
     * @param dto TelephoneDTO à créer.
     * @return Le TelephoneDTO créé.
     */
    @Override
    public TelephoneDTO createTelephone(TelephoneDTO dto) {
        // Convertir DTO en entité
        Telephone telephone = mapper.toTelephone(dto);

        // Générer et assigner le QR Code via ImmobilisationServiceImpl
        immobilisationService.generateAndAssignQRCode(telephone, dto);

        // Sauvegarder le téléphone dans la base de données
        Telephone saved = telephoneRepository.save(telephone);
        return mapper.toTelephoneDTO(saved);
    }

    /**
     * Mettre à jour un téléphone existant.
     *
     * @param id  ID du téléphone à mettre à jour.
     * @param dto TelephoneDTO contenant les nouvelles données.
     * @return Le TelephoneDTO mis à jour.
     */
    @Override
    public TelephoneDTO updateTelephone(Long id, TelephoneDTO dto) {
        Telephone telephone = telephoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Téléphone non trouvé avec l'ID : " + id));

        // Mise à jour des champs spécifiques à Telephone
        telephone.setMarque(dto.getMarque());
        telephone.setModele(dto.getModele());
        telephone.setType(dto.getType());
        telephone.setImei(dto.getImei());
        telephone.setNumeroSerie(dto.getNumeroSerie());
        telephone.setEtat(dto.getEtat());
        telephone.setUtilisateur(dto.getUtilisateur());
        telephone.setDateMiseEnService(dto.getDateMiseEnService());

        // Mise à jour des champs communs de la classe mère
        telephone.setDesignation(dto.getDesignation());
        telephone.setDateAcquisition(dto.getDateAcquisition());
        telephone.setLocalisation(dto.getLocalisation());
        telephone.setValeurAcquisition(dto.getValeurAcquisition());
        // Convertir categorieId en entité Categorie
        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + dto.getCategorieId()));
        telephone.setCategorie(categorie);
        Telephone updated = telephoneRepository.save(telephone);
        return mapper.toTelephoneDTO(updated);
    }

    /**
     * Supprimer un téléphone par ID.
     *
     * @param id ID du téléphone à supprimer.
     */
    @Override
    public void deleteTelephone(Long id) {
        if (!telephoneRepository.existsById(id)) {
            throw new RuntimeException("Téléphone non trouvé avec l'ID : " + id);
        }
        telephoneRepository.deleteById(id);
    }

    @Override
    public List<TelephoneDTO> createTelephones(List<ImmobilisationDTO> dtos) {
        // Vérifier que tous les DTO sont des instances d'OrdinateurDTO
        List<TelephoneDTO> telephoneDTOS = dtos.stream()
                .filter(dto -> dto instanceof TelephoneDTO)
                .map(dto -> (TelephoneDTO) dto)
                .toList();

        // Convertir les DTOs en entités
        List<Telephone> telephones = telephoneDTOS.stream().map(dto -> {
            // Convertir DTO en entité Ordinateur
            Telephone telephone = mapper.toTelephone(dto);

            // Récupérer la catégorie associée
            Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + dto.getCategorieId()));
            telephone.setCategorie(categorie);

            // Générer et assigner un QR Code
            immobilisationService.generateAndAssignQRCode(telephone, dto);

            return telephone;
        }).toList();

        // Sauvegarder toutes les entités
        List<Telephone> savedTelephones = telephoneRepository.saveAll(telephones);

        // Convertir les entités sauvegardées en DTOs
        return savedTelephones.stream()
                .map(mapper::toTelephoneDTO)
                .toList();
    }
}
