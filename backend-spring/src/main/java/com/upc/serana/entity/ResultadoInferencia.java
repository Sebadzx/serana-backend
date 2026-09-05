package com.upc.serana.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resultados_inferencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoInferencia {

    public enum NivelAnsiedad {
        BAJO,
        MODERADO,
        ALTO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private EvaluacionPsicometrica evaluacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAnsiedad nivelAnsiedad;

    @Column(nullable = false)
    private Double probabilidadRiesgo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean alertaCritica = false;

    @Column(length = 120)
    private String reglaClinicaAplicada;

    @Column
    private Double polaridadSentimiento;

    // Issue 13: Cambiado de length=500 a TEXT para evitar truncamiento silencioso
    // en evaluaciones con muchos términos de estrés detectados en el texto libre.
    @Column(columnDefinition = "TEXT")
    private String terminosDetectados;

    @Column(length = 2000)
    private String topFactoresJson;

    @Column(nullable = false)
    private LocalDateTime fechaInferencia;

    @OneToMany(mappedBy = "resultadoInferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecomendacionICBT> recomendaciones = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaInferencia = LocalDateTime.now();
    }
}
