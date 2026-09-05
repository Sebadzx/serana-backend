package com.upc.serana.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones_psicometricas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionPsicometrica {

    public enum EstadoEvaluacion {
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA_ARCO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true) // Nullable si se ejerce derecho ARCO de disociacion
    private Usuario usuario;

    // Almacenamiento serializado de respuestas para compatibilidad y portabilidad
    // Formato CSV ej: "3,4,5,2,4,5,3,4,5,4,3,5,4"
    @Column(name = "ts4us_respuestas", length = 100)
    private String ts4usRespuestas;

    // Formato CSV ej: "4,5,3,4,4,5,3,4"
    @Column(name = "asaidas_respuestas", length = 60)
    private String asaidasRespuestas;

    @Column(name = "horas_pantalla_dia")
    private Double horasPantallaDia;

    @Column(name = "horas_redes_sociales")
    private Double horasRedesSociales;

    @Column(name = "horas_estudio_virtual")
    private Double horasEstudioVirtual;

    @Column(name = "dispositivo_principal", length = 30)
    private String dispositivoPrincipal; // LAPTOP, SMARTPHONE, DESKTOP, TABLET

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoEvaluacion estado = EstadoEvaluacion.EN_PROGRESO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinalizacion;

    @OneToOne(mappedBy = "evaluacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TextoLibreEmocional textoLibre;

    @OneToOne(mappedBy = "evaluacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ResultadoInferencia resultadoInferencia;

    @PrePersist
    protected void onCreate() {
        this.fechaInicio = LocalDateTime.now();
    }
}
