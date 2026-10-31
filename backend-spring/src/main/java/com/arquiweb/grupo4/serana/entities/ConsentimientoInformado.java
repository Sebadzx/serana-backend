package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consentimientos_informados")
public class ConsentimientoInformado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private boolean aceptado;

    @Column(name = "fecha_aceptacion", nullable = false)
    private LocalDateTime fechaAceptacion;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "version_terminos")
    private String versionTerminos;

    public ConsentimientoInformado() {
    }

    public ConsentimientoInformado(Long id, Usuario usuario, boolean aceptado, LocalDateTime fechaAceptacion, String ipAddress, String versionTerminos) {
        this.id = id;
        this.usuario = usuario;
        this.aceptado = aceptado;
        this.fechaAceptacion = fechaAceptacion;
        this.ipAddress = ipAddress;
        this.versionTerminos = versionTerminos;
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

    public boolean isAceptado() {
        return aceptado;
    }

    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado;
    }

    public LocalDateTime getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(LocalDateTime fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
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

    public static ConsentimientoInformadoBuilder builder() {
        return new ConsentimientoInformadoBuilder();
    }

    public static class ConsentimientoInformadoBuilder {
        private Long id;
        private Usuario usuario;
        private boolean aceptado;
        private LocalDateTime fechaAceptacion;
        private String ipAddress;
        private String versionTerminos;

        public ConsentimientoInformadoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ConsentimientoInformadoBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public ConsentimientoInformadoBuilder aceptado(boolean aceptado) {
            this.aceptado = aceptado;
            return this;
        }

        public ConsentimientoInformadoBuilder fechaAceptacion(LocalDateTime fechaAceptacion) {
            this.fechaAceptacion = fechaAceptacion;
            return this;
        }

        public ConsentimientoInformadoBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public ConsentimientoInformadoBuilder versionTerminos(String versionTerminos) {
            this.versionTerminos = versionTerminos;
            return this;
        }

        public ConsentimientoInformado build() {
            return new ConsentimientoInformado(id, usuario, aceptado, fechaAceptacion, ipAddress, versionTerminos);
        }
    }
}
