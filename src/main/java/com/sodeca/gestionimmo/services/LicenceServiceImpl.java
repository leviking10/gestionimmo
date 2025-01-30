package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.LicenceDTO;
import com.sodeca.gestionimmo.entity.Licence;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.LicenceMapper;
import com.sodeca.gestionimmo.repository.LicenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LicenceServiceImpl implements LicenceService {

    private final LicenceRepository licenceRepository;
    private final LicenceMapper licenceMapper;

    @Override
    public LicenceDTO createLicence(LicenceDTO licenceDTO) {
        Licence licence = licenceMapper.toEntity(licenceDTO);
        Licence savedLicence = licenceRepository.save(licence);
        return licenceMapper.toDTO(savedLicence);
    }

    @Override
    public LicenceDTO updateLicence(Long id, LicenceDTO licenceDTO) {
        Licence licence = licenceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("La Licence est introuvable"));
        licence.setNom(licenceDTO.getNom());
        licence.setFournisseur(licenceDTO.getFournisseur());
        licence.setDateExpiration(licenceDTO.getDateExpiration());
        licence.setQuantite(licenceDTO.getQuantite());
        licence.setCout(licenceDTO.getCout());
        licence.setDetails(licenceDTO.getDetails());
        Licence updatedLicence = licenceRepository.save(licence);
        return licenceMapper.toDTO(updatedLicence);
    }

    @Override
    public LicenceDTO getLicenceById(Long id) {
        Licence licence = licenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Licence not found"));
        return licenceMapper.toDTO(licence);
    }

    @Override
    public List<LicenceDTO> getAllLicences() {
        return licenceRepository.findAll().stream()
                .map(licenceMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteLicence(Long id) {
        if (!licenceRepository.existsById(id)) {
            throw new BusinessException("Licence not found");
        }
        licenceRepository.deleteById(id);
    }

    @Override
    public List<LicenceDTO> getLicencesByNom(String nom) {
        return licenceRepository.findByNomContainingIgnoreCase(nom).stream()
                .map(licenceMapper::toDTO)
                .toList();
    }

    @Override
    public List<LicenceDTO> getLicencesByFournisseur(String fournisseur) {
        return licenceRepository.findByFournisseurContainingIgnoreCase(fournisseur).stream()
                .map(licenceMapper::toDTO)
                .toList();
    }

    @Override
    public List<LicenceDTO> getExpiredLicences() {
        return licenceRepository.findByDateExpirationBefore(LocalDate.now()).stream()
                .map(licenceMapper::toDTO)
                .toList();

    }
}