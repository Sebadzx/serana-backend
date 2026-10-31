package com.arquiweb.grupo4.serana.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones_psicometricas")
public class EvaluacionPsicometrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;

    @Column(name = "nivel_estres")
    private Integer nivelEstres;

    @Column(name = "nivel_ansiedad")
    private Integer nivelAnsiedad;

    @Column(name = "puntuacion_gad7")
    private Integer puntuacionGad7;

    @Column(name = "puntuacion_pss10")
    private Integer puntuacionPss10;

    @Column(name = "ts4us_respuestas_json", columnDefinition = "TEXT")
    private String ts4usRespuestasJson;

    @Column(name = "asaidas_respuestas_json", columnDefinition = "TEXT")
    private String asaidasRespuestasJson;

    @Column(name = "habito_sueno_horas")
    private Double habitoSuenoHoras;

    @Column(name = "tiempo_pantalla_horas")
    private Double tiempoPantallaHoras;

    @Column(name = "horas_redes_sociales")
    private Double horasRedesSociales;

    @Column(name = "horas_estudio_virtual")
    private Double horasEstudioVirtual;

    @Column(name = "dispositivo_principal")
    private String dispositivoPrincipal;

    public EvaluacionPsicometrica() {
    }

    public EvaluacionPsicometrica(Long id, Usuario usuario, LocalDateTime fechaEvaluacion, Integer nivelEstres, Integer nivelAnsiedad, Integer puntuacionGad7, Integer puntuacionPss10, Double habitoSuenoHoras, Double tiempoPantallaHoras) {
        this.id = id;
        this.usuario = usuario;
        this.fechaEvaluacion = fechaEvaluacion;
        this.nivelEstres = nivelEstres;
        this.nivelAnsiedad = nivelAnsiedad;
        this.puntuacionGad7 = puntuacionGad7;
        this.puntuacionPss10 = puntuacionPss10;
        this.habitoSuenoHoras = habitoSuenoHoras;
        this.tiempoPantallaHoras = tiempoPantallaHoras;
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

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public Integer getNivelEstres() {
        return nivelEstres;
    }

    public void setNivelEstres(Integer nivelEstres) {
        this.nivelEstres = nivelEstres;
    }

    public Integer getNivelAnsiedad() {
        return nivelAnsiedad;
    }

    public void setNivelAnsiedad(Integer nivelAnsiedad) {
        this.nivelAnsiedad = nivelAnsiedad;
    }

    public Integer getPuntuacionGad7() {
        return puntuacionGad7;
    }

    public void setPuntuacionGad7(Integer puntuacionGad7) {
        this.puntuacionGad7 = puntuacionGad7;
    }

    public Integer getPuntuacionPss10() {
        return puntuacionPss10;
    }

    public void setPuntuacionPss10(Integer puntuacionPss10) {
        this.puntuacionPss10 = puntuacionPss10;
    }

    public String getTs4usRespuestasJson() {
        return ts4usRespuestasJson;
    }

    public void setTs4usRespuestasJson(String ts4usRespuestasJson) {
        this.ts4usRespuestasJson = ts4usRespuestasJson;
    }

    public String getAsaidasRespuestasJson() {
        return asaidasRespuestasJson;
    }

    public void setAsaidasRespuestasJson(String asaidasRespuestasJson) {
        this.asaidasRespuestasJson = asaidasRespuestasJson;
    }

    public Double getHabitoSuenoHoras() {
        return habitoSuenoHoras;
    }

    public void setHabitoSuenoHoras(Double habitoSuenoHoras) {
        this.habitoSuenoHoras = habitoSuenoHoras;
    }

    public Double getTiempoPantallaHoras() {
        return tiempoPantallaHoras;
    }

    public void setTiempoPantallaHoras(Double tiempoPantallaHoras) {
        this.tiempoPantallaHoras = tiempoPantallaHoras;
    }

    public Double getHorasRedesSociales() {
        return horasRedesSociales;
    }

    public void setHorasRedesSociales(Double horasRedesSociales) {
        this.horasRedesSociales = horasRedesSociales;
    }

    public Double getHorasEstudioVirtual() {
        return horasEstudioVirtual;
    }

    public void setHorasEstudioVirtual(Double horasEstudioVirtual) {
        this.horasEstudioVirtual = horasEstudioVirtual;
    }

    public String getDispositivoPrincipal() {
        return dispositivoPrincipal;
    }

    public void setDispositivoPrincipal(String dispositivoPrincipal) {
        this.dispositivoPrincipal = dispositivoPrincipal;
    }

    public static EvaluacionPsicometricaBuilder builder() {
        return new EvaluacionPsicometricaBuilder();
    }

    public static class EvaluacionPsicometricaBuilder {
        private Long id;
        private Usuario usuario;
        private LocalDateTime fechaEvaluacion;
        private Integer nivelEstres;
        private Integer nivelAnsiedad;
        private Integer puntuacionGad7;
        private Integer puntuacionPss10;
        private String ts4usRespuestasJson;
        private String asaidasRespuestasJson;
        private Double habitoSuenoHoras;
        private Double tiempoPantallaHoras;
        private Double horasRedesSociales;
        private Double horasEstudioVirtual;
        private String dispositivoPrincipal;

        public EvaluacionPsicometricaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EvaluacionPsicometricaBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public EvaluacionPsicometricaBuilder fechaEvaluacion(LocalDateTime fechaEvaluacion) {
            this.fechaEvaluacion = fechaEvaluacion;
            return this;
        }

        public EvaluacionPsicometricaBuilder nivelEstres(Integer nivelEstres) {
            this.nivelEstres = nivelEstres;
            return this;
        }

        public EvaluacionPsicometricaBuilder nivelAnsiedad(Integer nivelAnsiedad) {
            this.nivelAnsiedad = nivelAnsiedad;
            return this;
        }

        public EvaluacionPsicometricaBuilder puntuacionGad7(Integer puntuacionGad7) {
            this.puntuacionGad7 = puntuacionGad7;
            return this;
        }

        public EvaluacionPsicometricaBuilder puntuacionPss10(Integer puntuacionPss10) {
            this.puntuacionPss10 = puntuacionPss10;
            return this;
        }

        public EvaluacionPsicometricaBuilder ts4usRespuestasJson(String ts4usRespuestasJson) {
            this.ts4usRespuestasJson = ts4usRespuestasJson;
            return this;
        }

        public EvaluacionPsicometricaBuilder asaidasRespuestasJson(String asaidasRespuestasJson) {
            this.asaidasRespuestasJson = asaidasRespuestasJson;
            return this;
        }

        public EvaluacionPsicometricaBuilder habitoSuenoHoras(Double habitoSuenoHoras) {
            this.habitoSuenoHoras = habitoSuenoHoras;
            return this;
        }

        public EvaluacionPsicometricaBuilder tiempoPantallaHoras(Double tiempoPantallaHoras) {
            this.tiempoPantallaHoras = tiempoPantallaHoras;
            return this;
        }

        public EvaluacionPsicometricaBuilder horasRedesSociales(Double horasRedesSociales) {
            this.horasRedesSociales = horasRedesSociales;
            return this;
        }

        public EvaluacionPsicometricaBuilder horasEstudioVirtual(Double horasEstudioVirtual) {
            this.horasEstudioVirtual = horasEstudioVirtual;
            return this;
        }

        public EvaluacionPsicometricaBuilder dispositivoPrincipal(String dispositivoPrincipal) {
            this.dispositivoPrincipal = dispositivoPrincipal;
            return this;
        }

        public EvaluacionPsicometrica build() {
            EvaluacionPsicometrica e = new EvaluacionPsicometrica(id, usuario, fechaEvaluacion, nivelEstres, nivelAnsiedad, puntuacionGad7, puntuacionPss10, habitoSuenoHoras, tiempoPantallaHoras);
            e.setTs4usRespuestasJson(ts4usRespuestasJson);
            e.setAsaidasRespuestasJson(asaidasRespuestasJson);
            e.setHorasRedesSociales(horasRedesSociales);
            e.setHorasEstudioVirtual(horasEstudioVirtual);
            e.setDispositivoPrincipal(dispositivoPrincipal);
            return e;
        }
    }
}
