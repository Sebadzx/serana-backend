package com.arquiweb.grupo4.serana.repositories;

import com.arquiweb.grupo4.serana.entities.ResultadoInferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoInferenciaRepository extends JpaRepository<ResultadoInferencia, Long> {
    Optional<ResultadoInferencia> findByEvaluacionId(Long evaluacionId);

    @Query("SELECT COUNT(r) FROM ResultadoInferencia r WHERE r.nivelAnsiedadPredicho = :nivel")
    long countByNivelAnsiedadPredicho(@Param("nivel") String nivel);

    long countByEsCasoCriticoTrue();
}
