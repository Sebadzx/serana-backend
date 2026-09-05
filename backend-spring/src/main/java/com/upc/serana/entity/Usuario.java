package com.upc.serana.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 120)
    private String nombreCompleto;

    @Column(length = 20)
    private String codigoUniversitario;

    @Column(length = 100)
    private String facultad;

    @Column(length = 100)
    private String carrera;

    @Column
    private Integer ciclo; // 1 a 12

    @Column(length = 60)
    private String campus; // Monterrico, San Isidro, San Miguel, Villa

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private Rol rol;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
