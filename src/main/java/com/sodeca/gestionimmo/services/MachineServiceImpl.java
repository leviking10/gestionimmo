package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.dto.MachineDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Machine;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;
    private final ImmobilisationMapper mapper;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationServiceImpl immobilisationService;

    public MachineServiceImpl(MachineRepository machineRepository, ImmobilisationMapper mapper, CategorieRepository categorieRepository, ImmobilisationServiceImpl immobilisationService) {
        this.machineRepository = machineRepository;
        this.mapper = mapper;
        this.categorieRepository = categorieRepository;
        this.immobilisationService = immobilisationService;
    }

    @Override
    public List<MachineDTO> getAllMachines() {
        return machineRepository.findAll()
                .stream()
                .map(mapper::toMachineDTO)
                .toList();
    }

    @Override
    public Optional<MachineDTO> getMachineById(Long id) {
        return machineRepository.findById(id)
                .map(mapper::toMachineDTO);
    }

    @Override
    public MachineDTO createMachine(MachineDTO dto) {
        Machine machine = mapper.toMachine(dto);

        // Récupérer la catégorie associée par sa désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        machine.setCategorie(categorie);

        // Générer et assigner le QR Code
        immobilisationService.generateAndAssignQRCode(machine, dto);

        Machine saved = machineRepository.save(machine);
        return mapper.toMachineDTO(saved);
    }

    @Override
    public MachineDTO updateMachine(Long id, MachineDTO dto) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine non trouvée avec l'ID : " + id));

        // Mise à jour des champs spécifiques
        machine.setTypeMachine(dto.getTypeMachine());
        machine.setPuissance(dto.getPuissance());
        machine.setFabricant(dto.getFabricant());
        machine.setNumeroSerie(dto.getNumeroSerie());
        machine.setDesignation(dto.getDesignation());
        machine.setDateAcquisition(dto.getDateAcquisition());
        machine.setValeurAcquisition(dto.getValeurAcquisition());
        machine.setLocalisation(dto.getLocalisation());

        // Récupérer la catégorie associée par sa désignation
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        machine.setCategorie(categorie);

        Machine updated = machineRepository.save(machine);
        return mapper.toMachineDTO(updated);
    }

    @Override
    public void deleteMachine(Long id) {
        if (!machineRepository.existsById(id)) {
            throw new RuntimeException("Machine non trouvée avec l'ID : " + id);
        }
        machineRepository.deleteById(id);
    }

    @Override
    public List<MachineDTO> createMachines(List<ImmobilisationDTO> dtos) {
        List<MachineDTO> machineDTOS = dtos.stream()
                .filter(dto -> dto instanceof MachineDTO)
                .map(dto -> (MachineDTO) dto)
                .toList();

        List<Machine> machines = machineDTOS.stream().map(dto -> {
            Machine machine = mapper.toMachine(dto);

            // Récupérer la catégorie associée par sa désignation
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec la désignation : " + dto.getCategorieDesignation()));

            // Vérifier si la catégorie est active
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }

            machine.setCategorie(categorie);

            // Générer et assigner le QR Code
            immobilisationService.generateAndAssignQRCode(machine, dto);

            return machine;
        }).toList();

        List<Machine> savedMachines = machineRepository.saveAll(machines);

        return savedMachines.stream()
                .map(mapper::toMachineDTO)
                .toList();
    }
}
