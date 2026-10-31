package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resultados_inferencia")
public class ResultadoInferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private EvaluacionPsicometrica evaluacion;

    @Column(name = "nivel_ansiedad_predicho", nullable = false)
    private String nivelAnsiedadPredicho;

    @Column(name = "prediccion_random_forest")
    private String prediccionRandomForest;

    @Column(name = "probabilidades_rf_json", columnDefinition = "TEXT")
    private String probabilidadesRfJson;

    @Column(name = "modificado_por_regla")
    private Boolean modificadoPorRegla;

    @Column(name = "regla_clinica_disparada")
    private String reglaClinicaDisparada;

    @Column(name = "score_probabilidad")
    private Double scoreProbabilidad;

    @Column(name = "es_caso_critico")
    private Boolean esCasoCritico;

    @Column(name = "justificacion_cli", columnDefinition = "TEXT")
    private String justificacionCli;

    @Column(name = "fecha_inferencia", nullable = false)
    private LocalDateTime fechaInferencia;

    public ResultadoInferencia() {
    }

    public ResultadoInferencia(Long id, EvaluacionPsicometrica evaluacion, String nivelAnsiedadPredicho, Double scoreProbabilidad, Boolean esCasoCritico, String justificacionCli, LocalDateTime fechaInferencia) {
        this.id = id;
        this.evaluacion = evaluacion;
        this.nivelAnsiedadPredicho = nivelAnsiedadPredicho;
        this.scoreProbabilidad = scoreProbabilidad;
        this.esCasoCritico = esCasoCritico;
        this.justificacionCli = justificacionCli;
        this.fechaInferencia = fechaInferencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EvaluacionPsicometrica getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(EvaluacionPsicometrica evaluacion) {
        this.evaluacion = evaluacion;
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

    public String getProbabilidadesRfJson() {
        return probabilidadesRfJson;
    }

    public void setProbabilidadesRfJson(String probabilidadesRfJson) {
        this.probabilidadesRfJson = probabilidadesRfJson;
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

    public LocalDateTime getFechaInferencia() {
        return fechaInferencia;
    }

    public void setFechaInferencia(LocalDateTime fechaInferencia) {
        this.fechaInferencia = fechaInferencia;
    }

    public static ResultadoInferenciaBuilder builder() {
        return new ResultadoInferenciaBuilder();
    }

    public static class ResultadoInferenciaBuilder {
        private Long id;
        private EvaluacionPsicometrica evaluacion;
        private String nivelAnsiedadPredicho;
        private String prediccionRandomForest;
        private String probabilidadesRfJson;
        private Boolean modificadoPorRegla;
        private String reglaClinicaDisparada;
        private Double scoreProbabilidad;
        private Boolean esCasoCritico;
        private String justificacionCli;
        private LocalDateTime fechaInferencia;

        public ResultadoInferenciaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResultadoInferenciaBuilder evaluacion(EvaluacionPsicometrica evaluacion) {
            this.evaluacion = evaluacion;
            return this;
        }

        public ResultadoInferenciaBuilder nivelAnsiedadPredicho(String nivelAnsiedadPredicho) {
            this.nivelAnsiedadPredicho = nivelAnsiedadPredicho;
            return this;
        }

        public ResultadoInferenciaBuilder prediccionRandomForest(String prediccionRandomForest) {
            this.prediccionRandomForest = prediccionRandomForest;
            return this;
        }

        public ResultadoInferenciaBuilder probabilidadesRfJson(String probabilidadesRfJson) {
            this.probabilidadesRfJson = probabilidadesRfJson;
            return this;
        }

        public ResultadoInferenciaBuilder modificadoPorRegla(Boolean modificadoPorRegla) {
            this.modificadoPorRegla = modificadoPorRegla;
            return this;
        }

        public ResultadoInferenciaBuilder reglaClinicaDisparada(String reglaClinicaDisparada) {
            this.reglaClinicaDisparada = reglaClinicaDisparada;
            return this;
        }

        public ResultadoInferenciaBuilder scoreProbabilidad(Double scoreProbabilidad) {
            this.scoreProbabilidad = scoreProbabilidad;
            return this;
        }

        public ResultadoInferenciaBuilder esCasoCritico(Boolean esCasoCritico) {
            this.esCasoCritico = esCasoCritico;
            return this;
        }

        public ResultadoInferenciaBuilder justificacionCli(String justificacionCli) {
            this.justificacionCli = justificacionCli;
            return this;
        }

        public ResultadoInferenciaBuilder fechaInferencia(LocalDateTime fechaInferencia) {
            this.fechaInferencia = fechaInferencia;
            return this;
        }

        public ResultadoInferencia build() {
            ResultadoInferencia r = new ResultadoInferencia(id, evaluacion, nivelAnsiedadPredicho, scoreProbabilidad, esCasoCritico, justificacionCli, fechaInferencia);
            r.setPrediccionRandomForest(prediccionRandomForest);
            r.setProbabilidadesRfJson(probabilidadesRfJson);
            r.setModificadoPorRegla(modificadoPorRegla);
            r.setReglaClinicaDisparada(reglaClinicaDisparada);
            return r;
        }
    }
}
