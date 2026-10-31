package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_arco")
public class SolicitudARCO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_solicitud", nullable = false)
    private String tipoSolicitud;

    @Column(nullable = false)
    private String estado;

    @Column(name = "detalle_solicitud", columnDefinition = "TEXT")
    private String detalleSolicitud;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "respuesta_admin", columnDefinition = "TEXT")
    private String respuestaAdmin;

    public SolicitudARCO() {
    }

    public SolicitudARCO(Long id, Usuario usuario, String tipoSolicitud, String estado, String detalleSolicitud, LocalDateTime fechaSolicitud, LocalDateTime fechaRespuesta, String respuestaAdmin) {
        this.id = id;
        this.usuario = usuario;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public static SolicitudARCOBuilder builder() {
        return new SolicitudARCOBuilder();
    }

    public static class SolicitudARCOBuilder {
        private Long id;
        private Usuario usuario;
        private String tipoSolicitud;
        private String estado;
        private String detalleSolicitud;
        private LocalDateTime fechaSolicitud;
        private LocalDateTime fechaRespuesta;
        private String respuestaAdmin;

        public SolicitudARCOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SolicitudARCOBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public SolicitudARCOBuilder tipoSolicitud(String tipoSolicitud) {
            this.tipoSolicitud = tipoSolicitud;
            return this;
        }

        public SolicitudARCOBuilder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public SolicitudARCOBuilder detalleSolicitud(String detalleSolicitud) {
            this.detalleSolicitud = detalleSolicitud;
            return this;
        }

        public SolicitudARCOBuilder fechaSolicitud(LocalDateTime fechaSolicitud) {
            this.fechaSolicitud = fechaSolicitud;
            return this;
        }

        public SolicitudARCOBuilder fechaRespuesta(LocalDateTime fechaRespuesta) {
            this.fechaRespuesta = fechaRespuesta;
            return this;
        }

        public SolicitudARCOBuilder respuestaAdmin(String respuestaAdmin) {
            this.respuestaAdmin = respuestaAdmin;
            return this;
        }

        public SolicitudARCO build() {
            return new SolicitudARCO(id, usuario, tipoSolicitud, estado, detalleSolicitud, fechaSolicitud, fechaRespuesta, respuestaAdmin);
        }
    }
}
