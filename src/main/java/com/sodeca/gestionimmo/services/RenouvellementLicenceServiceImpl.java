package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.RenouvellementLicenceDTO;
import com.sodeca.gestionimmo.entity.Licence;
import com.sodeca.gestionimmo.entity.RenouvellementLicence;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.RenouvellementLicenceMapper;
import com.sodeca.gestionimmo.repository.LicenceRepository;
import com.sodeca.gestionimmo.repository.RenouvellementLicenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RenouvellementLicenceServiceImpl implements RenouvellementLicenceService {

    private final RenouvellementLicenceRepository renouvellementRepository;
    private final LicenceRepository licenceRepository;
    private final RenouvellementLicenceMapper renouvellementMapper;

    @Override
    public RenouvellementLicenceDTO createRenouvellement(RenouvellementLicenceDTO renouvellementDTO) {
        Licence licence = licenceRepository.findById(renouvellementDTO.getLicenceId())
                .orElseThrow(() -> new RuntimeException("Licence not found"));
        RenouvellementLicence renouvellement = renouvellementMapper.toEntity(renouvellementDTO);
        renouvellement.setLicence(licence);

        licence.setQuantite(licence.getQuantite() + renouvellementDTO.getQuantiteAjoutee());
        licenceRepository.save(licence);

        RenouvellementLicence savedRenouvellement = renouvellementRepository.save(renouvellement);
        return renouvellementMapper.toDTO(savedRenouvellement);
    }

    @Override
    public List<RenouvellementLicenceDTO> getRenouvellementsByLicenceId(Long licenceId) {
        return renouvellementRepository.findByLicenceId(licenceId).stream()
                .map(renouvellementMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteRenouvellement(Long id) {
        if (!renouvellementRepository.existsById(id)) {
            throw new BusinessException("Renouvellement not found");
        }
        renouvellementRepository.deleteById(id);
    }
}
