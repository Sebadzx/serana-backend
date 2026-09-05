package com.upc.serana.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudARCODTO {
    @NotNull(message = "El tipo de derecho ARCO es obligatorio")
    private String tipoDerecho; // ACCESO, RECTIFICACION, CANCELACION, OPOSICION
    private String motivo;
}
