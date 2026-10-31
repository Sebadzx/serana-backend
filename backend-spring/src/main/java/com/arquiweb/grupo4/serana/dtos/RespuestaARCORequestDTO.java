package com.arquiweb.grupo4.serana.dtos;

import jakarta.validation.constraints.NotBlank;

public class RespuestaARCORequestDTO {

    @NotBlank(message = "El estado es obligatorio (ATENDIDO, RECHAZADO, EN_PROCESO)")
    private String estado;

    @NotBlank(message = "La respuesta del administrador es obligatoria")
    private String respuestaAdmin;

    public RespuestaARCORequestDTO() {
    }

    public RespuestaARCORequestDTO(String estado, String respuestaAdmin) {
        this.estado = estado;
        this.respuestaAdmin = respuestaAdmin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRespuestaAdmin() {
        return respuestaAdmin;
    }

    public void setRespuestaAdmin(String respuestaAdmin) {
        this.respuestaAdmin = respuestaAdmin;
    }

    public static RespuestaARCORequestDTOBuilder builder() {
        return new RespuestaARCORequestDTOBuilder();
    }

    public static class RespuestaARCORequestDTOBuilder {
        private String estado;
        private String respuestaAdmin;

        public RespuestaARCORequestDTOBuilder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public RespuestaARCORequestDTOBuilder respuestaAdmin(String respuestaAdmin) {
            this.respuestaAdmin = respuestaAdmin;
            return this;
        }

        public RespuestaARCORequestDTO build() {
            return new RespuestaARCORequestDTO(estado, respuestaAdmin);
        }
    }
}
