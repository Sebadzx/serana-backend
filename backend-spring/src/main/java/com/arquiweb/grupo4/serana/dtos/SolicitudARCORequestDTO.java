package com.arquiweb.grupo4.serana.dtos;

import jakarta.validation.constraints.NotBlank;

public class SolicitudARCORequestDTO {

    @NotBlank(message = "El tipo de solicitud es obligatorio (ACCESO, RECTIFICACION, CANCELACION, OPOSICION)")
    private String tipoSolicitud;

    @NotBlank(message = "El detalle de la solicitud es obligatorio")
    private String detalleSolicitud;

    public SolicitudARCORequestDTO() {
    }

    public SolicitudARCORequestDTO(String tipoSolicitud, String detalleSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
        this.detalleSolicitud = detalleSolicitud;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getDetalleSolicitud() {
        return detalleSolicitud;
    }

    public void setDetalleSolicitud(String detalleSolicitud) {
        this.detalleSolicitud = detalleSolicitud;
    }

    public static SolicitudARCORequestDTOBuilder builder() {
        return new SolicitudARCORequestDTOBuilder();
    }

    public static class SolicitudARCORequestDTOBuilder {
        private String tipoSolicitud;
        private String detalleSolicitud;

        public SolicitudARCORequestDTOBuilder tipoSolicitud(String tipoSolicitud) {
            this.tipoSolicitud = tipoSolicitud;
            return this;
        }

        public SolicitudARCORequestDTOBuilder detalleSolicitud(String detalleSolicitud) {
            this.detalleSolicitud = detalleSolicitud;
            return this;
        }

        public SolicitudARCORequestDTO build() {
            return new SolicitudARCORequestDTO(tipoSolicitud, detalleSolicitud);
        }
    }
}
