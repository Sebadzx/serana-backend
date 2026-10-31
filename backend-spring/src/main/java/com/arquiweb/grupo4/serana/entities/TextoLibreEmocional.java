package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "textos_libres_emocionales")
public class TextoLibreEmocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private EvaluacionPsicometrica evaluacion;

    @Column(name = "texto_libre", columnDefinition = "TEXT")
    private String textoLibre;

    @Column(name = "estresores_detectados")
    private String estresoresDetectados;

    @Column(name = "polaridad_sentimiento")
    private Double polaridadSentimiento;

    public TextoLibreEmocional() {
    }

    public TextoLibreEmocional(Long id, EvaluacionPsicometrica evaluacion, String textoLibre, String estresoresDetectados, Double polaridadSentimiento) {
        this.id = id;
        this.evaluacion = evaluacion;
        this.textoLibre = textoLibre;
        this.estresoresDetectados = estresoresDetectados;
        this.polaridadSentimiento = polaridadSentimiento;
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

    public String getTextoLibre() {
        return textoLibre;
    }

    public void setTextoLibre(String textoLibre) {
        this.textoLibre = textoLibre;
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

    public static TextoLibreEmocionalBuilder builder() {
        return new TextoLibreEmocionalBuilder();
    }

    public static class TextoLibreEmocionalBuilder {
        private Long id;
        private EvaluacionPsicometrica evaluacion;
        private String textoLibre;
        private String estresoresDetectados;
        private Double polaridadSentimiento;

        public TextoLibreEmocionalBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TextoLibreEmocionalBuilder evaluacion(EvaluacionPsicometrica evaluacion) {
            this.evaluacion = evaluacion;
            return this;
        }

        public TextoLibreEmocionalBuilder textoLibre(String textoLibre) {
            this.textoLibre = textoLibre;
            return this;
        }

        public TextoLibreEmocionalBuilder estresoresDetectados(String estresoresDetectados) {
            this.estresoresDetectados = estresoresDetectados;
            return this;
        }

        public TextoLibreEmocionalBuilder polaridadSentimiento(Double polaridadSentimiento) {
            this.polaridadSentimiento = polaridadSentimiento;
            return this;
        }

        public TextoLibreEmocional build() {
            return new TextoLibreEmocional(id, evaluacion, textoLibre, estresoresDetectados, polaridadSentimiento);
        }
    }
}
