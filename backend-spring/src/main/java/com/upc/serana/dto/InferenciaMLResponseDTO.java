package com.upc.serana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenciaMLResponseDTO {
    private Long evaluacionId;
    private String nivelAnsiedad;
    private Double probabilidadRiesgo;
    private Boolean alertaCritica;
    private String reglaClinicaDisparada;
    private IndicadoresPLNDTO indicadoresPLN;
    private List<FeatureImportanceDTO> featureImportance;
    private List<String> recomendacionesICBT;
    private Double tiempoInferenciaMs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndicadoresPLNDTO {
        private Double polaridadSentimiento;
        private Integer conteoTerminosArgot;
        private List<String> terminosDetectados;
        private Double densidadEstres;
        private Integer longitudPalabras;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureImportanceDTO {
        private String factor;
        private Double peso;
    }
}
