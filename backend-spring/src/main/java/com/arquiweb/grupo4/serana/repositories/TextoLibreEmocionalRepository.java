package com.arquiweb.grupo4.serana.repositories;

import com.arquiweb.grupo4.serana.entities.TextoLibreEmocional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TextoLibreEmocionalRepository extends JpaRepository<TextoLibreEmocional, Long> {
    Optional<TextoLibreEmocional> findByEvaluacionId(Long evaluacionId);
}
