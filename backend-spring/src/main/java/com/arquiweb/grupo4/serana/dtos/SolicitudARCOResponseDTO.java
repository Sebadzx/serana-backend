package com.arquiweb.grupo4.serana.dtos;

import java.time.LocalDateTime;

public class SolicitudARCOResponseDTO {

    private Long id;
    private String codigoUsuario;
    private String nombreUsuario;
    private String tipoSolicitud;
    private String estado;
    private String detalleSolicitud;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;
    private String respuestaAdmin;

    public SolicitudARCOResponseDTO() {
    }

    public SolicitudARCOResponseDTO(Long id, String codigoUsuario, String nombreUsuario, String tipoSolicitud, String estado, String detalleSolicitud, LocalDateTime fechaSolicitud, LocalDateTime fechaRespuesta, String respuestaAdmin) {
        this.id = id;
        this.codigoUsuario = codigoUsuario;
        this.nombreUsuario = nombreUsuario;
        this.tipoSolicitud = tipoSolicitud;
        this.estado = estado;
        this.detalleSolicitud = detalleSolicitud;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaRespuesta = fechaRespuesta;
        this.respuestaAdmin = respuestaAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalleSolicitud() {
        return detalleSolicitud;
    }

    public void setDetalleSolicitud(String detalleSolicitud) {
        this.detalleSolicitud = detalleSolicitud;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public String getRespuestaAdmin() {
        return respuestaAdmin;
    }

    public void setRespuestaAdmin(String respuestaAdmin) {
        this.respuestaAdmin = respuestaAdmin;
    }

    public static SolicitudARCOResponseDTOBuilder builder() {
        return new SolicitudARCOResponseDTOBuilder();
    }

    public static class SolicitudARCOResponseDTOBuilder {
        private Long id;
        private String codigoUsuario;
        private String nombreUsuario;
        private String tipoSolicitud;
        private String estado;
        private String detalleSolicitud;
        private LocalDateTime fechaSolicitud;
        private LocalDateTime fechaRespuesta;
        private String respuestaAdmin;

        public SolicitudARCOResponseDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder codigoUsuario(String codigoUsuario) {
            this.codigoUsuario = codigoUsuario;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder nombreUsuario(String nombreUsuario) {
            this.nombreUsuario = nombreUsuario;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder tipoSolicitud(String tipoSolicitud) {
            this.tipoSolicitud = tipoSolicitud;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder detalleSolicitud(String detalleSolicitud) {
            this.detalleSolicitud = detalleSolicitud;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder fechaSolicitud(LocalDateTime fechaSolicitud) {
            this.fechaSolicitud = fechaSolicitud;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder fechaRespuesta(LocalDateTime fechaRespuesta) {
            this.fechaRespuesta = fechaRespuesta;
            return this;
        }

        public SolicitudARCOResponseDTOBuilder respuestaAdmin(String respuestaAdmin) {
            this.respuestaAdmin = respuestaAdmin;
            return this;
        }

        public SolicitudARCOResponseDTO build() {
            return new SolicitudARCOResponseDTO(id, codigoUsuario, nombreUsuario, tipoSolicitud, estado, detalleSolicitud, fechaSolicitud, fechaRespuesta, respuestaAdmin);
        }
    }
}
