package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "recomendaciones_icbt")
public class RecomendacionICBT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultado_inferencia_id", nullable = false)
    private ResultadoInferencia resultadoInferencia;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "modulo_icbt", nullable = false)
    private String moduloICBT;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean completada = false;

    public RecomendacionICBT() {
    }

    public RecomendacionICBT(Long id, ResultadoInferencia resultadoInferencia, String titulo, String moduloICBT, String descripcion, Boolean completada) {
        this.id = id;
        this.resultadoInferencia = resultadoInferencia;
        this.titulo = titulo;
        this.moduloICBT = moduloICBT;
        this.descripcion = descripcion;
        this.completada = completada != null ? completada : false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResultadoInferencia getResultadoInferencia() {
        return resultadoInferencia;
    }

    public void setResultadoInferencia(ResultadoInferencia resultadoInferencia) {
        this.resultadoInferencia = resultadoInferencia;
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

    public static RecomendacionICBTBuilder builder() {
        return new RecomendacionICBTBuilder();
    }

    public static class RecomendacionICBTBuilder {
        private Long id;
        private ResultadoInferencia resultadoInferencia;
        private String titulo;
        private String moduloICBT;
        private String descripcion;
        private Boolean completada = false;

        public RecomendacionICBTBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RecomendacionICBTBuilder resultadoInferencia(ResultadoInferencia resultadoInferencia) {
            this.resultadoInferencia = resultadoInferencia;
            return this;
        }

        public RecomendacionICBTBuilder titulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public RecomendacionICBTBuilder moduloICBT(String moduloICBT) {
            this.moduloICBT = moduloICBT;
            return this;
        }

        public RecomendacionICBTBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public RecomendacionICBTBuilder completada(Boolean completada) {
            this.completada = completada;
            return this;
        }

        public RecomendacionICBT build() {
            return new RecomendacionICBT(id, resultadoInferencia, titulo, moduloICBT, descripcion, completada);
        }
    }
}
