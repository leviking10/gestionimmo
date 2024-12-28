package com.sodeca.gestionimmo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.*;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
public class ImmobilisationServiceImpl implements ImmobilisationService {

    private final ImmobilisationRepository immobilisationRepository;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ImmobilisationServiceImpl.class);
    public ImmobilisationServiceImpl(ImmobilisationMapper mapper,
                                     ImmobilisationRepository immobilisationRepository,
                                     CategorieRepository categorieRepository) {
        this.mapper = mapper;
        this.immobilisationRepository = immobilisationRepository;
        this.categorieRepository = categorieRepository;
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

    @Override
    public List<ImmobilisationDTO> createImmobilisations(List<ImmobilisationDTO> dtos) {
        List<Immobilisation> immobilisations = dtos.stream().map(dto -> {
            // Récupérer la catégorie par désignation
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec la désignation : " + dto.getCategorieDesignation()));

            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }

            // Convertir le DTO en entité
            Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);
            immobilisation.setCategorie(categorie);

            if (dto.getAffectation() == null) {
                dto.setAffectation(StatutAffectation.DISPONIBLE);
            }
            if (dto.getEtatImmobilisation() == null) {
                dto.setEtatImmobilisation(EtatImmobilisation.EN_SERVICE);
            }
             categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec la désignation : " + dto.getCategorieDesignation()));

            // Vérifier si la catégorie est active
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }
            // Générer et assigner un QR Code
            generateAndAssignQRCode(immobilisation);

            return immobilisation;
        }).toList();

        // Sauvegarder toutes les immobilisations
        List<Immobilisation> savedImmobilisations = immobilisationRepository.saveAll(immobilisations);

        // Convertir en DTO et retourner
        return savedImmobilisations.stream()
                .map(mapper::toPolymorphicDTO)
                .toList();
    }


    private void generateAndAssignQRCode(Immobilisation immobilisation) {
        try {
            String qrCodeData = "https://gestionimmo.sodeca.com/immobilisation/" + immobilisation.getCodeImmo();
            String qrCodeBase64 = generateQRCode(qrCodeData);
            immobilisation.setQrCode(qrCodeBase64);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR Code", e);
        }
    }


    @Override
    public ImmobilisationDTO createImmobilisation(ImmobilisationDTO dto) {
        // Récupérer la catégorie par désignation
        // Ajout des valeurs par défaut pour les champs obligatoires
        if (dto.getAffectation() == null) {
            dto.setAffectation(StatutAffectation.DISPONIBLE);
        }
        if (dto.getEtatImmobilisation() == null) {
            dto.setEtatImmobilisation(EtatImmobilisation.EN_SERVICE);
        }

        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        // Convertir le DTO en entité
        Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);
        immobilisation.setCategorie(categorie);
        // Sauvegarder l'immobilisation dans la base de données
        immobilisation.setCodeImmo(generateUniqueCodeImmo());
        immobilisation.setType(TypeImmobilisation.valueOf(dto.getType().name()));
        immobilisation.setTypeAmortissement(TypeAmortissement.valueOf(dto.getTypeAmortissement().name()));
        Immobilisation savedImmobilisation = immobilisationRepository.save(immobilisation);
        generateAndAssignQRCode(immobilisation);
        immobilisationRepository.save(savedImmobilisation);
                System.out.println("Type dans DTO : " + dto.getTypeAmortissement());


        return mapper.toPolymorphicDTO(savedImmobilisation);

    }

    @Override
    @Transactional
    public ImmobilisationDTO updateImmobilisation(Long id, ImmobilisationDTO dto) {
        // Récupérer l'immobilisation existante
        Immobilisation ancienneImmobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + id));

        // Mise à jour des champs si non null dans le DTO
        if (dto.getDesignation() != null) {
            ancienneImmobilisation.setDesignation(dto.getDesignation());
        }

        if (dto.getDateAcquisition() != null) {
            ancienneImmobilisation.setDateAcquisition(dto.getDateAcquisition());
        }

        if (dto.getValeurAcquisition() != null) {
            ancienneImmobilisation.setValeurAcquisition(dto.getValeurAcquisition());
        }

        if (dto.getLocalisation() != null) {
            ancienneImmobilisation.setLocalisation(dto.getLocalisation());
        }

        if (dto.getDateMiseEnService() != null) {
            ancienneImmobilisation.setDateMiseEnService(dto.getDateMiseEnService());
        }

        if (dto.getEtatImmobilisation() != null) {
            ancienneImmobilisation.setEtatImmo(dto.getEtatImmobilisation());
        }

        if (dto.getAffectation() != null) {
            ancienneImmobilisation.setStatut(dto.getAffectation());
        }

        if (dto.getTypeAmortissement() != null) {
            ancienneImmobilisation.setTypeAmortissement(dto.getTypeAmortissement());
        }

        if (dto.getCategorieDesignation() != null) {
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec la désignation : " + dto.getCategorieDesignation()));
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }
            ancienneImmobilisation.setCategorie(categorie);
        }

        // Sauvegarder les modifications
        Immobilisation updatedImmobilisation = immobilisationRepository.save(ancienneImmobilisation);

        return mapper.toPolymorphicDTO(updatedImmobilisation);
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
            throw new RuntimeException("QR Code non disponible pour cette immobilisation");
        }

        return Base64.getDecoder().decode(immobilisation.getQrCode());
    }

    @Override
    public byte[] downloadQRCode(Long id) {
        Immobilisation immobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + id));

        if (immobilisation.getQrCode() == null || immobilisation.getQrCode().isEmpty()) {
            throw new RuntimeException("Le QR Code pour cette immobilisation n'existe pas.");
        }

        return Base64.getDecoder().decode(immobilisation.getQrCode());
    }

    @Override
    public ImmobilisationDTO getImmobilisationByCode(String codeImmo) {
        Immobilisation immobilisation = immobilisationRepository.findByCodeImmo(codeImmo)
                .orElseThrow(() -> new RuntimeException("Aucune immobilisation trouvée avec le code : " + codeImmo));
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
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + immobilisationId));

        immobilisation.setEtatImmo(nouvelEtat);
        immobilisationRepository.save(immobilisation);
    }

    private String generateQRCode(String text) throws Exception {
        int width = 250;
        int height = 250;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }


    @Override
    public List<ImmobilisationDTO> importImmobilisationsFromFile(MultipartFile file) throws IOException {
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

    private List<ImmobilisationDTO> processExcelFile(MultipartFile file) throws IOException {
        List<ImmobilisationDTO> immobilisations = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorer la ligne d'en-tête

                try {
                    ImmobilisationDTO dto = mapExcelRowToImmobilisationDTO(row);
                    System.out.println("DTO mappé à partir du fichier : " + dto); // Log du DTO
                    if (isValidImmobilisation(dto)) {
                        immobilisations.add(dto);
                    } else {
                        System.out.println("DTO invalide à la ligne " + (row.getRowNum() + 1) + ": " + dto);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement de la ligne " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        }
        return saveImportedImmobilisations(immobilisations);
    }


    private ImmobilisationDTO mapExcelRowToImmobilisationDTO(Row row) {
        try {
            ImmobilisationDTO dto = new ImmobilisationDTO();
            dto.setDesignation(getCellValue(row.getCell(0)).trim());
            dto.setCategorieDesignation(getCellValue(row.getCell(1)).trim());

            // Gestion de la date d'acquisition
            dto.setDateAcquisition(getCellDateValue(row.getCell(2)));

            // Gestion de la valeur d'acquisition
            dto.setValeurAcquisition(parseDouble(getCellValue(row.getCell(3))));

            dto.setLocalisation(getCellValue(row.getCell(4)).trim());

            // Gestion de la date de mise en service (peut être null)
            dto.setDateMiseEnService(getCellDateValue(row.getCell(5)));

            // Gestion du type d'immobilisation
            String typeImmobilisation = getCellValue(row.getCell(6)).trim().toUpperCase();
            dto.setType(TypeImmobilisation.valueOf(typeImmobilisation));

            // Gestion du type d'amortissement
            String typeAmortissement = getCellValue(row.getCell(7)).trim().toUpperCase().replace("É", "E").replace("È", "E");
            dto.setTypeAmortissement(TypeAmortissement.valueOf(typeAmortissement));

            // Champs par défaut
            dto.setAffectation(StatutAffectation.DISPONIBLE);
            dto.setEtatImmobilisation(EtatImmobilisation.EN_SERVICE);

            return dto;
        } catch (Exception e) {
            System.err.println("Erreur lors du mappage de la ligne " + row.getRowNum() + ": " + e.getMessage());
            e.printStackTrace(); // Ajout du stack trace pour faciliter le débogage
            throw new RuntimeException("Erreur lors du mappage de la ligne " + row.getRowNum(), e);
        }
    }

    // Méthode pour convertir une valeur en double avec gestion des erreurs
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Valeur invalide pour un nombre: " + value, e);
        }
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

                try {
                    ImmobilisationDTO dto = mapCsvRowToImmobilisationDTO(values);
                    System.out.println("DTO mappé à partir du fichier CSV : " + dto); // Log du DTO
                    if (isValidImmobilisation(dto)) {
                        immobilisations.add(dto);
                    } else {
                        System.out.println("DTO invalide pour la ligne CSV : " + Arrays.toString(values));
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement de la ligne CSV: " + Arrays.toString(values) + " - " + e.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException("Erreur de validation dans le fichier CSV.", e);
        }

        return saveImportedImmobilisations(immobilisations);
    }
    private ImmobilisationDTO mapCsvRowToImmobilisationDTO(String[] values) {
        try {
            ImmobilisationDTO dto = new ImmobilisationDTO();
            dto.setDesignation(values[0].trim());
            dto.setCategorieDesignation(values[1].trim());

            // Conversion de la date d'acquisition
            dto.setDateAcquisition(parseDate(values[2].trim())); // Utilise parseDate pour gérer le format "dd/MM/yyyy"

            dto.setValeurAcquisition(Double.parseDouble(values[3].trim()));
            dto.setLocalisation(values[4].trim());

            // Conversion de la date de mise en service (peut être null)
            if (values[5] != null && !values[5].trim().isEmpty()) {
                dto.setDateMiseEnService(parseDate(values[5].trim()));
            }

            // Conversion sans accents pour TypeImmobilisation
            String typeImmobilisation = values[6].trim().toUpperCase().replace("É", "E").replace("È", "E");
            dto.setType(TypeImmobilisation.valueOf(typeImmobilisation));

            // Conversion sans accents pour TypeAmortissement
            String typeAmortissement = values[7].trim().toUpperCase().replace("É", "E").replace("È", "E");
            dto.setTypeAmortissement(TypeAmortissement.valueOf(typeAmortissement));

            // Ajout des champs `statut` et `etatImmo`
            dto.setAffectation(StatutAffectation.DISPONIBLE); // Par défaut
            dto.setEtatImmobilisation(EtatImmobilisation.EN_SERVICE); // Par défaut

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du mappage des données CSV: " + Arrays.toString(values), e);
        }
    }



    private List<ImmobilisationDTO> saveImportedImmobilisations(List<ImmobilisationDTO> dtos) {
        List<Immobilisation> immobilisations = dtos.stream().map(dto -> {
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
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

    private LocalDate getCellDateValue(Cell cell) {
        if (cell == null) {
            logger.warn("Cellule vide ou date manquante.");
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                // Si la cellule contient une date Excel formatée
                LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                logger.info("Date extraite (format Excel) : {}", date);
                return date;
            } else if (cell.getCellType() == CellType.STRING) {
                // Si la cellule contient une date au format texte
                String cellValue = cell.getStringCellValue().trim();
                logger.info("Date au format texte détectée : {}", cellValue);
                return parseDate(cellValue);
            } else {
                logger.warn("Type de cellule inattendu pour une date.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la conversion de la date : {}", e.getMessage());
        }

        return null;
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
            throw new RuntimeException("Format de date invalide : " + date + ". Attendu : jj/MM/yyyy");
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

