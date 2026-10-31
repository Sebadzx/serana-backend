package com.arquiweb.grupo4.serana.dtos;

import java.util.List;
import java.util.Map;

public class MLInferenceResponseDTO {

    private String nivelAnsiedadPredicho;
    private String prediccionRandomForest;
    private Map<String, Double> probabilidadesRandomForest;
    private Boolean modificadoPorRegla;
    private String reglaClinicaDisparada;
    private Double scoreProbabilidad;
    private Boolean esCasoCritico;
    private String justificacionCli;
    private List<String> estresoresDetectados;
    private Double polaridadSentimiento;

    public MLInferenceResponseDTO() {
    }

    public String getNivelAnsiedadPredicho() {
        return nivelAnsiedadPredicho;
    }

    public void setNivelAnsiedadPredicho(String nivelAnsiedadPredicho) {
        this.nivelAnsiedadPredicho = nivelAnsiedadPredicho;
    }

    public String getPrediccionRandomForest() {
        return prediccionRandomForest;
    }

    public void setPrediccionRandomForest(String prediccionRandomForest) {
        this.prediccionRandomForest = prediccionRandomForest;
    }

    public Map<String, Double> getProbabilidadesRandomForest() {
        return probabilidadesRandomForest;
    }

    public void setProbabilidadesRandomForest(Map<String, Double> probabilidadesRandomForest) {
        this.probabilidadesRandomForest = probabilidadesRandomForest;
    }

    public Boolean getModificadoPorRegla() {
        return modificadoPorRegla;
    }

    public void setModificadoPorRegla(Boolean modificadoPorRegla) {
        this.modificadoPorRegla = modificadoPorRegla;
    }

    public String getReglaClinicaDisparada() {
        return reglaClinicaDisparada;
    }

    public void setReglaClinicaDisparada(String reglaClinicaDisparada) {
        this.reglaClinicaDisparada = reglaClinicaDisparada;
    }

    public Double getScoreProbabilidad() {
        return scoreProbabilidad;
    }

    public void setScoreProbabilidad(Double scoreProbabilidad) {
        this.scoreProbabilidad = scoreProbabilidad;
    }

    public Boolean getEsCasoCritico() {
        return esCasoCritico;
    }

    public void setEsCasoCritico(Boolean esCasoCritico) {
        this.esCasoCritico = esCasoCritico;
    }

    public String getJustificacionCli() {
        return justificacionCli;
    }

    public void setJustificacionCli(String justificacionCli) {
        this.justificacionCli = justificacionCli;
    }

    public List<String> getEstresoresDetectados() {
        return estresoresDetectados;
    }

    public void setEstresoresDetectados(List<String> estresoresDetectados) {
        this.estresoresDetectados = estresoresDetectados;
    }

    public Double getPolaridadSentimiento() {
        return polaridadSentimiento;
    }

    public void setPolaridadSentimiento(Double polaridadSentimiento) {
        this.polaridadSentimiento = polaridadSentimiento;
    }

    public static MLInferenceResponseDTOBuilder builder() {
        return new MLInferenceResponseDTOBuilder();
    }

    public static class MLInferenceResponseDTOBuilder {
        private String nivelAnsiedadPredicho;
        private String prediccionRandomForest;
        private Map<String, Double> probabilidadesRandomForest;
        private Boolean modificadoPorRegla;
        private String reglaClinicaDisparada;
        private Double scoreProbabilidad;
        private Boolean esCasoCritico;
        private String justificacionCli;
        private List<String> estresoresDetectados;
        private Double polaridadSentimiento;

        public MLInferenceResponseDTOBuilder nivelAnsiedadPredicho(String nivelAnsiedadPredicho) {
            this.nivelAnsiedadPredicho = nivelAnsiedadPredicho;
            return this;
        }

        public MLInferenceResponseDTOBuilder prediccionRandomForest(String prediccionRandomForest) {
            this.prediccionRandomForest = prediccionRandomForest;
            return this;
        }

        public MLInferenceResponseDTOBuilder probabilidadesRandomForest(Map<String, Double> probabilidadesRandomForest) {
            this.probabilidadesRandomForest = probabilidadesRandomForest;
            return this;
        }

        public MLInferenceResponseDTOBuilder modificadoPorRegla(Boolean modificadoPorRegla) {
            this.modificadoPorRegla = modificadoPorRegla;
            return this;
        }

        public MLInferenceResponseDTOBuilder reglaClinicaDisparada(String reglaClinicaDisparada) {
            this.reglaClinicaDisparada = reglaClinicaDisparada;
            return this;
        }

        public MLInferenceResponseDTOBuilder scoreProbabilidad(Double scoreProbabilidad) {
            this.scoreProbabilidad = scoreProbabilidad;
            return this;
        }

        public MLInferenceResponseDTOBuilder esCasoCritico(Boolean esCasoCritico) {
            this.esCasoCritico = esCasoCritico;
            return this;
        }

        public MLInferenceResponseDTOBuilder justificacionCli(String justificacionCli) {
            this.justificacionCli = justificacionCli;
            return this;
        }

        public MLInferenceResponseDTOBuilder estresoresDetectados(List<String> estresoresDetectados) {
            this.estresoresDetectados = estresoresDetectados;
            return this;
        }

        public MLInferenceResponseDTOBuilder polaridadSentimiento(Double polaridadSentimiento) {
            this.polaridadSentimiento = polaridadSentimiento;
            return this;
        }

        public MLInferenceResponseDTO build() {
            MLInferenceResponseDTO dto = new MLInferenceResponseDTO();
            dto.setNivelAnsiedadPredicho(nivelAnsiedadPredicho);
            dto.setPrediccionRandomForest(prediccionRandomForest);
            dto.setProbabilidadesRandomForest(probabilidadesRandomForest);
            dto.setModificadoPorRegla(modificadoPorRegla);
            dto.setReglaClinicaDisparada(reglaClinicaDisparada);
            dto.setScoreProbabilidad(scoreProbabilidad);
            dto.setEsCasoCritico(esCasoCritico);
            dto.setJustificacionCli(justificacionCli);
            dto.setEstresoresDetectados(estresoresDetectados);
            dto.setPolaridadSentimiento(polaridadSentimiento);
            return dto;
        }
    }
}
