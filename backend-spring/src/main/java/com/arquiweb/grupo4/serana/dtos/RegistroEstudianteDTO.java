package com.arquiweb.grupo4.serana.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistroEstudianteDTO {

    @NotBlank(message = "El codigo universitario es obligatorio")
    private String codigoUniversitario;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El email institucional es obligatorio")
    @Email(message = "Formato de email invalido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "Debe aceptar el consentimiento informado")
    private Boolean aceptoConsentimiento;

    private String ipAddress;
    private String versionTerminos;

    public RegistroEstudianteDTO() {
    }

    public RegistroEstudianteDTO(String codigoUniversitario, String nombreCompleto, String email, String password, Boolean aceptoConsentimiento, String ipAddress, String versionTerminos) {
        this.codigoUniversitario = codigoUniversitario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.aceptoConsentimiento = aceptoConsentimiento;
        this.ipAddress = ipAddress;
        this.versionTerminos = versionTerminos;
    }

    public String getCodigoUniversitario() {
        return codigoUniversitario;
    }

    public void setCodigoUniversitario(String codigoUniversitario) {
        this.codigoUniversitario = codigoUniversitario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAceptoConsentimiento() {
        return aceptoConsentimiento;
    }

    public void setAceptoConsentimiento(Boolean aceptoConsentimiento) {
        this.aceptoConsentimiento = aceptoConsentimiento;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getVersionTerminos() {
        return versionTerminos;
    }

    public void setVersionTerminos(String versionTerminos) {
        this.versionTerminos = versionTerminos;
    }

    public static RegistroEstudianteDTOBuilder builder() {
        return new RegistroEstudianteDTOBuilder();
    }

    public static class RegistroEstudianteDTOBuilder {
        private String codigoUniversitario;
        private String nombreCompleto;
        private String email;
        private String password;
        private Boolean aceptoConsentimiento;
        private String ipAddress;
        private String versionTerminos;

        public RegistroEstudianteDTOBuilder codigoUniversitario(String codigoUniversitario) {
            this.codigoUniversitario = codigoUniversitario;
            return this;
        }

        public RegistroEstudianteDTOBuilder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public RegistroEstudianteDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RegistroEstudianteDTOBuilder password(String password) {
            this.password = password;
            return this;
        }

        public RegistroEstudianteDTOBuilder aceptoConsentimiento(Boolean aceptoConsentimiento) {
            this.aceptoConsentimiento = aceptoConsentimiento;
            return this;
        }

        public RegistroEstudianteDTOBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public RegistroEstudianteDTOBuilder versionTerminos(String versionTerminos) {
            this.versionTerminos = versionTerminos;
            return this;
        }

        public RegistroEstudianteDTO build() {
            return new RegistroEstudianteDTO(codigoUniversitario, nombreCompleto, email, password, aceptoConsentimiento, ipAddress, versionTerminos);
        }
    }
}
