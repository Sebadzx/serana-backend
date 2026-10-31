package com.arquiweb.grupo4.serana.services;

import com.arquiweb.grupo4.serana.dtos.MLInferenceRequestDTO;
import com.arquiweb.grupo4.serana.dtos.MLInferenceResponseDTO;

public interface InferenciaMLClientService {
    MLInferenceResponseDTO realizarInferencia(MLInferenceRequestDTO requestDTO);
}
