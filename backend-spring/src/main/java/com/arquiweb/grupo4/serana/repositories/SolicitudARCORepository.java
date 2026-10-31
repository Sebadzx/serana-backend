package com.arquiweb.grupo4.serana.repositories;

import com.arquiweb.grupo4.serana.entities.SolicitudARCO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudARCORepository extends JpaRepository<SolicitudARCO, Long> {
    List<SolicitudARCO> findByUsuarioIdOrderByFechaSolicitudDesc(Long usuarioId);
    List<SolicitudARCO> findByEstadoOrderByFechaSolicitudAsc(String estado);
}
