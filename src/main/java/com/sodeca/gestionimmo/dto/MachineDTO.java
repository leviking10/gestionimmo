package com.sodeca.gestionimmo.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MachineDTO extends ImmobilisationDTO {
    private String typeMachine; // Type de machine (ex : Remplisseuse, Souffleuse)
    private String puissance; // Puissance (ex : "5 kW")
    private String fabricant; // Fabricant de la machine
    private String numeroSerie; // Numéro de série unique
}
