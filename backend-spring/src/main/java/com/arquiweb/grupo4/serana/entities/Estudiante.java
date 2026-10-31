package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "carrera_universitaria")
    private String carreraUniversitaria;

    @Column(name = "ciclo_academico")
    private Integer cicloAcademico;

    public Estudiante() {
    }

    public Estudiante(Long id, Usuario usuario, String carreraUniversitaria, Integer cicloAcademico) {
        this.id = id;
        this.usuario = usuario;
        this.carreraUniversitaria = carreraUniversitaria;
        this.cicloAcademico = cicloAcademico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public static EstudianteBuilder builder() {
        return new EstudianteBuilder();
    }

    public static class EstudianteBuilder {
        private Long id;
        private Usuario usuario;
        private String carreraUniversitaria;
        private Integer cicloAcademico;

        public EstudianteBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EstudianteBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public EstudianteBuilder carreraUniversitaria(String carreraUniversitaria) {
            this.carreraUniversitaria = carreraUniversitaria;
            return this;
        }

        public EstudianteBuilder cicloAcademico(Integer cicloAcademico) {
            this.cicloAcademico = cicloAcademico;
            return this;
        }

        public Estudiante build() {
            return new Estudiante(id, usuario, carreraUniversitaria, cicloAcademico);
        }
    }
}
