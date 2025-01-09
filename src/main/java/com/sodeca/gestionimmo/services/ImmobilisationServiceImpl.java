package com.sodeca.gestionimmo.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.*;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
public class ImmobilisationServiceImpl implements ImmobilisationService {

    private final ImmobilisationRepository immobilisationRepository;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationMapper mapper;
    private final QRCodeService qrCodeService;
    private static final Logger logger = LoggerFactory.getLogger(ImmobilisationServiceImpl.class);
    public ImmobilisationServiceImpl(ImmobilisationMapper mapper,
                                     ImmobilisationRepository immobilisationRepository,
                                     CategorieRepository categorieRepository, QRCodeService qrCodeService) {
        this.mapper = mapper;
        this.immobilisationRepository = immobilisationRepository;
        this.categorieRepository = categorieRepository;
        this.qrCodeService = qrCodeService;
    }

    @Override
    public List<ImmobilisationDTO> getAllImmobilisations() {
        List<Immobilisation> immobilisations = immobilisationRepository.findAll();

        immobilisations.forEach(immo -> logger.info("Immobilisation récupérée : {}", immo));

        return immobilisations.stream()
                .map(mapper::toDTO)
                .toList();
    }


    @Override
    public Optional<ImmobilisationDTO> getImmobilisationById(Long id) {
        return immobilisationRepository.findById(id)
                .map(mapper::toPolymorphicDTO);
    }

    public List<ImmobilisationDTO> createImmobilisations(List<ImmobilisationDTO> dtos) {
        List<Immobilisation> immobilisations = dtos.stream()
                .map(this::convertToImmobilisation)
                .toList();

        List<Immobilisation> saved = immobilisationRepository.saveAll(immobilisations);
        return saved.stream().map(mapper::toPolymorphicDTO).toList();
    }

    private Immobilisation convertToImmobilisation(ImmobilisationDTO dto) {
        Categorie categorie = getValidatedCategorie(dto.getCategorieDesignation());
        Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);
        immobilisation.setCategorie(categorie);
        generateAndAssignQRCode(immobilisation);
        return immobilisation;
    }

    private Categorie getValidatedCategorie(String designation) {
        Categorie categorie = categorieRepository.findByDesignation(designation)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + designation));
        if (!categorie.isActif()) {
            throw new BusinessException("La catégorie sélectionnée est désactivée.");
        }
        return categorie;
    }



    private void generateAndAssignQRCode(Immobilisation immobilisation) {
        String qrCodeData = "https://gestionimmo.sodeca.com/immobilisation/" + immobilisation.getCodeImmo();
        immobilisation.setQrCode(qrCodeService.generateQRCode(qrCodeData));
    }


    @Override
    public ImmobilisationDTO createImmobilisation(ImmobilisationDTO dto) {
        // Vérification et mapping
        Categorie categorie = getValidatedCategorie(dto.getCategorieDesignation());
        Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);

        // Paramètres par défaut
        setDefaultValues(dto, immobilisation);

        immobilisation.setCategorie(categorie);
        immobilisation.setCodeImmo(generateUniqueCodeImmo());
        generateAndAssignQRCode(immobilisation);

        Immobilisation saved = immobilisationRepository.save(immobilisation);
        return mapper.toPolymorphicDTO(saved);
    }
    private void setDefaultValues(ImmobilisationDTO dto, Immobilisation immobilisation) {
        immobilisation.setAffectation(dto.getAffectation() != null ? dto.getAffectation() : StatutAffectation.DISPONIBLE);
        immobilisation.setEtatImmo(dto.getEtatImmo() != null ? dto.getEtatImmo() : EtatImmobilisation.EN_SERVICE);
    }
    @Override
    @Transactional
    public ImmobilisationDTO updateImmobilisation(Long id, ImmobilisationDTO dto) {
        Immobilisation immobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable  : "));

        // Mise à jour des champs
        updateFields(immobilisation, dto);
        Immobilisation updated = immobilisationRepository.save(immobilisation);

        return mapper.toPolymorphicDTO(updated);
    }
    private void updateFields(Immobilisation immobilisation, ImmobilisationDTO dto) {
        if (dto.getDesignation() != null) immobilisation.setDesignation(dto.getDesignation());
        if (dto.getDateAcquisition() != null) immobilisation.setDateAcquisition(dto.getDateAcquisition());
        if (dto.getValeurAcquisition() != null) immobilisation.setValeurAcquisition(dto.getValeurAcquisition());
        if (dto.getLocalisation() != null) immobilisation.setLocalisation(dto.getLocalisation());
        if (dto.getEtatImmo() != null) immobilisation.setEtatImmo(dto.getEtatImmo());
        if (dto.getAffectation() != null) immobilisation.setAffectation(dto.getAffectation());
    }


    @Override
    public void deleteImmobilisation(Long id) {
        immobilisationRepository.deleteById(id);
    }

    @Override
    public byte[] getQRCodeAsImage(Long id) {
        Immobilisation immobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cette immobilisation n'existe pas"));

        if (immobilisation.getQrCode() == null || immobilisation.getQrCode().isEmpty()) {
            throw new BusinessException("QR Code non disponible pour cette immobilisation");
        }

        return Base64.getDecoder().decode(immobilisation.getQrCode());
    }

    @Override
    public byte[] downloadQRCode(Long id) {
        Immobilisation immobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable "));

        if (immobilisation.getQrCode() == null || immobilisation.getQrCode().isEmpty()) {
            throw new BusinessException("Le QR Code pour cette immobilisation n'existe pas.");
        }

        return Base64.getDecoder().decode(immobilisation.getQrCode());
    }

    @Override
    public ImmobilisationDTO getImmobilisationByCode(String codeImmo) {
        Immobilisation immobilisation = immobilisationRepository.findByCodeImmo(codeImmo)
                .orElseThrow(() -> new BusinessException("Aucune immobilisation trouvée avec le code : " + codeImmo));
        return mapper.toPolymorphicDTO(immobilisation);
    }

    @Override
    public List<ImmobilisationDTO> getImmobilisationsCedees() {
        return immobilisationRepository.findAll().stream()
                .filter(immobilisation -> immobilisation.getStatutCession() == StatutCession.VENDU ||
                        immobilisation.getStatutCession() == StatutCession.MISE_EN_REBUT||immobilisation.getStatutCession()==StatutCession.SORTIE)
                .map(mapper::toPolymorphicDTO)
                .toList();
    }

    @Override
    public ImmobilisationDTO getImmobilisationByQRCode(String qrCodeData) {
        String immobilisationId = extractIdFromQRCode(qrCodeData);

        Immobilisation immobilisation = immobilisationRepository.findById(Long.parseLong(immobilisationId))
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + immobilisationId));

        return mapper.toPolymorphicDTO(immobilisation);
    }

    private String extractIdFromQRCode(String qrCodeData) {
        String[] parts = qrCodeData.split("/");
        return parts[parts.length - 1];
    }

    private String generateUniqueCodeImmo() {
        String code;
        do {
            code = "IMMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (immobilisationRepository.findByCodeImmo(code).isPresent());
        return code;
    }

    @Override
    public void updateEtat(Long immobilisationId, EtatImmobilisation nouvelEtat) {
        Immobilisation immobilisation = immobilisationRepository.findById(immobilisationId)
                .orElseThrow(() -> new BusinessException("Immobilisation introuvable avec l'ID : " + immobilisationId));

        immobilisation.setEtatImmo(nouvelEtat);
        immobilisationRepository.save(immobilisation);
    }
    @Override
    public List<ImmobilisationDTO> importImmobilisationsFromFile(MultipartFile file) throws IOException {
        validateFile(file);

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BusinessException("Le fichier doit avoir un nom valide.",
                    "INVALID_FILE_NAME", HttpStatus.BAD_REQUEST);
        }

        fileName = fileName.toLowerCase();

        if (isExcelFile(fileName)) {
            return processExcelFile(file);
        } else if (isCsvFile(fileName)) {
            return processCsvFile(file);
        } else {
            throw new BusinessException("Format de fichier non supporté. Veuillez uploader un fichier Excel ou CSV.",
                    "UNSUPPORTED_FILE_FORMAT", HttpStatus.BAD_REQUEST);
        }
    }


    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Le fichier est vide ou non fourni.",
                    "EMPTY_FILE", HttpStatus.BAD_REQUEST);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BusinessException("Le fichier doit avoir un nom valide.",
                    "INVALID_FILE_NAME", HttpStatus.BAD_REQUEST);
        }

        // Validation supplémentaire : vérifier les extensions autorisées
        if (!isValidExtension(fileName)) {
            throw new BusinessException("Extension de fichier non supportée. Veuillez uploader un fichier Excel ou CSV.",
                    "INVALID_FILE_EXTENSION", HttpStatus.BAD_REQUEST);
        }
    }
    private boolean isValidExtension(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".xls") || lowerCaseFileName.endsWith(".xlsx") || lowerCaseFileName.endsWith(".csv");
    }
    private boolean isExcelFile(String fileName) {
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }

    private boolean isCsvFile(String fileName) {
        return fileName.endsWith(".csv");
    }
    private List<ImmobilisationDTO> processExcelFile(MultipartFile file) throws IOException {
        List<ImmobilisationDTO> immobilisations = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorer la ligne d'en-tête

                try {
                    ImmobilisationDTO dto = mapExcelRowToImmobilisationDTO(row);
                    if (isValidImmobilisation(dto)) {
                        immobilisations.add(dto);
                    } else {
                        logger.warn("DTO invalide à la ligne {} : {}", row.getRowNum() + 1, dto);
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de la ligne {} : {}", row.getRowNum() + 1, e.getMessage());
                }
            }
        }
        return saveImportedImmobilisations(immobilisations);
    }
    private List<ImmobilisationDTO> processCsvFile(MultipartFile file) throws IOException {
        List<ImmobilisationDTO> immobilisations = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            boolean isHeader = true;

            while ((values = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Ignorer la ligne d'en-tête
                    continue;
                }

                processCsvRow(values, immobilisations);
            }
        } catch (CsvValidationException e) {
            throw new BusinessException("Erreur de validation dans le fichier CSV.", "CSV_VALIDATION_ERROR", HttpStatus.BAD_REQUEST, e);
        } catch (IOException e) {
            throw new BusinessException("Erreur de lecture du fichier CSV.", "CSV_IO_ERROR", HttpStatus.BAD_REQUEST, e);
        }

        return saveImportedImmobilisations(immobilisations);
    }

    private void processCsvRow(String[] values, List<ImmobilisationDTO> immobilisations) {
        try {
            ImmobilisationDTO dto = mapCsvRowToImmobilisationDTO(values);
            if (isValidImmobilisation(dto)) {
                immobilisations.add(dto);
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("DTO invalide pour la ligne CSV : {}", Arrays.toString(values));
                }
            }
        } catch (BusinessException e) {
            logger.error("Erreur métier lors du traitement de la ligne CSV : {} - {}", Arrays.toString(values), e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du traitement de la ligne CSV : {} - {}", Arrays.toString(values), e.getMessage());
        }
    }


    private ImmobilisationDTO mapExcelRowToImmobilisationDTO(Row row) {
        try {
            validateExcelRow(row);

            ImmobilisationDTO dto = new ImmobilisationDTO();
            dto.setDesignation(getCellValue(row.getCell(0)).trim());
            dto.setCategorieDesignation(getCellValue(row.getCell(1)).trim());
            dto.setDateAcquisition(parseCellDate(row.getCell(2)).toLocalDate());
            dto.setValeurAcquisition(parseDouble(getCellValue(row.getCell(3))));
            dto.setLocalisation(getCellValue(row.getCell(4)).trim());
            // Gestion sécurisée de la date de mise en service
            LocalDateTime optionalDate = parseOptionalCellDate(row.getCell(5));
            dto.setDateMiseEnService(optionalDate != null ? optionalDate.toLocalDate() : null);

            dto.setType(parseEnum(getCellValue(row.getCell(6)), TypeImmobilisation.class));
            dto.setTypeAmortissement(parseEnum(getCellValue(row.getCell(7)), TypeAmortissement.class));

            // Champs par défaut
            dto.setAffectation(StatutAffectation.DISPONIBLE);
            dto.setEtatImmo(EtatImmobilisation.EN_SERVICE);

            return dto;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors du mappage de la ligne Excel.", "EXCEL_ROW_MAPPING_ERROR", HttpStatus.BAD_REQUEST, e);
        }
    }


    private void validateExcelRow(Row row) {
        if (row == null) {
            throw new BusinessException("La ligne Excel est nulle.", "EXCEL_ROW_NULL", HttpStatus.BAD_REQUEST);
        }

        int expectedColumns = 8;
        if (row.getLastCellNum() < expectedColumns) {
            throw new BusinessException("La ligne Excel contient moins de colonnes que prévu.", "EXCEL_ROW_INCOMPLETE", HttpStatus.BAD_REQUEST);
        }
    }
    private LocalDateTime parseCellDate(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            throw new BusinessException("La cellule ne contient pas une date valide.", "EXCEL_DATE_FORMAT_ERROR", HttpStatus.BAD_REQUEST);
        }
        return cell.getLocalDateTimeCellValue();
    }

    private LocalDateTime parseOptionalCellDate(Cell cell) {
        return (cell == null || cell.getCellType() == CellType.BLANK) ? null : parseCellDate(cell);
    }

    // Méthode pour convertir une valeur en double avec gestion des erreurs

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new BusinessException("Valeur numérique invalide: " + value, "NUMBER_FORMAT_ERROR", HttpStatus.BAD_REQUEST, e);
        }
    }
    private static final StatutAffectation DEFAULT_STATUT_AFFECTATION = StatutAffectation.DISPONIBLE;
    private static final EtatImmobilisation DEFAULT_ETAT_IMMO = EtatImmobilisation.EN_SERVICE;

    private ImmobilisationDTO mapCsvRowToImmobilisationDTO(String[] values) {
        try {
            validateCsvValues(values);

            ImmobilisationDTO dto = new ImmobilisationDTO();
            dto.setDesignation(values[0].trim());
            dto.setCategorieDesignation(values[1].trim());
            dto.setDateAcquisition(parseDate(values[2].trim()));
            dto.setValeurAcquisition(parseDouble(values[3].trim()));
            dto.setLocalisation(values[4].trim());
            dto.setDateMiseEnService(parseOptionalDate(values[5]));
            dto.setType(parseEnum(values[6], TypeImmobilisation.class));
            dto.setTypeAmortissement(parseEnum(values[7], TypeAmortissement.class));

            dto.setAffectation(DEFAULT_STATUT_AFFECTATION);
            dto.setEtatImmo(DEFAULT_ETAT_IMMO);

            return dto;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors du mappage des données CSV: " + Arrays.toString(values), "CSV_MAPPING_ERROR", HttpStatus.BAD_REQUEST, e);
        }
    }
    private void validateCsvValues(String[] values) {
        if (values == null || values.length < 8) {
            throw new BusinessException("Les données CSV sont incomplètes ou nulles.");
        }
    }
    private LocalDate parseOptionalDate(String dateStr) {
        return (dateStr == null || dateStr.trim().isEmpty()) ? null : parseDate(dateStr.trim());
    }
    private <T extends Enum<T>> T parseEnum(String value, Class<T> enumType) {
        try {
            return Enum.valueOf(enumType, normalizeString(value));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Valeur d'enum invalide: " + value, "ENUM_CONVERSION_ERROR", HttpStatus.BAD_REQUEST, e);
        }
    }
    private String normalizeString(String value) {
        return value.trim().toUpperCase()
                .replace("É", "E")
                .replace("È", "E")
                .replace("À", "A")
                .replace("Ù", "U");
    }
    private List<ImmobilisationDTO> saveImportedImmobilisations(List<ImmobilisationDTO> dtos) {
        List<Immobilisation> immobilisations = dtos.stream().map(dto -> {
            Categorie categorie = categorieRepository.findByDesignation(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + dto.getCategorieDesignation()));
            Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);
            immobilisation.setCategorie(categorie);
            immobilisation.setCodeImmo(generateUniqueCodeImmo());
            generateAndAssignQRCode(immobilisation);
            return immobilisation;
        }).toList();

        List<Immobilisation> savedImmobilisations = immobilisationRepository.saveAll(immobilisations);

        return savedImmobilisations.stream()
                .map(mapper::toPolymorphicDTO)
                .toList();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            logger.info("Date analysée avec succès : {}", parsedDate);
            return parsedDate;
        } catch (Exception e) {
            logger.error("Erreur lors de l'analyse de la date : {}. Attendu au format jj/MM/yyyy.", date);
            throw new BusinessException("Format de date invalide : " + date + ". Attendu : jj/MM/yyyy");
        }
    }
    private boolean isValidImmobilisation(ImmobilisationDTO dto) {
        return dto.getDesignation() != null && !dto.getDesignation().isEmpty()
                && dto.getCategorieDesignation() != null && !dto.getCategorieDesignation().isEmpty()
                && dto.getDateAcquisition() != null
                && dto.getValeurAcquisition() > 0
                && dto.getType() != null
                && dto.getTypeAmortissement() != null;
    }
}

