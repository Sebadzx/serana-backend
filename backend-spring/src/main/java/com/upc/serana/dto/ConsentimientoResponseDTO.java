package com.upc.serana.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ConsentimientoResponseDTO {
    private Long id;
    private boolean aceptado;
    private String versionPolitica;
    private LocalDateTime fechaAceptacion;
    private String mensaje;
}
