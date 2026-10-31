package com.arquiweb.grupo4.serana.dtos;

public class EstudianteDTO {

    private Long id;
    private String codigoUniversitario;
    private String nombreCompleto;
    private String email;
    private String carreraUniversitaria;
    private Integer cicloAcademico;

    public EstudianteDTO() {
    }

    public EstudianteDTO(Long id, String codigoUniversitario, String nombreCompleto, String email, String carreraUniversitaria, Integer cicloAcademico) {
        this.id = id;
        this.codigoUniversitario = codigoUniversitario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.carreraUniversitaria = carreraUniversitaria;
        this.cicloAcademico = cicloAcademico;
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

    public String getCarreraUniversitaria() {
        return carreraUniversitaria;
    }

    public void setCarreraUniversitaria(String carreraUniversitaria) {
        this.carreraUniversitaria = carreraUniversitaria;
    }

    public Integer getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(Integer cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public static EstudianteDTOBuilder builder() {
        return new EstudianteDTOBuilder();
    }

    public static class EstudianteDTOBuilder {
        private Long id;
        private String codigoUniversitario;
        private String nombreCompleto;
        private String email;
        private String carreraUniversitaria;
        private Integer cicloAcademico;

        public EstudianteDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EstudianteDTOBuilder codigoUniversitario(String codigoUniversitario) {
            this.codigoUniversitario = codigoUniversitario;
            return this;
        }

        public EstudianteDTOBuilder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public EstudianteDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public EstudianteDTOBuilder carreraUniversitaria(String carreraUniversitaria) {
            this.carreraUniversitaria = carreraUniversitaria;
            return this;
        }

        public EstudianteDTOBuilder cicloAcademico(Integer cicloAcademico) {
            this.cicloAcademico = cicloAcademico;
            return this;
        }

        public EstudianteDTO build() {
            return new EstudianteDTO(id, codigoUniversitario, nombreCompleto, email, carreraUniversitaria, cicloAcademico);
        }
    }
}
