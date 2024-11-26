package com.sodeca.gestionimmo.dto;

import com.sodeca.gestionimmo.enums.StatutCession;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CessionDTO {

    private Long id; // Identifiant de la cession

    @NotNull(message = "L'identifiant de l'immobilisation est obligatoire")
    private Long immobilisationId; // Identifiant de l'immobilisation associée

    @NotNull(message = "Le statut de la cession est obligatoire")
    private StatutCession statutCession; // Statut (VENDU ou REBUTE)

    @NotNull(message = "La date de cession est obligatoire")
    private LocalDate dateCession; // Date de cession ou de rebut

    private Double valeurCession; // Valeur de la cession (ou valeur résiduelle)
}
