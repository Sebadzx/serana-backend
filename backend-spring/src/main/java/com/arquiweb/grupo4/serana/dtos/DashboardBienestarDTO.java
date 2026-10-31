package com.arquiweb.grupo4.serana.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public class DashboardBienestarDTO {

    private long totalEstudiantesEvaluados;
    private long casosCriticos;
    private Map<String, Long> distribucionAnsiedad;
    private Double porcentajeCasosRiesgo;
    private LocalDateTime fechaActualizacion;

    public DashboardBienestarDTO() {
    }

    public DashboardBienestarDTO(long totalEstudiantesEvaluados, long casosCriticos, Map<String, Long> distribucionAnsiedad, Double porcentajeCasosRiesgo, LocalDateTime fechaActualizacion) {
        this.totalEstudiantesEvaluados = totalEstudiantesEvaluados;
        this.casosCriticos = casosCriticos;
        this.distribucionAnsiedad = distribucionAnsiedad;
        this.porcentajeCasosRiesgo = porcentajeCasosRiesgo;
        this.fechaActualizacion = fechaActualizacion;
    }

    public long getTotalEstudiantesEvaluados() {
        return totalEstudiantesEvaluados;
    }

    public void setTotalEstudiantesEvaluados(long totalEstudiantesEvaluados) {
        this.totalEstudiantesEvaluados = totalEstudiantesEvaluados;
    }

    public long getCasosCriticos() {
        return casosCriticos;
    }

    public void setCasosCriticos(long casosCriticos) {
        this.casosCriticos = casosCriticos;
    }

    public Map<String, Long> getDistribucionAnsiedad() {
        return distribucionAnsiedad;
    }

    public void setDistribucionAnsiedad(Map<String, Long> distribucionAnsiedad) {
        this.distribucionAnsiedad = distribucionAnsiedad;
    }

    public Double getPorcentajeCasosRiesgo() {
        return porcentajeCasosRiesgo;
    }

    public void setPorcentajeCasosRiesgo(Double porcentajeCasosRiesgo) {
        this.porcentajeCasosRiesgo = porcentajeCasosRiesgo;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public static DashboardBienestarDTOBuilder builder() {
        return new DashboardBienestarDTOBuilder();
    }

    public static class DashboardBienestarDTOBuilder {
        private long totalEstudiantesEvaluados;
        private long casosCriticos;
        private Map<String, Long> distribucionAnsiedad;
        private Double porcentajeCasosRiesgo;
        private LocalDateTime fechaActualizacion;

        public DashboardBienestarDTOBuilder totalEstudiantesEvaluados(long totalEstudiantesEvaluados) {
            this.totalEstudiantesEvaluados = totalEstudiantesEvaluados;
            return this;
        }

        public DashboardBienestarDTOBuilder casosCriticos(long casosCriticos) {
            this.casosCriticos = casosCriticos;
            return this;
        }

        public DashboardBienestarDTOBuilder distribucionAnsiedad(Map<String, Long> distribucionAnsiedad) {
            this.distribucionAnsiedad = distribucionAnsiedad;
            return this;
        }

        public DashboardBienestarDTOBuilder porcentajeCasosRiesgo(Double porcentajeCasosRiesgo) {
            this.porcentajeCasosRiesgo = porcentajeCasosRiesgo;
            return this;
        }

        public DashboardBienestarDTOBuilder fechaActualizacion(LocalDateTime fechaActualizacion) {
            this.fechaActualizacion = fechaActualizacion;
            return this;
        }

        public DashboardBienestarDTO build() {
            return new DashboardBienestarDTO(totalEstudiantesEvaluados, casosCriticos, distribucionAnsiedad, porcentajeCasosRiesgo, fechaActualizacion);
        }
    }
}
