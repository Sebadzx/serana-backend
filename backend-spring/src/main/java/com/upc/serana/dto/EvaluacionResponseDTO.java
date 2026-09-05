package com.upc.serana.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EvaluacionResponseDTO {
    private Long evaluacionId;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private String nivelAnsiedad;
    private Double probabilidadRiesgo;
    private Boolean alertaCritica;
    private String reglaClinicaAplicada;
    private List<String> recomendaciones;
    private Object topFactores;
    private Object indicadoresPLN;
}
