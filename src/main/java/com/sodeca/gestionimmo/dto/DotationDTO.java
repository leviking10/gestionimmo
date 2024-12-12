package com.sodeca.gestionimmo.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DotationDTO {
    private Long id;
    private Long technicienId;
    private Long pieceId;
    private int quantite;
    private LocalDateTime dateDotation;
}
