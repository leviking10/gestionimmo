package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.CessionDTO;
import com.sodeca.gestionimmo.entity.Cession;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.StatutCession;
import com.sodeca.gestionimmo.repository.CessionRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import org.springframework.stereotype.Service;

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

        Cession cession = new Cession();
        cession.setImmobilisation(immobilisation);
        cession.setStatutCession(cessionDTO.getStatutCession());
        cession.setDateCession(cessionDTO.getDateCession());
        cession.setValeurCession(cessionDTO.getValeurCession());

        Cession savedCession = cessionRepository.save(cession);

        return new CessionDTO(
                savedCession.getId(),
                savedCession.getImmobilisation().getId(),
                savedCession.getStatutCession(),
                savedCession.getDateCession(),
                savedCession.getValeurCession()
        );
    }

    @Override
    public void annulerCession(Long immobilisationId) {
        // Récupérer l'immobilisation
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable"));

        // Vérifier si l'immobilisation a une cession enregistrée
        if (immobilisation.getStatutCession() == StatutCession.DISPONIBLE) {
            throw new RuntimeException("Cette immobilisation n'a pas de cession enregistrée.");
        }

        // Réinitialiser les champs liés à la cession
        immobilisation.setStatutCession(StatutCession.DISPONIBLE); // La rendre à nouveau disponible
        immobilisation.setDateCession(null); // Effacer la date de cession
        immobilisation.setValeurCession(null); // Effacer la valeur de cession
        immobilisationRepository.save(immobilisation); // Sauvegarder les modifications
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

        Cession updatedCession = cessionRepository.save(cession);

        return new CessionDTO(
                updatedCession.getId(),
                updatedCession.getImmobilisation().getId(),
                updatedCession.getStatutCession(),
                updatedCession.getDateCession(),
                updatedCession.getValeurCession()
        );
    }

    @Override
    public void deleteCession(Long cessionId) {
        cessionRepository.deleteById(cessionId);
    }

    @Override
    public List<CessionDTO> getCessionsByImmobilisation(Long immobilisationId) {
        return cessionRepository.findByImmobilisationId(immobilisationId)
                .stream()
                .map(cession -> new CessionDTO(
                        cession.getId(),
                        cession.getImmobilisation().getId(),
                        cession.getStatutCession(),
                        cession.getDateCession(),
                        cession.getValeurCession()
                ))
                .toList();
    }
}
