package com.arquiweb.grupo4.serana.repositories;

import com.arquiweb.grupo4.serana.entities.ConsentimientoInformado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentimientoInformadoRepository extends JpaRepository<ConsentimientoInformado, Long> {
    Optional<ConsentimientoInformado> findFirstByUsuarioIdOrderByFechaAceptacionDesc(Long usuarioId);
    List<ConsentimientoInformado> findByUsuarioId(Long usuarioId);
}
