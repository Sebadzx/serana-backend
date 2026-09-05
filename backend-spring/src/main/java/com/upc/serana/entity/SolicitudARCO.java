package com.upc.serana.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_arco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudARCO {

    public enum TipoDerecho {
        ACCESO,
        RECTIFICACION,
        CANCELACION,
        OPOSICION
    }

    public enum EstadoSolicitud {
        RECIBIDA,
        EN_PROCESO,
        ATENDIDA,
        RECHAZADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TipoDerecho tipoDerecho;

    @Column(length = 1000)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoSolicitud estado = EstadoSolicitud.RECIBIDA;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaResolucion;

    @Column(length = 1000)
    private String respuestaDetalle;

    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
    }
}
