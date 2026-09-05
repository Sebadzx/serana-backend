package com.upc.serana.repository;

import com.upc.serana.entity.SolicitudARCO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudARCORepository extends JpaRepository<SolicitudARCO, Long> {
    List<SolicitudARCO> findByUsuarioIdOrderByFechaSolicitudDesc(Long usuarioId);
    List<SolicitudARCO> findByEstadoOrderByFechaSolicitudAsc(SolicitudARCO.EstadoSolicitud estado);
}
