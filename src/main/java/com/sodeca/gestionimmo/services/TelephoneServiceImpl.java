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
        Telephone telephone = telephoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Téléphone non trouvé avec l'ID : " + id));

        // Mise à jour des champs techniques spécifiques au téléphone
        telephone.setMarque(dto.getMarque());
        telephone.setModele(dto.getModele());
        telephone.setImei(dto.getImei());
        telephone.setNumeroSerie(dto.getNumeroSerie());
        telephone.setEtatImmo(dto.getEtatImmobilisation());
        Telephone updated = telephoneRepository.save(telephone);
        return mapper.toTelephoneDTO(updated);
    }
}
