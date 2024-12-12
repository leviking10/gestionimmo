package com.sodeca.gestionimmo.services;
import com.opencsv.exceptions.CsvValidationException;
import com.sodeca.gestionimmo.dto.PersonnelDTO;
import com.sodeca.gestionimmo.entity.Personnel;
import com.sodeca.gestionimmo.mapper.PersonnelMapper;
import com.sodeca.gestionimmo.repository.PersonnelRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;


@Service
public class PersonnelServiceImpl implements PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;

    public PersonnelServiceImpl(PersonnelRepository personnelRepository, PersonnelMapper personnelMapper) {
        this.personnelRepository = personnelRepository;
        this.personnelMapper = personnelMapper;
    }

    @Override
    public PersonnelDTO createPersonnel(PersonnelDTO personnelDTO) {
        if (personnelRepository.existsByMatricule(personnelDTO.getMatricule())) {
            throw new RuntimeException("Un personnel avec ce matricule existe déjà.");
        }
        Personnel personnel = personnelMapper.toEntity(personnelDTO);
        Personnel savedPersonnel = personnelRepository.save(personnel);
        return personnelMapper.toDTO(savedPersonnel);
    }
    @Override
    public void activationForMultiple(List<Long> ids) {
        // Récupérer tous les personnels concernés
        List<Personnel> personnels = personnelRepository.findAllById(ids);

        if (personnels.isEmpty()) {
            throw new RuntimeException("Aucun personnel trouvé pour les IDs fournis.");
        }

        // Bascule du statut actif/inactif pour chaque personnel
        personnels.forEach(personnel -> personnel.setActif(!personnel.isActif()));

        // Sauvegarder les modifications
        personnelRepository.saveAll(personnels);
    }

    @Override
    public PersonnelDTO updatePersonnel(Long personnelId, PersonnelDTO personnelDTO) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel introuvable avec l'ID : " + personnelId));

        personnel.setNom(personnelDTO.getNom());
        personnel.setPrenom(personnelDTO.getPrenom());
        personnel.setTelephone(personnelDTO.getTelephone());
        personnel.setPoste(personnelDTO.getPoste());
        personnel.setDepartement(personnelDTO.getDepartement());
        personnel.setAdresse(personnelDTO.getAdresse());
        personnel.setLocalite(personnelDTO.getLocalite());
        personnel.setService(personnelDTO.getService());
        personnel.setActif(personnelDTO.isActif());

        Personnel updatedPersonnel = personnelRepository.save(personnel);
        return personnelMapper.toDTO(updatedPersonnel);
    }

    @Override
    public void deletePersonnel(Long personnelId) {
        if (!personnelRepository.existsById(personnelId)) {
            throw new RuntimeException("Cette personne introuvable avec l'ID : " + personnelId);
        }
        personnelRepository.deleteById(personnelId);
    }

    @Override
    public List<PersonnelDTO> getAllPersonnel() {
        return personnelRepository.findAll()
                .stream()
                .map(personnelMapper::toDTO)
                .toList();
    }

    @Override
    public PersonnelDTO getPersonnelById(Long personnelId) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel introuvable avec l'ID : " + personnelId));
        return personnelMapper.toDTO(personnel);
    }

    @Override
    public PersonnelDTO getPersonnelByMatricule(String matricule) {
        Personnel personnel = personnelRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Personnel introuvable avec le matricule : " + matricule));
        return personnelMapper.toDTO(personnel);
    }

    @Override
    public PersonnelDTO getPersonnelByTelephone(String telephone) {
        Personnel personnel = personnelRepository.findByTelephone(telephone)
                .orElseThrow(() -> new RuntimeException("Personnel introuvable avec le téléphone : " + telephone));
        return personnelMapper.toDTO(personnel);
    }
    @Override
    public List<PersonnelDTO> createPersonnels(List<PersonnelDTO> personnelDTOS) {
        // Convertir chaque DTO en entité
        List<Personnel> personnels = personnelDTOS.stream()
                .map(personnelMapper::toEntity)
                .toList();

        // Sauvegarder toutes les entités en une seule transaction
        List<Personnel> savedPersonnels = personnelRepository.saveAll(personnels);

        // Convertir les entités sauvegardées en DTO et les retourner
        return savedPersonnels.stream()
                .map(personnelMapper::toDTO)
                .toList();
    }
/**
     * Lire un fichier Excel et retourner une liste de personnels.
     *
     * @param file Fichier Excel ou csv à lire.
     * @return Liste de personnels.
 */
    @Override
    public List<PersonnelDTO> readFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().toLowerCase();

        if (isExcelFile(fileName)) {
            return processExcelFile(file);
        } else if (isCsvFile(fileName)) {
            return processCsvFile(file);
        } else {
            throw new RuntimeException("Format de fichier non supporté. Veuillez uploader un fichier Excel ou CSV.");
        }
    }

    private boolean isExcelFile(String fileName) {
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }

    private boolean isCsvFile(String fileName) {
        return fileName.endsWith(".csv");
    }

    // Traitement des fichiers Excel
    private List<PersonnelDTO> processExcelFile(MultipartFile file) throws IOException {
        List<PersonnelDTO> personnels = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Ignorer la ligne d'en-tête

            PersonnelDTO personnelDTO = mapExcelRowToPersonnelDTO(row);
            if (isValidPersonnel(personnelDTO)) {
                personnels.add(personnelDTO);
            }
        }

        workbook.close();
        return personnels;
    }

    private PersonnelDTO mapExcelRowToPersonnelDTO(Row row) {
        PersonnelDTO personnelDTO = new PersonnelDTO();
        personnelDTO.setMatricule(getCellValue(row.getCell(0)));
        personnelDTO.setNom(getCellValue(row.getCell(1)));
        personnelDTO.setPrenom(getCellValue(row.getCell(2)));
        personnelDTO.setTelephone(getCellValue(row.getCell(3)));
        personnelDTO.setPoste(getCellValue(row.getCell(4)));
        personnelDTO.setDepartement(getCellValue(row.getCell(5)));
        personnelDTO.setAdresse(getCellValue(row.getCell(6)));
        personnelDTO.setLocalite(getCellValue(row.getCell(7)));
        personnelDTO.setService(getCellValue(row.getCell(8)));
        return personnelDTO;
    }

    // Traitement des fichiers CSV
    private List<PersonnelDTO> processCsvFile(MultipartFile file) throws IOException {
        List<PersonnelDTO> personnels = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            boolean isHeader = true;

            while ((values = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Ignorer la ligne d'en-tête
                    continue;
                }

                PersonnelDTO personnelDTO = mapCsvRowToPersonnelDTO(values);
                if (isValidPersonnel(personnelDTO)) {
                    personnels.add(personnelDTO);
                }
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException("Erreur de validation dans le fichier CSV.", e);
        }

        return personnels;
    }
    @Override
    public void desactiverOuActiverPersonnel(Long id) {
        // Récupérer le personnel à partir de l'ID
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec l'ID : " + id));

        // Bascule du statut actif/inactif
        personnel.setActif(!personnel.isActif());
        personnelRepository.save(personnel);
    }
    private PersonnelDTO mapCsvRowToPersonnelDTO(String[] values) {
        PersonnelDTO personnelDTO = new PersonnelDTO();
        personnelDTO.setMatricule(values[0]);
        personnelDTO.setNom(values[1]);
        personnelDTO.setPrenom(values[2]);
        personnelDTO.setTelephone(values[3]);
        personnelDTO.setPoste(values[4]);
        personnelDTO.setDepartement(values[5]);
        personnelDTO.setAdresse(values[6]);
        personnelDTO.setLocalite(values[7]);
        personnelDTO.setService(values[8]);
        return personnelDTO;
    }

    // Méthode pour extraire la valeur d'une cellule Excel
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    // Validation des champs obligatoires
    private boolean isValidPersonnel(PersonnelDTO personnelDTO) {
        return personnelDTO.getMatricule() != null && !personnelDTO.getMatricule().isEmpty()
                && personnelDTO.getNom() != null && !personnelDTO.getNom().isEmpty()
                && personnelDTO.getPrenom() != null && !personnelDTO.getPrenom().isEmpty()
                && personnelDTO.getTelephone() != null && !personnelDTO.getTelephone().isEmpty();
    }

}
