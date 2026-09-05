package com.upc.serana.service.impl;

import com.upc.serana.dto.DashboardBienestarDTO;
import com.upc.serana.entity.ResultadoInferencia;
import com.upc.serana.repository.EvaluacionRepository;
import com.upc.serana.repository.ResultadoInferenciaRepository;
import com.upc.serana.service.DashboardBienestarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardBienestarServiceImpl implements DashboardBienestarService {

    private final EvaluacionRepository evaluacionRepository;
    private final ResultadoInferenciaRepository resultadoInferenciaRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardBienestarDTO obtenerMetricasGlobales() {
        long total = evaluacionRepository.count();
        long alertas = resultadoInferenciaRepository.countAlertasCriticas();

        Map<String, Long> distribucion = new HashMap<>();
        distribucion.put("BAJO", 0L);
        distribucion.put("MODERADO", 0L);
        distribucion.put("ALTO", 0L);

        List<Object[]> rawDist = resultadoInferenciaRepository.countDistribucionPorNivel();
        long completadas = 0;
        for (Object[] row : rawDist) {
            String nivel = row[0].toString();
            Long conteo = (Long) row[1];
            distribucion.put(nivel, conteo);
            completadas += conteo;
        }

        List<ResultadoInferencia> alertasRecientes = resultadoInferenciaRepository.findAlertasCriticasRecientes();
        List<DashboardBienestarDTO.AlertaResumenDTO> alertasDTO = alertasRecientes.stream()
                .limit(10)
                .map(a -> {
                    String carrera = (a.getEvaluacion() != null && a.getEvaluacion().getUsuario() != null)
                            ? a.getEvaluacion().getUsuario().getCarrera()
                            : "Anonimo (ARCO)";
                    Integer ciclo = (a.getEvaluacion() != null && a.getEvaluacion().getUsuario() != null)
                            ? a.getEvaluacion().getUsuario().getCiclo()
                            : null;

                    return DashboardBienestarDTO.AlertaResumenDTO.builder()
                            .evaluacionId(a.getEvaluacion().getId())
                            .carrera(carrera)
                            .ciclo(ciclo)
                            .nivelAnsiedad(a.getNivelAnsiedad().name())
                            .probabilidad(a.getProbabilidadRiesgo())
                            .reglaDisparada(a.getReglaClinicaAplicada())
                            .fecha(a.getFechaInferencia().toString())
                            .build();
                }).collect(Collectors.toList());

        return DashboardBienestarDTO.builder()
                .totalEvaluaciones(total)
                .evaluacionesCompletadas(completadas)
                .alertasCriticasActivas(alertas)
                .distribucionNiveles(distribucion)
                .ultimasAlertasCriticas(alertasDTO)
                .build();
    }
}
