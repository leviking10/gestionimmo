package com.sodeca.gestionimmo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sodeca.gestionimmo.dto.ImmobilisationDTO;
import com.sodeca.gestionimmo.entity.Categorie;
import com.sodeca.gestionimmo.entity.Immobilisation;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutCession;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class ImmobilisationServiceImpl implements ImmobilisationService {

    private final ImmobilisationRepository immobilisationRepository;
    private final CategorieRepository categorieRepository;
    private final ImmobilisationMapper mapper;

    public ImmobilisationServiceImpl(ImmobilisationMapper mapper,
                                     ImmobilisationRepository immobilisationRepository,
                                     CategorieRepository categorieRepository) {
        this.mapper = mapper;
        this.immobilisationRepository = immobilisationRepository;
        this.categorieRepository = categorieRepository;
    }

    @Override
    public List<ImmobilisationDTO> getAllImmobilisations() {
        return immobilisationRepository.findAll()
                .stream()
                .map(mapper::toPolymorphicDTO)
                .toList();
    }

    @Override
    public Optional<ImmobilisationDTO> getImmobilisationById(Long id) {
        return immobilisationRepository.findById(id)
                .map(mapper::toPolymorphicDTO);
    }
    // Méthode pour générer un code séquentiel au format CODE00001
    private String generateSequentialCodeImmo() {
        // Récupérer le dernier code existant dans la base
        String dernierCode = immobilisationRepository.findTopByOrderByCodeImmoDesc()
                .map(Immobilisation::getCodeImmo)
                .orElse("CODE00001"); // Si aucun code n'existe, commencer à CODE00001

        // Extraire la partie numérique et incrémenter
        int numero = Integer.parseInt(dernierCode.replace("CODE", "")) + 1;

        // Formater le code en CODE00001
        return String.format("CODE%05d", numero);
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

            // Générer un code d'immobilisation unique au format CODE00001
            if (dto.getCodeImmo() == null || dto.getCodeImmo().isEmpty()) {
                immobilisation.setCodeImmo(generateSequentialCodeImmo());
            } else {
                if (immobilisationRepository.findByCodeImmo(dto.getCodeImmo()).isPresent()) {
                    throw new RuntimeException("Le code d'immobilisation est déjà utilisé : " + dto.getCodeImmo());
                }
                immobilisation.setCodeImmo(dto.getCodeImmo());
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
        Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec la désignation : " + dto.getCategorieDesignation()));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        // Convertir le DTO en entité
        Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);
        immobilisation.setCategorie(categorie);
        // Vérifier si le code est fourni, sinon le générer automatiquement
        if (dto.getCodeImmo() == null || dto.getCodeImmo().isEmpty()) {
            immobilisation.setCodeImmo(generateUniqueCodeImmo());
        } else {
            // Vérifier l'unicité du code fourni
            if (immobilisationRepository.findByCodeImmo(dto.getCodeImmo()).isPresent()) {
                throw new RuntimeException("Le code d'immobilisation est déjà utilisé : " + dto.getCodeImmo());
            }
            immobilisation.setCodeImmo(dto.getCodeImmo());
        }

        // Sauvegarder l'immobilisation dans la base de données
        Immobilisation savedImmobilisation = immobilisationRepository.save(immobilisation);

        // Générer et assigner un QR Code si nécessaire
        if (savedImmobilisation.getQrCode() == null || savedImmobilisation.getQrCode().isEmpty()) {
            try {
                String qrCodeData = "https://gestionimmo.sodeca.com/immobilisation/" + savedImmobilisation.getCodeImmo();
                String qrCodeBase64 = generateQRCode(qrCodeData);
                savedImmobilisation.setQrCode(qrCodeBase64);
                immobilisationRepository.save(savedImmobilisation);
                System.out.println("Type dans DTO : " + dto.getType());
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la génération du QR Code", e);
            }
        }

        return mapper.toPolymorphicDTO(savedImmobilisation);

    }
    @Override
    @Transactional
    public ImmobilisationDTO updateImmobilisation(Long id, ImmobilisationDTO dto) {
        // Récupérer l'immobilisation existante
        Immobilisation ancienneImmobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + id));

        // Vérifier si le type a changé
        if (dto.getType() != null && !dto.getType().equals(ancienneImmobilisation.getType())) {
            // Supprimer l'ancien enregistrement (avec les données spécifiques)
            immobilisationRepository.delete(ancienneImmobilisation);

            // Créer une nouvelle immobilisation avec le nouveau type
            Immobilisation nouvelleImmobilisation = mapper.toPolymorphicEntity(dto);

            // Associer la même catégorie si elle n'est pas modifiée
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + dto.getCategorieDesignation()));
            nouvelleImmobilisation.setCategorie(categorie);

            // Conserver l'ID et le code unique pour ne pas casser les relations
            nouvelleImmobilisation.setId(id);
            nouvelleImmobilisation.setCodeImmo(ancienneImmobilisation.getCodeImmo());

            // Sauvegarder la nouvelle immobilisation
            Immobilisation savedImmobilisation = immobilisationRepository.save(nouvelleImmobilisation);
            return mapper.toPolymorphicDTO(savedImmobilisation);
        }

        // Mise à jour des champs génériques (ignorer le champ codeImmo)
        ancienneImmobilisation.setDesignation(dto.getDesignation());
        ancienneImmobilisation.setDateAcquisition(dto.getDateAcquisition());
        ancienneImmobilisation.setLocalisation(dto.getLocalisation());
        ancienneImmobilisation.setDateMiseEnService(dto.getDateMiseEnService());
        ancienneImmobilisation.setValeurAcquisition(dto.getValeurAcquisition());
        ancienneImmobilisation.setEtatImmo(dto.getEtatImmobilisation());

        // Mise à jour de la catégorie
        if (dto.getCategorieDesignation() != null) {
            Categorie categorie = categorieRepository.findByCategorie(dto.getCategorieDesignation())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec la désignation : " + dto.getCategorieDesignation()));
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }
            ancienneImmobilisation.setCategorie(categorie);
        }

        // Sauvegarder l'immobilisation mise à jour
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
                        immobilisation.getStatutCession() == StatutCession.REBUTE)
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
}
