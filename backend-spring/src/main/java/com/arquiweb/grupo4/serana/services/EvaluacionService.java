package com.arquiweb.grupo4.serana.services;

import com.arquiweb.grupo4.serana.dtos.EvaluacionRequestDTO;
import com.arquiweb.grupo4.serana.dtos.EvaluacionResponseDTO;
import com.arquiweb.grupo4.serana.entities.Usuario;

import java.util.List;
import java.util.Map;

public interface EvaluacionService {
    EvaluacionResponseDTO registrarEvaluacion(EvaluacionRequestDTO dto, Usuario usuario);
    List<EvaluacionResponseDTO> listarEvaluacionesUsuario(Usuario usuario);

    // Flujo paso a paso (US-04, US-05, US-06, US-07)
    Map<String, Object> paso1CuestionarioPsicometrico(Map<String, Object> datos, Usuario usuario);
    Map<String, Object> paso2HabitosDigitales(Long evaluacionId, Map<String, Object> datos, Usuario usuario);
    EvaluacionResponseDTO paso3TextoLibreYFinalizar(Long evaluacionId, Map<String, Object> datos, Usuario usuario);
}
