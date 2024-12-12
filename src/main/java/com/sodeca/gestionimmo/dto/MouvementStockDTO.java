package com.sodeca.gestionimmo.dto;

import com.sodeca.gestionimmo.enums.TypeMouvement;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MouvementStockDTO {

    private Long id;
    private Long pieceId;
    private String referencePiece;
    private int quantite;
    private TypeMouvement typeMouvement;
    private LocalDateTime dateMouvement;
    private String commentaire;
}
