package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "bienestar_psicologico")
public class BienestarPsicologico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "codigo_colegiatura")
    private String codigoColegiatura;

    public BienestarPsicologico() {
    }

    public BienestarPsicologico(Long id, Usuario usuario, String especialidad, String codigoColegiatura) {
        this.id = id;
        this.usuario = usuario;
        this.especialidad = especialidad;
        this.codigoColegiatura = codigoColegiatura;
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

    public static BienestarPsicologicoBuilder builder() {
        return new BienestarPsicologicoBuilder();
    }

    public static class BienestarPsicologicoBuilder {
        private Long id;
        private Usuario usuario;
        private String especialidad;
        private String codigoColegiatura;

        public BienestarPsicologicoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BienestarPsicologicoBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public BienestarPsicologicoBuilder especialidad(String especialidad) {
            this.especialidad = especialidad;
            return this;
        }

        public BienestarPsicologicoBuilder codigoColegiatura(String codigoColegiatura) {
            this.codigoColegiatura = codigoColegiatura;
            return this;
        }

        public BienestarPsicologico build() {
            return new BienestarPsicologico(id, usuario, especialidad, codigoColegiatura);
        }
    }
}
