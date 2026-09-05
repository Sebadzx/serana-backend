package com.upc.serana.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
@Builder
public class DashboardBienestarDTO {
    private long totalEvaluaciones;
    private long evaluacionesCompletadas;
    private long alertasCriticasActivas;
    private Map<String, Long> distribucionNiveles; // BAJO, MODERADO, ALTO
    private List<AlertaResumenDTO> ultimasAlertasCriticas;

    @Data
    @Builder
    public static class AlertaResumenDTO {
        private Long evaluacionId;
        private String carrera;
        private Integer ciclo;
        private String nivelAnsiedad;
        private Double probabilidad;
        private String reglaDisparada;
        private String fecha;
    }
}
