package com.upc.serana.service.impl;

import com.upc.serana.dto.InferenciaMLRequestDTO;
import com.upc.serana.dto.InferenciaMLResponseDTO;
import com.upc.serana.service.InferenciaMLClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Issue 6: Migrado de WebClient (stack reactivo) con .block() a RestClient síncrono.
 * Spring Boot 3.2 / Spring 6.1 introduce RestClient como la API HTTP síncrona moderna
 * que reemplaza a RestTemplate. Esto elimina la inconsistencia de importar WebFlux
 * solo para hacer llamadas bloqueantes.
 */
@Service
@Slf4j
public class InferenciaMLClientServiceImpl implements InferenciaMLClientService {

    private final RestClient restClient;

    /**
     * Issue 6: El bean RestClient es configurado con timeout en RestClientConfig
     * e inyectado aquí. No se construye internamente para mantener la separación
     * entre configuración de infraestructura y lógica de servicio.
     */
    public InferenciaMLClientServiceImpl(RestClient mlRestClient) {
        this.restClient = mlRestClient;
    }

    @Override
    public InferenciaMLResponseDTO solicitarInferencia(InferenciaMLRequestDTO requestDTO) {
        log.info("Enviando evaluacion ID: {} al Microservicio Python de ML Hibrido...", requestDTO.getEvaluacionId());
        try {
            return restClient.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDTO)
                    .retrieve()
                    .body(InferenciaMLResponseDTO.class);
        } catch (RestClientException ex) {
            log.error("Fallo o timeout al conectar con el microservicio Python de ML: {}. Activando fallback de resiliencia...", ex.getMessage());
            return generarRespuestaFallback(requestDTO);
        }
    }

    private InferenciaMLResponseDTO generarRespuestaFallback(InferenciaMLRequestDTO req) {
        // Fallback heuristico clinico de emergencia en caso de caida momentanea de red
        double promTs4us = req.getEscalas().getTs4usRespuestas().stream().mapToInt(Integer::intValue).average().orElse(3.0);
        String nivel = promTs4us >= 3.8 ? "ALTO" : (promTs4us >= 2.6 ? "MODERADO" : "BAJO");
        double prob = promTs4us >= 3.8 ? 0.85 : (promTs4us >= 2.6 ? 0.60 : 0.25);

        return InferenciaMLResponseDTO.builder()
                .evaluacionId(req.getEvaluacionId())
                .nivelAnsiedad(nivel)
                .probabilidadRiesgo(prob)
                .alertaCritica(promTs4us >= 4.2)
                .reglaClinicaDisparada("FALLBACK_RESILIENCIA_LOCAL")
                .indicadoresPLN(new InferenciaMLResponseDTO.IndicadoresPLNDTO(0.0, 0, List.of(), 0.0, 0))
                .featureImportance(List.of(
                        new InferenciaMLResponseDTO.FeatureImportanceDTO("Sobrecarga Digital TS4US (Fallback)", 0.60),
                        new InferenciaMLResponseDTO.FeatureImportanceDTO("Horas de Pantalla", 0.40)
                ))
                .recomendacionesICBT(List.of(
                        "Pauta preventiva: Realizar pausas activas cada 30 minutos.",
                        "Consulte al Departamento de Bienestar Universitario para una evaluacion complementaria."
                ))
                .tiempoInferenciaMs(50.0)
                .build();
    }
}

