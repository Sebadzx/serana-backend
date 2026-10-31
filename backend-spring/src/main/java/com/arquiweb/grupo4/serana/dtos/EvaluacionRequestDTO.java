package com.arquiweb.grupo4.serana.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EvaluacionRequestDTO {

    @NotNull
    @Min(0) @Max(10)
    private Integer nivelEstres;

    @NotNull
    @Min(0) @Max(10)
    private Integer nivelAnsiedad;

    @NotNull
    @Min(0) @Max(21)
    private Integer puntuacionGad7;

    @NotNull
    @Min(0) @Max(40)
    private Integer puntuacionPss10;

    @NotNull
    @Min(0) @Max(24)
    private Double habitoSuenoHoras;

    @NotNull
    @Min(0) @Max(24)
    private Double tiempoPantallaHoras;

    private String textoLibre;

    public EvaluacionRequestDTO() {
    }

    public EvaluacionRequestDTO(Integer nivelEstres, Integer nivelAnsiedad, Integer puntuacionGad7, Integer puntuacionPss10, Double habitoSuenoHoras, Double tiempoPantallaHoras, String textoLibre) {
        this.nivelEstres = nivelEstres;
        this.nivelAnsiedad = nivelAnsiedad;
        this.puntuacionGad7 = puntuacionGad7;
        this.puntuacionPss10 = puntuacionPss10;
        this.habitoSuenoHoras = habitoSuenoHoras;
        this.tiempoPantallaHoras = tiempoPantallaHoras;
        this.textoLibre = textoLibre;
    }

    public Integer getNivelEstres() {
        return nivelEstres;
    }

    public void setNivelEstres(Integer nivelEstres) {
        this.nivelEstres = nivelEstres;
    }

    public Integer getNivelAnsiedad() {
        return nivelAnsiedad;
    }

    public void setNivelAnsiedad(Integer nivelAnsiedad) {
        this.nivelAnsiedad = nivelAnsiedad;
    }

    public Integer getPuntuacionGad7() {
        return puntuacionGad7;
    }

    public void setPuntuacionGad7(Integer puntuacionGad7) {
        this.puntuacionGad7 = puntuacionGad7;
    }

    public Integer getPuntuacionPss10() {
        return puntuacionPss10;
    }

    public void setPuntuacionPss10(Integer puntuacionPss10) {
        this.puntuacionPss10 = puntuacionPss10;
    }

    public Double getHabitoSuenoHoras() {
        return habitoSuenoHoras;
    }

    public void setHabitoSuenoHoras(Double habitoSuenoHoras) {
        this.habitoSuenoHoras = habitoSuenoHoras;
    }

    public Double getTiempoPantallaHoras() {
        return tiempoPantallaHoras;
    }

    public void setTiempoPantallaHoras(Double tiempoPantallaHoras) {
        this.tiempoPantallaHoras = tiempoPantallaHoras;
    }

    public String getTextoLibre() {
        return textoLibre;
    }

    public void setTextoLibre(String textoLibre) {
        this.textoLibre = textoLibre;
    }

    public static EvaluacionRequestDTOBuilder builder() {
        return new EvaluacionRequestDTOBuilder();
    }

    public static class EvaluacionRequestDTOBuilder {
        private Integer nivelEstres;
        private Integer nivelAnsiedad;
        private Integer puntuacionGad7;
        private Integer puntuacionPss10;
        private Double habitoSuenoHoras;
        private Double tiempoPantallaHoras;
        private String textoLibre;

        public EvaluacionRequestDTOBuilder nivelEstres(Integer nivelEstres) {
            this.nivelEstres = nivelEstres;
            return this;
        }

        public EvaluacionRequestDTOBuilder nivelAnsiedad(Integer nivelAnsiedad) {
            this.nivelAnsiedad = nivelAnsiedad;
            return this;
        }

        public EvaluacionRequestDTOBuilder puntuacionGad7(Integer puntuacionGad7) {
            this.puntuacionGad7 = puntuacionGad7;
            return this;
        }

        public EvaluacionRequestDTOBuilder puntuacionPss10(Integer puntuacionPss10) {
            this.puntuacionPss10 = puntuacionPss10;
            return this;
        }

        public EvaluacionRequestDTOBuilder habitoSuenoHoras(Double habitoSuenoHoras) {
            this.habitoSuenoHoras = habitoSuenoHoras;
            return this;
        }

        public EvaluacionRequestDTOBuilder tiempoPantallaHoras(Double tiempoPantallaHoras) {
            this.tiempoPantallaHoras = tiempoPantallaHoras;
            return this;
        }

        public EvaluacionRequestDTOBuilder textoLibre(String textoLibre) {
            this.textoLibre = textoLibre;
            return this;
        }

        public EvaluacionRequestDTO build() {
            return new EvaluacionRequestDTO(nivelEstres, nivelAnsiedad, puntuacionGad7, puntuacionPss10, habitoSuenoHoras, tiempoPantallaHoras, textoLibre);
        }
    }
}
