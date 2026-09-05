package com.upc.serana.repository;

import com.upc.serana.entity.ConsentimientoInformado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentimientoRepository extends JpaRepository<ConsentimientoInformado, Long> {
    Optional<ConsentimientoInformado> findTopByUsuarioIdOrderByFechaAceptacionDesc(Long usuarioId);
    boolean existsByUsuarioIdAndAceptadoTrue(Long usuarioId);
}
