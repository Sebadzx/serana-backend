package com.arquiweb.grupo4.serana.repositories;

import com.arquiweb.grupo4.serana.entities.RecomendacionICBT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecomendacionICBTRepository extends JpaRepository<RecomendacionICBT, Long> {
    List<RecomendacionICBT> findByResultadoInferenciaId(Long resultadoInferenciaId);
}
