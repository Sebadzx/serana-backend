package com.upc.serana.service;

import com.upc.serana.dto.EvaluacionRequestDTO;
import com.upc.serana.dto.EvaluacionResponseDTO;
import com.upc.serana.dto.GuardarProgresoDTO;

import java.util.List;

public interface EvaluacionService {
    EvaluacionResponseDTO iniciarEvaluacion();
    EvaluacionResponseDTO guardarProgreso(Long evaluacionId, GuardarProgresoDTO progresoDTO);
    EvaluacionResponseDTO completarEvaluacion(Long evaluacionId, EvaluacionRequestDTO evaluacionDTO);
    EvaluacionResponseDTO obtenerResultadoPorId(Long evaluacionId);
    List<EvaluacionResponseDTO> obtenerMisEvaluaciones();
}
