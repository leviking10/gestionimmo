package com.sodeca.gestionimmo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class SignalementDTO {
    private Long id;
    private Long personnelId;
    private Long immobilisationId;
    private String description;
    private LocalDateTime dateSignalement;
}
