package com.sodeca.gestionimmo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
public class QRCodeService {

    public String generateQRCode(String text) {
        try {
            int width = 250;
            int height = 250;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // Utilisation d'EnumMap pour les clés de type Enum
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
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
        } catch (WriterException e) {
            // Exception spécifique liée à la génération du QR Code
            throw new BusinessException("Erreur lors de la création du QR Code : " + e.getMessage(),
                    "QR_CODE_GENERATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, e);
        } catch (IOException e) {
            // Exception spécifique liée à l'écriture de l'image
            throw new BusinessException("Erreur lors de la création du fichier image pour le QR Code : " + e.getMessage(),
                    "IMAGE_CREATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            // Exception générique pour couvrir les autres cas
            throw new BusinessException("Erreur inattendue lors de la génération du QR Code : " + e.getMessage(),
                    "UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

}
