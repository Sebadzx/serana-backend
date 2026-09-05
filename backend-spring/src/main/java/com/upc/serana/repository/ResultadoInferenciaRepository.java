package com.upc.serana.repository;

import com.upc.serana.entity.ResultadoInferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultadoInferenciaRepository extends JpaRepository<ResultadoInferencia, Long> {
    Optional<ResultadoInferencia> findByEvaluacionId(Long evaluacionId);
    
    @Query("SELECT r.nivelAnsiedad, COUNT(r) FROM ResultadoInferencia r GROUP BY r.nivelAnsiedad")
    List<Object[]> countDistribucionPorNivel();

    @Query("SELECT COUNT(r) FROM ResultadoInferencia r WHERE r.alertaCritica = true")
    long countAlertasCriticas();

    @Query("SELECT r FROM ResultadoInferencia r WHERE r.alertaCritica = true ORDER BY r.fechaInferencia DESC")
    List<ResultadoInferencia> findAlertasCriticasRecientes();
}
