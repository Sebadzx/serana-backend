package com.upc.serana.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsentimientoRequestDTO {
    @NotNull(message = "Debe indicar si acepta o no el consentimiento informado")
    private Boolean aceptado;
    private String versionPolitica;
}
