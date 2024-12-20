package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.ConsommationCarburantDTO;
import com.sodeca.gestionimmo.dto.ConsommationResumeDTO;
import com.sodeca.gestionimmo.entity.ConsommationCarburant;
import com.sodeca.gestionimmo.entity.Vehicule;
import com.sodeca.gestionimmo.mapper.ConsommationCarburantMapper;
import com.sodeca.gestionimmo.repository.ConsommationCarburantRepository;
import com.sodeca.gestionimmo.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsommationCarburantServiceImpl implements ConsommationCarburantService {

    private final ConsommationCarburantRepository repository;
    private final VehiculeRepository vehiculeRepository;
    private final ConsommationCarburantMapper mapper;

    public ConsommationCarburantServiceImpl(ConsommationCarburantRepository repository,
                                            VehiculeRepository vehiculeRepository,
                                            ConsommationCarburantMapper mapper) {
        this.repository = repository;
        this.vehiculeRepository = vehiculeRepository;
        this.mapper = mapper;
    }

    @Override
    public ConsommationCarburantDTO addConsommation(ConsommationCarburantDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        ConsommationCarburant consommation = mapper.toEntity(dto);
        consommation.setVehicule(vehicule);
        return mapper.toDTO(repository.save(consommation));
    }

    @Override
    public List<ConsommationCarburantDTO> getConsommationsByVehicule(Long vehiculeId) {
        return repository.findByVehiculeIdOrderByDateDesc(vehiculeId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ConsommationCarburantDTO> getHistoriqueConsommation(Long vehiculeId) {
        return getConsommationsByVehicule(vehiculeId);
    }

    @Override
    public Double getConsommationMoyenne(Long vehiculeId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<ConsommationCarburant> consommations = repository.findByVehiculeIdAndDateBetween(vehiculeId, start, end);
        double totalLitres = consommations.stream().mapToDouble(ConsommationCarburant::getQuantiteLitres).sum();
        double totalKilometrage = consommations.stream().mapToDouble(ConsommationCarburant::getKilométrage).sum();
        return totalKilometrage > 0 ? (totalLitres / totalKilometrage) * 100 : 0.0;
    }

    @Override
    public Double getCoutTotalConsommation(Long vehiculeId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        return repository.findByVehiculeIdAndDateBetween(vehiculeId, start, end).stream()
                .mapToDouble(ConsommationCarburant::getCoutTotal).sum();
    }

    @Override
    public List<ConsommationResumeDTO> getResumeConsommation(String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        return repository.findAllByDateBetween(start, end).stream()
                .collect(Collectors.groupingBy(
                        consommation -> consommation.getVehicule().getId(),
                        Collectors.summarizingDouble(ConsommationCarburant::getQuantiteLitres)))
                .entrySet().stream()
                .map(entry -> new ConsommationResumeDTO(entry.getKey(), entry.getValue().getSum(), entry.getValue().getAverage()))
                .toList();
    }

    @Override
    public List<ConsommationCarburantDTO> getAllConsommations() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }
}
