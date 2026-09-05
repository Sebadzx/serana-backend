package com.upc.serana.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recomendaciones_icbt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecomendacionICBT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultado_id", nullable = false)
    private ResultadoInferencia resultadoInferencia;

    @Column(nullable = false, length = 1000)
    private String pauta;

    @Column(length = 60)
    private String categoria;

    @Column(nullable = false)
    @Builder.Default
    private Boolean leida = false;
}
