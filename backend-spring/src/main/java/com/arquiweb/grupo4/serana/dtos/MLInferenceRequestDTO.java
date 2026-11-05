package com.arquiweb.grupo4.serana.dtos;

import java.util.List;
import java.util.Map;

public class MLInferenceRequestDTO {

    private List<Integer> tmdRespuestas;
    private List<Integer> staiRasgoRespuestas;
    private Integer tipoDispositivo;
    private Integer tiempoUsoDiario;
    private Integer lugarUsoFrecuente;
    private Integer tipoAccesoRed;
    private Integer reglasHogarUso;
    private Integer percepcionDiscriminacion;
    private Integer discusionesUsoCelular;
    private Integer appMensajeria;
    private Integer appVideojuegos;
    private Integer appRedesSociales;
    private Integer appMusica;
    private Integer appVideos;
    private Integer appGestionTiempo;
    private Integer appCamara;
    private Integer appCorreo;
    private String textoLibre;

    private Map<String, Integer> habitosSocioDemo;

    public Map<String, Integer> getHabitosSocioDemo() {
        return habitosSocioDemo;
    }

    public void setHabitosSocioDemo(Map<String, Integer> habitosSocioDemo) {
        this.habitosSocioDemo = habitosSocioDemo;
    }

    public MLInferenceRequestDTO() {
    }

    public List<Integer> getTmdRespuestas() {
        return tmdRespuestas;
    }

    public void setTmdRespuestas(List<Integer> tmdRespuestas) {
        this.tmdRespuestas = tmdRespuestas;
    }

    public List<Integer> getStaiRasgoRespuestas() {
        return staiRasgoRespuestas;
    }

    public void setStaiRasgoRespuestas(List<Integer> staiRasgoRespuestas) {
        this.staiRasgoRespuestas = staiRasgoRespuestas;
    }

    public Integer getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(Integer tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public Integer getTiempoUsoDiario() {
        return tiempoUsoDiario;
    }

    public void setTiempoUsoDiario(Integer tiempoUsoDiario) {
        this.tiempoUsoDiario = tiempoUsoDiario;
    }

    public Integer getLugarUsoFrecuente() {
        return lugarUsoFrecuente;
    }

    public void setLugarUsoFrecuente(Integer lugarUsoFrecuente) {
        this.lugarUsoFrecuente = lugarUsoFrecuente;
    }

    public Integer getTipoAccesoRed() {
        return tipoAccesoRed;
    }

    public void setTipoAccesoRed(Integer tipoAccesoRed) {
        this.tipoAccesoRed = tipoAccesoRed;
    }

    public Integer getReglasHogarUso() {
        return reglasHogarUso;
    }

    public void setReglasHogarUso(Integer reglasHogarUso) {
        this.reglasHogarUso = reglasHogarUso;
    }

    public Integer getPercepcionDiscriminacion() {
        return percepcionDiscriminacion;
    }

    public void setPercepcionDiscriminacion(Integer percepcionDiscriminacion) {
        this.percepcionDiscriminacion = percepcionDiscriminacion;
    }

    public Integer getDiscusionesUsoCelular() {
        return discusionesUsoCelular;
    }

    public void setDiscusionesUsoCelular(Integer discusionesUsoCelular) {
        this.discusionesUsoCelular = discusionesUsoCelular;
    }

    public Integer getAppMensajeria() {
        return appMensajeria;
    }

    public void setAppMensajeria(Integer appMensajeria) {
        this.appMensajeria = appMensajeria;
    }

    public Integer getAppVideojuegos() {
        return appVideojuegos;
    }

    public void setAppVideojuegos(Integer appVideojuegos) {
        this.appVideojuegos = appVideojuegos;
    }

    public Integer getAppRedesSociales() {
        return appRedesSociales;
    }

    public void setAppRedesSociales(Integer appRedesSociales) {
        this.appRedesSociales = appRedesSociales;
    }

    public Integer getAppMusica() {
        return appMusica;
    }

    public void setAppMusica(Integer appMusica) {
        this.appMusica = appMusica;
    }

    public Integer getAppVideos() {
        return appVideos;
    }

    public void setAppVideos(Integer appVideos) {
        this.appVideos = appVideos;
    }

    public Integer getAppGestionTiempo() {
        return appGestionTiempo;
    }

    public void setAppGestionTiempo(Integer appGestionTiempo) {
        this.appGestionTiempo = appGestionTiempo;
    }

    public Integer getAppCamara() {
        return appCamara;
    }

    public void setAppCamara(Integer appCamara) {
        this.appCamara = appCamara;
    }

    public Integer getAppCorreo() {
        return appCorreo;
    }

    public void setAppCorreo(Integer appCorreo) {
        this.appCorreo = appCorreo;
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
        private List<Integer> tmdRespuestas;
        private List<Integer> staiRasgoRespuestas;
        private Integer tipoDispositivo = 2;
        private Integer tiempoUsoDiario = 3;
        private Integer lugarUsoFrecuente = 2;
        private Integer tipoAccesoRed = 3;
        private Integer reglasHogarUso = 2;
        private Integer percepcionDiscriminacion = 1;
        private Integer discusionesUsoCelular = 2;
        private Integer appMensajeria = 2;
        private Integer appVideojuegos = 1;
        private Integer appRedesSociales = 2;
        private Integer appMusica = 2;
        private Integer appVideos = 2;
        private Integer appGestionTiempo = 2;
        private Integer appCamara = 2;
        private Integer appCorreo = 2;
        private String textoLibre;

        public MLInferenceRequestDTOBuilder tmdRespuestas(List<Integer> tmdRespuestas) {
            this.tmdRespuestas = tmdRespuestas;
            return this;
        }

        public MLInferenceRequestDTOBuilder staiRasgoRespuestas(List<Integer> staiRasgoRespuestas) {
            this.staiRasgoRespuestas = staiRasgoRespuestas;
            return this;
        }

        public MLInferenceRequestDTOBuilder tipoDispositivo(Integer tipoDispositivo) {
            this.tipoDispositivo = tipoDispositivo;
            return this;
        }

        public MLInferenceRequestDTOBuilder tiempoUsoDiario(Integer tiempoUsoDiario) {
            this.tiempoUsoDiario = tiempoUsoDiario;
            return this;
        }

        public MLInferenceRequestDTOBuilder lugarUsoFrecuente(Integer lugarUsoFrecuente) {
            this.lugarUsoFrecuente = lugarUsoFrecuente;
            return this;
        }

        public MLInferenceRequestDTOBuilder tipoAccesoRed(Integer tipoAccesoRed) {
            this.tipoAccesoRed = tipoAccesoRed;
            return this;
        }

        public MLInferenceRequestDTOBuilder reglasHogarUso(Integer reglasHogarUso) {
            this.reglasHogarUso = reglasHogarUso;
            return this;
        }

        public MLInferenceRequestDTOBuilder percepcionDiscriminacion(Integer percepcionDiscriminacion) {
            this.percepcionDiscriminacion = percepcionDiscriminacion;
            return this;
        }

        public MLInferenceRequestDTOBuilder discusionesUsoCelular(Integer discusionesUsoCelular) {
            this.discusionesUsoCelular = discusionesUsoCelular;
            return this;
        }

        public MLInferenceRequestDTOBuilder appMensajeria(Integer appMensajeria) {
            this.appMensajeria = appMensajeria;
            return this;
        }

        public MLInferenceRequestDTOBuilder appVideojuegos(Integer appVideojuegos) {
            this.appVideojuegos = appVideojuegos;
            return this;
        }

        public MLInferenceRequestDTOBuilder appRedesSociales(Integer appRedesSociales) {
            this.appRedesSociales = appRedesSociales;
            return this;
        }

        public MLInferenceRequestDTOBuilder appMusica(Integer appMusica) {
            this.appMusica = appMusica;
            return this;
        }

        public MLInferenceRequestDTOBuilder appVideos(Integer appVideos) {
            this.appVideos = appVideos;
            return this;
        }

        public MLInferenceRequestDTOBuilder appGestionTiempo(Integer appGestionTiempo) {
            this.appGestionTiempo = appGestionTiempo;
            return this;
        }

        public MLInferenceRequestDTOBuilder appCamara(Integer appCamara) {
            this.appCamara = appCamara;
            return this;
        }

        public MLInferenceRequestDTOBuilder appCorreo(Integer appCorreo) {
            this.appCorreo = appCorreo;
            return this;
        }

        public MLInferenceRequestDTOBuilder textoLibre(String textoLibre) {
            this.textoLibre = textoLibre;
            return this;
        }

        public MLInferenceRequestDTO build() {
            MLInferenceRequestDTO dto = new MLInferenceRequestDTO();
            dto.setTmdRespuestas(tmdRespuestas);
            dto.setStaiRasgoRespuestas(staiRasgoRespuestas);
            dto.setTipoDispositivo(tipoDispositivo);
            dto.setTiempoUsoDiario(tiempoUsoDiario);
            dto.setLugarUsoFrecuente(lugarUsoFrecuente);
            dto.setTipoAccesoRed(tipoAccesoRed);
            dto.setReglasHogarUso(reglasHogarUso);
            dto.setPercepcionDiscriminacion(percepcionDiscriminacion);
            dto.setDiscusionesUsoCelular(discusionesUsoCelular);
            dto.setAppMensajeria(appMensajeria);
            dto.setAppVideojuegos(appVideojuegos);
            dto.setAppRedesSociales(appRedesSociales);
            dto.setAppMusica(appMusica);
            dto.setAppVideos(appVideos);
            dto.setAppGestionTiempo(appGestionTiempo);
            dto.setAppCamara(appCamara);
            dto.setAppCorreo(appCorreo);
            dto.setTextoLibre(textoLibre);
            return dto;
        }
    }
}
