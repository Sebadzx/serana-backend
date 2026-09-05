package com.upc.serana.repository;

import com.upc.serana.entity.EvaluacionPsicometrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionRepository extends JpaRepository<EvaluacionPsicometrica, Long> {
    List<EvaluacionPsicometrica> findByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);
    Optional<EvaluacionPsicometrica> findByIdAndUsuarioId(Long id, Long usuarioId);
    
    @Query("SELECT COUNT(e) FROM EvaluacionPsicometrica e WHERE e.estado = :estado")
    long countByEstado(@Param("estado") EvaluacionPsicometrica.EstadoEvaluacion estado);

    @Query("SELECT e FROM EvaluacionPsicometrica e WHERE e.usuario.id = :usuarioId AND e.estado = 'EN_PROGRESO' ORDER BY e.fechaInicio DESC")
    List<EvaluacionPsicometrica> findEnProgresoByUsuarioId(@Param("usuarioId") Long usuarioId);
}
