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
public class InferenciaMLRequestDTO {
    private Long evaluacionId;
    private PerfilEstudianteDTO perfilEstudiante;
    private EscalasDTO escalas;
    private String textoLibre;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerfilEstudianteDTO {
        private String carrera;
        private Integer ciclo;
        private Double horasPantallaDia;
        private Double horasRedesSociales;
        private Double horasEstudioVirtual;
        private String dispositivoPrincipal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EscalasDTO {
        private List<Integer> ts4usRespuestas;
        private List<Integer> asaidasRespuestas;
    }
}
