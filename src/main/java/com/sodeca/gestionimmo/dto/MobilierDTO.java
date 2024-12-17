package com.sodeca.gestionimmo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobilierDTO extends ImmobilisationDTO {
    private String typeMobilier; // Type de mobilier, ex : "Chaise", "Table", "Étagère"
    private String materiau; // Matériau, ex : "Bois", "Métal", "Plastique"
}
