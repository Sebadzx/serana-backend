package com.arquiweb.grupo4.serana.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EvaluacionResponseDTO {

    private Long idEvaluacion;
    private LocalDateTime fechaEvaluacion;
    private String nivelAnsiedadPredicho;
    private String prediccionRandomForest;
    private Map<String, Double> probabilidadesRandomForest;
    private Boolean modificadoPorRegla;
    private String reglaClinicaDisparada;
    private Double scoreProbabilidad;
    private Boolean esCasoCritico;
    private String justificacionCli;
    private String estresoresDetectados;
    private Double polaridadSentimiento;
    private List<RecomendacionDTO> recomendacionesICBT;

    public EvaluacionResponseDTO() {
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(Long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
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

    public String getEstresoresDetectados() {
        return estresoresDetectados;
    }

    public void setEstresoresDetectados(String estresoresDetectados) {
        this.estresoresDetectados = estresoresDetectados;
    }

    public Double getPolaridadSentimiento() {
        return polaridadSentimiento;
    }

    public void setPolaridadSentimiento(Double polaridadSentimiento) {
        this.polaridadSentimiento = polaridadSentimiento;
    }

    public List<RecomendacionDTO> getRecomendacionesICBT() {
        return recomendacionesICBT;
    }

    public void setRecomendacionesICBT(List<RecomendacionDTO> recomendacionesICBT) {
        this.recomendacionesICBT = recomendacionesICBT;
    }

    public static EvaluacionResponseDTOBuilder builder() {
        return new EvaluacionResponseDTOBuilder();
    }

    public static class EvaluacionResponseDTOBuilder {
        private Long idEvaluacion;
        private LocalDateTime fechaEvaluacion;
        private String nivelAnsiedadPredicho;
        private String prediccionRandomForest;
        private Map<String, Double> probabilidadesRandomForest;
        private Boolean modificadoPorRegla;
        private String reglaClinicaDisparada;
        private Double scoreProbabilidad;
        private Boolean esCasoCritico;
        private String justificacionCli;
        private String estresoresDetectados;
        private Double polaridadSentimiento;
        private List<RecomendacionDTO> recomendacionesICBT;

        public EvaluacionResponseDTOBuilder idEvaluacion(Long idEvaluacion) {
            this.idEvaluacion = idEvaluacion;
            return this;
        }

        public EvaluacionResponseDTOBuilder fechaEvaluacion(LocalDateTime fechaEvaluacion) {
            this.fechaEvaluacion = fechaEvaluacion;
            return this;
        }

        public EvaluacionResponseDTOBuilder nivelAnsiedadPredicho(String nivelAnsiedadPredicho) {
            this.nivelAnsiedadPredicho = nivelAnsiedadPredicho;
            return this;
        }

        public EvaluacionResponseDTOBuilder prediccionRandomForest(String prediccionRandomForest) {
            this.prediccionRandomForest = prediccionRandomForest;
            return this;
        }

        public EvaluacionResponseDTOBuilder probabilidadesRandomForest(Map<String, Double> probabilidadesRandomForest) {
            this.probabilidadesRandomForest = probabilidadesRandomForest;
            return this;
        }

        public EvaluacionResponseDTOBuilder modificadoPorRegla(Boolean modificadoPorRegla) {
            this.modificadoPorRegla = modificadoPorRegla;
            return this;
        }

        public EvaluacionResponseDTOBuilder reglaClinicaDisparada(String reglaClinicaDisparada) {
            this.reglaClinicaDisparada = reglaClinicaDisparada;
            return this;
        }

        public EvaluacionResponseDTOBuilder scoreProbabilidad(Double scoreProbabilidad) {
            this.scoreProbabilidad = scoreProbabilidad;
            return this;
        }

        public EvaluacionResponseDTOBuilder esCasoCritico(Boolean esCasoCritico) {
            this.esCasoCritico = esCasoCritico;
            return this;
        }

        public EvaluacionResponseDTOBuilder justificacionCli(String justificacionCli) {
            this.justificacionCli = justificacionCli;
            return this;
        }

        public EvaluacionResponseDTOBuilder estresoresDetectados(String estresoresDetectados) {
            this.estresoresDetectados = estresoresDetectados;
            return this;
        }

        public EvaluacionResponseDTOBuilder polaridadSentimiento(Double polaridadSentimiento) {
            this.polaridadSentimiento = polaridadSentimiento;
            return this;
        }

        public EvaluacionResponseDTOBuilder recomendacionesICBT(List<RecomendacionDTO> recomendacionesICBT) {
            this.recomendacionesICBT = recomendacionesICBT;
            return this;
        }

        public EvaluacionResponseDTO build() {
            EvaluacionResponseDTO dto = new EvaluacionResponseDTO();
            dto.setIdEvaluacion(idEvaluacion);
            dto.setFechaEvaluacion(fechaEvaluacion);
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
            dto.setRecomendacionesICBT(recomendacionesICBT);
            return dto;
        }
    }

    public static class RecomendacionDTO {
        private Long id;
        private String titulo;
        private String moduloICBT;
        private String descripcion;
        private Boolean completada;

        public RecomendacionDTO() {
        }

        public RecomendacionDTO(Long id, String titulo, String moduloICBT, String descripcion, Boolean completada) {
            this.id = id;
            this.titulo = titulo;
            this.moduloICBT = moduloICBT;
            this.descripcion = descripcion;
            this.completada = completada;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getModuloICBT() {
            return moduloICBT;
        }

        public void setModuloICBT(String moduloICBT) {
            this.moduloICBT = moduloICBT;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Boolean getCompletada() {
            return completada;
        }

        public void setCompletada(Boolean completada) {
            this.completada = completada;
        }

        public static RecomendacionDTOBuilder builder() {
            return new RecomendacionDTOBuilder();
        }

        public static class RecomendacionDTOBuilder {
            private Long id;
            private String titulo;
            private String moduloICBT;
            private String descripcion;
            private Boolean completada;

            public RecomendacionDTOBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public RecomendacionDTOBuilder titulo(String titulo) {
                this.titulo = titulo;
                return this;
            }

            public RecomendacionDTOBuilder moduloICBT(String moduloICBT) {
                this.moduloICBT = moduloICBT;
                return this;
            }

            public RecomendacionDTOBuilder descripcion(String descripcion) {
                this.descripcion = descripcion;
                return this;
            }

            public RecomendacionDTOBuilder completada(Boolean completada) {
                this.completada = completada;
                return this;
            }

            public RecomendacionDTO build() {
                return new RecomendacionDTO(id, titulo, moduloICBT, descripcion, completada);
            }
        }
    }
}
