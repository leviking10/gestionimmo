package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.TelephoneDTO;
import com.sodeca.gestionimmo.entity.Telephone;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.TelephoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TelephoneServiceImpl implements TelephoneService {

    private final TelephoneRepository telephoneRepository;
    private final ImmobilisationMapper mapper;

    public TelephoneServiceImpl(TelephoneRepository telephoneRepository, ImmobilisationMapper mapper) {
        this.telephoneRepository = telephoneRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TelephoneDTO> getAllTelephones() {
        return telephoneRepository.findAll()
                .stream()
                .map(mapper::toTelephoneDTO)
                .toList();
    }

    @Override
    public Optional<TelephoneDTO> getTelephoneById(Long id) {
        return telephoneRepository.findById(id)
                .map(mapper::toTelephoneDTO);
    }
    @Override
    public TelephoneDTO updateTelephone(Long id, TelephoneDTO dto) {
        // Récupérer le téléphone existant
        Telephone existingTelephone = telephoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Téléphone non trouvé avec l'ID : " + id));

        // Mettre à jour les champs spécifiques
        existingTelephone.setMarque(dto.getMarque());
        existingTelephone.setModele(dto.getModele());
        existingTelephone.setImei(dto.getImei());
        existingTelephone.setNumeroSerie(dto.getNumeroSerie());
        existingTelephone.setEtatImmo(dto.getEtatImmobilisation());
        existingTelephone.setStatut(dto.getAffectation());
        // Sauvegarder et retourner le DTO mis à jour
        Telephone updatedTelephone = telephoneRepository.save(existingTelephone);
        return mapper.toTelephoneDTO(updatedTelephone);
    }
}
