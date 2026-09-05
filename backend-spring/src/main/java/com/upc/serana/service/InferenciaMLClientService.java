package com.upc.serana.service;

import com.upc.serana.dto.InferenciaMLRequestDTO;
import com.upc.serana.dto.InferenciaMLResponseDTO;

public interface InferenciaMLClientService {
    InferenciaMLResponseDTO solicitarInferencia(InferenciaMLRequestDTO requestDTO);
}
