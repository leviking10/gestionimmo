package com.sodeca.gestionimmo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelephoneDTO extends ImmobilisationDTO {
    private String marque;
    private String modele;
    private String imei;
    private String numeroSerie;
}
