package com.upc.serana.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "textos_libres_emocionales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextoLibreEmocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private EvaluacionPsicometrica evaluacion;

    @Column(nullable = false, length = 2500)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
}
