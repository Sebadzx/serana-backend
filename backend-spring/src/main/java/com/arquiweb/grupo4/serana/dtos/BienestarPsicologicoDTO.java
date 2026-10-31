package com.arquiweb.grupo4.serana.dtos;

public class BienestarPsicologicoDTO {

    private Long id;
    private String codigoUniversitario;
    private String nombreCompleto;
    private String email;
    private String especialidad;
    private String codigoColegiatura;

    public BienestarPsicologicoDTO() {
    }

    public BienestarPsicologicoDTO(Long id, String codigoUniversitario, String nombreCompleto, String email, String especialidad, String codigoColegiatura) {
        this.id = id;
        this.codigoUniversitario = codigoUniversitario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.especialidad = especialidad;
        this.codigoColegiatura = codigoColegiatura;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getCodigoColegiatura() {
        return codigoColegiatura;
    }

    public void setCodigoColegiatura(String codigoColegiatura) {
        this.codigoColegiatura = codigoColegiatura;
    }

    public static BienestarPsicologicoDTOBuilder builder() {
        return new BienestarPsicologicoDTOBuilder();
    }

    public static class BienestarPsicologicoDTOBuilder {
        private Long id;
        private String codigoUniversitario;
        private String nombreCompleto;
        private String email;
        private String especialidad;
        private String codigoColegiatura;

        public BienestarPsicologicoDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BienestarPsicologicoDTOBuilder codigoUniversitario(String codigoUniversitario) {
            this.codigoUniversitario = codigoUniversitario;
            return this;
        }

        public BienestarPsicologicoDTOBuilder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public BienestarPsicologicoDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public BienestarPsicologicoDTOBuilder especialidad(String especialidad) {
            this.especialidad = especialidad;
            return this;
        }

        public BienestarPsicologicoDTOBuilder codigoColegiatura(String codigoColegiatura) {
            this.codigoColegiatura = codigoColegiatura;
            return this;
        }

        public BienestarPsicologicoDTO build() {
            return new BienestarPsicologicoDTO(id, codigoUniversitario, nombreCompleto, email, especialidad, codigoColegiatura);
        }
    }
}
