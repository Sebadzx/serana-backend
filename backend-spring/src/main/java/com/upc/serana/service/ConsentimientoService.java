package com.upc.serana.service;

import com.upc.serana.dto.ConsentimientoRequestDTO;
import com.upc.serana.dto.ConsentimientoResponseDTO;

public interface ConsentimientoService {
    ConsentimientoResponseDTO registrarConsentimiento(ConsentimientoRequestDTO requestDTO, String ipCliente);
    boolean validarConsentimientoActivo(Long usuarioId);
    ConsentimientoResponseDTO consultarEstadoActual();
}
