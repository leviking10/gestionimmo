package com.sodeca.gestionimmo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sodeca.gestionimmo.dto.*;
import com.sodeca.gestionimmo.entity.*;
import com.sodeca.gestionimmo.enums.StatutCession;
import com.sodeca.gestionimmo.mapper.ImmobilisationMapper;
import com.sodeca.gestionimmo.repository.CategorieRepository;
import com.sodeca.gestionimmo.repository.ImmobilisationRepository;
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
    public List<ImmobilisationDTO> createImmobilisations(List<ImmobilisationDTO> dtos) {
        List<Immobilisation> immobilisations = dtos.stream().map(dto -> {
            // Récupérer la catégorie associée
            Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'ID : " + dto.getCategorieId()));

            // Vérifier si la catégorie est active
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }
                        // Convertir le DTO en entité
            Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);
            immobilisation.setCategorie(categorie);

            // Générer et assigner un QR Code
            generateAndAssignQRCode(immobilisation, dto);

            return immobilisation;
        }).toList();

        // Sauvegarder toutes les immobilisations
        List<Immobilisation> savedImmobilisations = immobilisationRepository.saveAll(immobilisations);

        // Retourner la liste des DTOs sauvegardés
        return savedImmobilisations.stream()
                .map(mapper::toPolymorphicDTO)
                .toList();
    }

    @Override
    public List<ImmobilisationDTO> getAllImmobilisations() {
        return immobilisationRepository.findAll()
                .stream()
                .map(mapper::toPolymorphicDTO) // Utiliser la conversion polymorphique
                .toList();
    }
    @Override
    public Optional<ImmobilisationDTO> getImmobilisationById(Long id) {
        return immobilisationRepository.findById(id)
                .map(mapper::toPolymorphicDTO);
    }

    @Override
    public void deleteImmobilisation(Long id) {
        immobilisationRepository.deleteById(id);
    }

    @Override
    public ImmobilisationDTO updateImmobilisation(Long id, ImmobilisationDTO dto) {
        Immobilisation immobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cette immobilisation n'existe pas"));

        // Mise à jour des champs communs
        immobilisation.setDesignation(dto.getDesignation());

        // Si la catégorie est mise à jour
        if (dto.getId() != null) {
            Categorie categorie = categorieRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

            // Vérifiez si la catégorie est active avant de l'assigner
            if (!categorie.isActif()) {
                throw new RuntimeException("La catégorie sélectionnée est désactivée.");
            }

            immobilisation.setCategorie(categorie); // Mise à jour de la relation
        }
        immobilisation.setDateAcquisition(dto.getDateAcquisition());
        immobilisation.setLocalisation(dto.getLocalisation());
        immobilisation.setDateMiseEnService(dto.getDateMiseEnService());
        Immobilisation updated = immobilisationRepository.save(immobilisation);
        return mapper.toPolymorphicDTO(updated);
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
    public List<ImmobilisationDTO> getImmobilisationsCedees() {
        // Rechercher les immobilisations ayant un statut de cession défini
        List<Immobilisation> immobilisationsCedees = immobilisationRepository.findAll().stream()
                .filter(immobilisation -> immobilisation.getStatutCession() == StatutCession.VENDU ||
                        immobilisation.getStatutCession() == StatutCession.REBUTE)
                .toList();

        // Convertir en DTO et retourner la liste
        return immobilisationsCedees.stream()
                .map(mapper::toPolymorphicDTO)
                .toList();
    }
    @Override
    public ImmobilisationDTO getImmobilisationByQRCode(String qrCodeData) {
        // Décoder les données du QR Code pour extraire l'identifiant
        String immobilisationId = extractIdFromQRCode(qrCodeData);

        // Récupérer l'immobilisation par son ID
        Immobilisation immobilisation = immobilisationRepository.findById(Long.parseLong(immobilisationId))
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + immobilisationId));

        // Convertir l'entité en DTO
        return mapper.toPolymorphicDTO(immobilisation);
    }

    // Méthode utilitaire pour extraire l'ID depuis le QR Code
    private String extractIdFromQRCode(String qrCodeData) {
        // Exemple de traitement basique : extraire l'ID depuis une URL dans le QR Code
        String[] parts = qrCodeData.split("/");
        return parts[parts.length - 1]; // Dernier segment de l'URL
    }

    // Méthode utilitaire pour générer et assigner un QR Code
    void generateAndAssignQRCode(Immobilisation immobilisation, ImmobilisationDTO dto) {
        try {
            String qrCodeData = "ID: " + dto.getId() + ", Désignation: " + dto.getDesignation() + ", Catégorie: " + immobilisation.getCategorie();
            String qrCode = generateQRCode(qrCodeData);
            immobilisation.setQrCode(qrCode);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR Code", e);
        }
    }

    @Override
    public ImmobilisationDTO createImmobilisation(ImmobilisationDTO dto) {
        // Récupérer la catégorie associée
        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        // Vérifier si la catégorie est active
        if (!categorie.isActif()) {
            throw new RuntimeException("La catégorie sélectionnée est désactivée.");
        }

        // Convertir le DTO en entité
        Immobilisation immobilisation = mapper.toPolymorphicEntity(dto);
        immobilisation.setCategorie(categorie);

        // Sauvegarder l'immobilisation dans la base de données pour obtenir l'ID
        Immobilisation savedImmobilisation = immobilisationRepository.save(immobilisation);

        // Vérifier si le QR Code est déjà généré, sinon le générer
        if (savedImmobilisation.getQrCode() == null || savedImmobilisation.getQrCode().isEmpty()) {
            try {
                String qrCodeData = "https://gestionimmo.sodeca.com/immobilisation/" + savedImmobilisation.getId();
                String qrCodeBase64 = generateQRCode(qrCodeData);
                savedImmobilisation.setQrCode(qrCodeBase64);
                immobilisationRepository.save(savedImmobilisation); // Mettre à jour avec le QR Code
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la génération du QR Code", e);
            }
        }

        return mapper.toPolymorphicDTO(savedImmobilisation);
    }

    @Override
    public byte[] downloadQRCode(Long id) {
        Immobilisation immobilisation = immobilisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immobilisation introuvable avec l'ID : " + id));

        // Vérifier si le QR Code existe
        if (immobilisation.getQrCode() == null || immobilisation.getQrCode().isEmpty()) {
            throw new RuntimeException("Le QR Code pour cette immobilisation n'existe pas.");
        }

        // Décoder et retourner le QR Code
        return Base64.getDecoder().decode(immobilisation.getQrCode());
    }

    // Méthode utilitaire pour générer le QR Code
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
