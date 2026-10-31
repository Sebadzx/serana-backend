package com.arquiweb.grupo4.serana.dtos;

import java.util.List;

public class MLInferenceRequestDTO {

    private Integer nivelEstres;
    private Integer nivelAnsiedad;
    private Integer puntuacionGad7;
    private Integer puntuacionPss10;
    private List<Integer> ts4usRespuestas;
    private List<Integer> asaidasRespuestas;
    private Double habitoSuenoHoras;
    private Double tiempoPantallaHoras;
    private Double horasRedesSociales;
    private Double horasEstudioVirtual;
    private String dispositivoPrincipal;
    private String textoLibre;

    public MLInferenceRequestDTO() {
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

    public List<Integer> getTs4usRespuestas() {
        return ts4usRespuestas;
    }

    public void setTs4usRespuestas(List<Integer> ts4usRespuestas) {
        this.ts4usRespuestas = ts4usRespuestas;
    }

    public List<Integer> getAsaidasRespuestas() {
        return asaidasRespuestas;
    }

    public void setAsaidasRespuestas(List<Integer> asaidasRespuestas) {
        this.asaidasRespuestas = asaidasRespuestas;
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

    public Double getHorasRedesSociales() {
        return horasRedesSociales;
    }

    public void setHorasRedesSociales(Double horasRedesSociales) {
        this.horasRedesSociales = horasRedesSociales;
    }

    public Double getHorasEstudioVirtual() {
        return horasEstudioVirtual;
    }

    public void setHorasEstudioVirtual(Double horasEstudioVirtual) {
        this.horasEstudioVirtual = horasEstudioVirtual;
    }

    public String getDispositivoPrincipal() {
        return dispositivoPrincipal;
    }

    public void setDispositivoPrincipal(String dispositivoPrincipal) {
        this.dispositivoPrincipal = dispositivoPrincipal;
    }

    public String getTextoLibre() {
        return textoLibre;
    }

    public void setTextoLibre(String textoLibre) {
        this.textoLibre = textoLibre;
    }

    public static MLInferenceRequestDTOBuilder builder() {
        return new MLInferenceRequestDTOBuilder();
    }

    public static class MLInferenceRequestDTOBuilder {
        private Integer nivelEstres;
        private Integer nivelAnsiedad;
        private Integer puntuacionGad7;
        private Integer puntuacionPss10;
        private List<Integer> ts4usRespuestas;
        private List<Integer> asaidasRespuestas;
        private Double habitoSuenoHoras;
        private Double tiempoPantallaHoras;
        private Double horasRedesSociales;
        private Double horasEstudioVirtual;
        private String dispositivoPrincipal;
        private String textoLibre;

        public MLInferenceRequestDTOBuilder nivelEstres(Integer nivelEstres) {
            this.nivelEstres = nivelEstres;
            return this;
        }

        public MLInferenceRequestDTOBuilder nivelAnsiedad(Integer nivelAnsiedad) {
            this.nivelAnsiedad = nivelAnsiedad;
            return this;
        }

        public MLInferenceRequestDTOBuilder puntuacionGad7(Integer puntuacionGad7) {
            this.puntuacionGad7 = puntuacionGad7;
            return this;
        }

        public MLInferenceRequestDTOBuilder puntuacionPss10(Integer puntuacionPss10) {
            this.puntuacionPss10 = puntuacionPss10;
            return this;
        }

        public MLInferenceRequestDTOBuilder ts4usRespuestas(List<Integer> ts4usRespuestas) {
            this.ts4usRespuestas = ts4usRespuestas;
            return this;
        }

        public MLInferenceRequestDTOBuilder asaidasRespuestas(List<Integer> asaidasRespuestas) {
            this.asaidasRespuestas = asaidasRespuestas;
            return this;
        }

        public MLInferenceRequestDTOBuilder habitoSuenoHoras(Double habitoSuenoHoras) {
            this.habitoSuenoHoras = habitoSuenoHoras;
            return this;
        }

        public MLInferenceRequestDTOBuilder tiempoPantallaHoras(Double tiempoPantallaHoras) {
            this.tiempoPantallaHoras = tiempoPantallaHoras;
            return this;
        }

        public MLInferenceRequestDTOBuilder horasRedesSociales(Double horasRedesSociales) {
            this.horasRedesSociales = horasRedesSociales;
            return this;
        }

        public MLInferenceRequestDTOBuilder horasEstudioVirtual(Double horasEstudioVirtual) {
            this.horasEstudioVirtual = horasEstudioVirtual;
            return this;
        }

        public MLInferenceRequestDTOBuilder dispositivoPrincipal(String dispositivoPrincipal) {
            this.dispositivoPrincipal = dispositivoPrincipal;
            return this;
        }

        public MLInferenceRequestDTOBuilder textoLibre(String textoLibre) {
            this.textoLibre = textoLibre;
            return this;
        }

        public MLInferenceRequestDTO build() {
            MLInferenceRequestDTO dto = new MLInferenceRequestDTO();
            dto.setNivelEstres(nivelEstres);
            dto.setNivelAnsiedad(nivelAnsiedad);
            dto.setPuntuacionGad7(puntuacionGad7);
            dto.setPuntuacionPss10(puntuacionPss10);
            dto.setTs4usRespuestas(ts4usRespuestas);
            dto.setAsaidasRespuestas(asaidasRespuestas);
            dto.setHabitoSuenoHoras(habitoSuenoHoras);
            dto.setTiempoPantallaHoras(tiempoPantallaHoras);
            dto.setHorasRedesSociales(horasRedesSociales);
            dto.setHorasEstudioVirtual(horasEstudioVirtual);
            dto.setDispositivoPrincipal(dispositivoPrincipal);
            dto.setTextoLibre(textoLibre);
            return dto;
        }
    }
}
