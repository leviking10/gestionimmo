package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.CessionDTO;
import com.sodeca.gestionimmo.entity.Cession;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutCession;
import com.sodeca.gestionimmo.repository.CessionRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CessionServiceImpl implements CessionService {

    private final CessionRepository cessionRepository;
    private final ImmobilisationRepository immobilisationRepository;

    public CessionServiceImpl(CessionRepository cessionRepository, ImmobilisationRepository immobilisationRepository) {
        this.cessionRepository = cessionRepository;
        this.immobilisationRepository = immobilisationRepository;
    }

    @Override
    public CessionDTO createCession(CessionDTO cessionDTO) {
        Immobilisation immobilisation = immobilisationRepository.findById(cessionDTO.getImmobilisationId())
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        if (cessionDTO.getStatutCession() == StatutCession.VENDU && cessionDTO.getValeurCession() == null) {
            throw new RuntimeException("La valeur de cession est obligatoire pour une immobilisation vendue.");
        }

        immobilisation.setStatutCession(cessionDTO.getStatutCession());
        immobilisation.setDateCession(cessionDTO.getDateCession());
        immobilisation.setValeurCession(cessionDTO.getValeurCession());
        immobilisationRepository.save(immobilisation);

        Cession cession = new Cession();
        cession.setImmobilisation(immobilisation);
        cession.setStatutCession(cessionDTO.getStatutCession());
        cession.setDateCession(cessionDTO.getDateCession());
        cession.setValeurCession(cessionDTO.getValeurCession());

        Cession savedCession = cessionRepository.save(cession);

        return mapToDTO(savedCession);
    }

    @Override
    public CessionDTO updateCession(Long cessionId, CessionDTO cessionDTO) {
        Cession cession = cessionRepository.findById(cessionId)
                .orElseThrow(() -> new RuntimeException("Cession introuvable"));

        if (cessionDTO.getStatutCession() == StatutCession.VENDU && cessionDTO.getValeurCession() == null) {
            throw new RuntimeException("La valeur de cession est obligatoire pour une immobilisation vendue.");
        }

        cession.setStatutCession(cessionDTO.getStatutCession());
        cession.setDateCession(cessionDTO.getDateCession());
        cession.setValeurCession(cessionDTO.getValeurCession());

        Immobilisation immobilisation = cession.getImmobilisation();
        immobilisation.setStatutCession(cessionDTO.getStatutCession());
        immobilisation.setDateCession(cessionDTO.getDateCession());
        immobilisation.setValeurCession(cessionDTO.getValeurCession());
        immobilisationRepository.save(immobilisation);

        Cession updatedCession = cessionRepository.save(cession);

        return mapToDTO(updatedCession);
    }

    @Override
    public void deleteCession(Long cessionId) {
        Cession cession = cessionRepository.findById(cessionId)
                .orElseThrow(() -> new RuntimeException("Cession introuvable"));

        Immobilisation immobilisation = cession.getImmobilisation();
        immobilisation.setStatutCession(StatutCession.DISPONIBLE);
        immobilisation.setDateCession(null);
        immobilisation.setValeurCession(null);
        immobilisationRepository.save(immobilisation);

        cessionRepository.deleteById(cessionId);
    }

    @Override
    public List<CessionDTO> getCessionsByImmobilisation(Long immobilisationId) {
        return cessionRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    @Override
    public List<CessionDTO> getAllCessions() {
        return cessionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }



    @Override
    public List<CessionDTO> getCessionsByStatut(StatutCession statut) {
        return cessionRepository.findByStatutCession(statut)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    @Override
    public CessionDTO getCessionById(Long cessionId) {
        return mapToDTO(getCession(cessionId));
    }

    @Override
    public Long getCountByStatut(StatutCession statut) {
        return cessionRepository.countByStatutCession(statut);
    }

    @Override
    public List<CessionDTO> getCessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return cessionRepository.findByDateCessionBetween(startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    @Override
    public void annulerCession(Long immobilisationId) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        if (immobilisation.getStatutCession() == StatutCession.DISPONIBLE) {
            throw new RuntimeException("Cette immobilisation n'a pas de cession enregistrée.");
        }

        immobilisation.setStatutCession(StatutCession.DISPONIBLE);
        immobilisation.setDateCession(null);
        immobilisation.setValeurCession(null);
        immobilisationRepository.save(immobilisation);
    }


    private Cession getCession(Long id) {
        return cessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cession introuvable avec ID : " + id));
    }



    private CessionDTO mapToDTO(Cession cession) {
        return new CessionDTO(
                cession.getId(),
                cession.getImmobilisation().getId(),
                cession.getStatutCession(),
                cession.getDateCession(),
                cession.getValeurCession()
        );
    }
}