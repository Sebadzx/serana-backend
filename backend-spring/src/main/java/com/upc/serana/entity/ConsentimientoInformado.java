package com.upc.serana.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consentimientos_informados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentimientoInformado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Boolean aceptado;

    @Column(nullable = false, length = 20)
    private String versionPolitica;

    @Column(length = 64)
    private String ipAnonimizada;

    @Column(length = 128)
    private String hashFirma;

    @Column(nullable = false)
    private LocalDateTime fechaAceptacion;

    @PrePersist
    protected void onCreate() {
        this.fechaAceptacion = LocalDateTime.now();
    }
}
