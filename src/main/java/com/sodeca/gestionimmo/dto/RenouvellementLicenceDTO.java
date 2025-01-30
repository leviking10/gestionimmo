package com.sodeca.gestionimmo.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenouvellementLicenceDTO {

    private Long id; // ID du renouvellement
    private Long licenceId; // ID de la licence concernée
    private String nomLicence; // Nom de la licence pour affichage
    private LocalDate dateRenouvellement; // Date du renouvellement
    private int quantiteAjoutee; // Quantité ajoutée
    private double coutRenouvellement; // Coût du renouvellement
}
