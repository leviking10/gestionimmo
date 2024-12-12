package com.sodeca.gestionimmo.entity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Machine extends Immobilisation {
    private String typeMachine; // Exemple : Remplisseuse, Souffleuse
    private String puissance; // Exemple : 5 kW, 10 kW
    private String fabricant; // Nom du fabricant
    private String numeroSerie; // Numéro de série unique
}
