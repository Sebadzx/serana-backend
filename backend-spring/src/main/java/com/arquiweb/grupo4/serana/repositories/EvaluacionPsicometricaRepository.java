package com.arquiweb.grupo4.serana.repositories;

import com.arquiweb.grupo4.serana.entities.EvaluacionPsicometrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionPsicometricaRepository extends JpaRepository<EvaluacionPsicometrica, Long> {
    List<EvaluacionPsicometrica> findByUsuarioIdOrderByFechaEvaluacionDesc(Long usuarioId);
    Optional<EvaluacionPsicometrica> findFirstByUsuarioIdOrderByFechaEvaluacionDesc(Long usuarioId);
}
