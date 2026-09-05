package com.upc.serana.repository;

import com.upc.serana.entity.RecomendacionICBT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecomendacionICBTRepository extends JpaRepository<RecomendacionICBT, Long> {
    List<RecomendacionICBT> findByResultadoInferenciaId(Long resultadoId);
}
