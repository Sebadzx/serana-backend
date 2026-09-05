package com.upc.serana.service;

import com.upc.serana.dto.SolicitudARCODTO;
import com.upc.serana.entity.SolicitudARCO;

public interface ArcoService {
    SolicitudARCO registrarSolicitud(SolicitudARCODTO dto);
    void ejecutarCancelacionDisociacion(Long usuarioId);
}
