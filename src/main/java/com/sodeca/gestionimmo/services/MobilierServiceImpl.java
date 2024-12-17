package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.MobilierDTO;
import com.sodeca.gestionimmo.entity.Mobilier;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.MobilierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MobilierServiceImpl implements MobilierService {

    private final MobilierRepository mobilierRepository;
    private final ImmobilisationMapper mapper;

    public MobilierServiceImpl(MobilierRepository mobilierRepository, ImmobilisationMapper mapper) {
        this.mobilierRepository = mobilierRepository;
        this.mapper = mapper;
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
    public MobilierDTO updateMobilier(Long id, MobilierDTO dto) {
        try {
            // Récupération de l'entité Mobilier existante
            Mobilier mobilier = mobilierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mobilier non trouvé avec l'ID : " + id));
            // Mise à jour des champs spécifiques
            mobilier.setTypeMobilier(dto.getTypeMobilier());
            mobilier.setMateriau(dto.getMateriau());
            // Sauvegarde des modifications
            Mobilier updated = mobilierRepository.save(mobilier);

            // Conversion et retour du DTO mis à jour
            return mapper.toMobilierDTO(updated);
        } catch (RuntimeException e) {
            // Gestion des exceptions
            throw new RuntimeException("Erreur lors de la mise à jour du mobilier : " + e.getMessage(), e);
        } catch (Exception e) {
            // Capture des exceptions générales
            throw new RuntimeException("Une erreur inattendue est survenue lors de la mise à jour du mobilier.", e);
        }
    }
}
