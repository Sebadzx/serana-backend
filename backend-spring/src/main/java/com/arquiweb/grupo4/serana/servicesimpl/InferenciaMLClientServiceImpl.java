package com.arquiweb.grupo4.serana.servicesimpl;

import com.arquiweb.grupo4.serana.dtos.MLInferenceRequestDTO;
import com.arquiweb.grupo4.serana.dtos.MLInferenceResponseDTO;
import com.arquiweb.grupo4.serana.services.InferenciaMLClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class InferenciaMLClientServiceImpl implements InferenciaMLClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ml.service.url:http://localhost:8001}")
    private String mlServiceUrl;

    public InferenciaMLClientServiceImpl() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public MLInferenceResponseDTO realizarInferencia(MLInferenceRequestDTO requestDTO) {
        String endpoint = mlServiceUrl.endsWith("/predict") ? mlServiceUrl : (mlServiceUrl + "/predict");

        Map<String, Object> payload = new HashMap<>();
        payload.put("ts4us_respuestas", requestDTO.getTs4usRespuestas());
        payload.put("asaidas_respuestas", requestDTO.getAsaidasRespuestas());
        payload.put("habito_sueno_horas", requestDTO.getHabitoSuenoHoras() != null ? requestDTO.getHabitoSuenoHoras() : 6.5);
        payload.put("tiempo_pantalla_horas", requestDTO.getTiempoPantallaHoras() != null ? requestDTO.getTiempoPantallaHoras() : 8.0);
        payload.put("horas_redes_sociales", requestDTO.getHorasRedesSociales() != null ? requestDTO.getHorasRedesSociales() : 3.0);
        payload.put("horas_estudio_virtual", requestDTO.getHorasEstudioVirtual() != null ? requestDTO.getHorasEstudioVirtual() : 5.0);
        payload.put("dispositivo_principal", requestDTO.getDispositivoPrincipal() != null ? requestDTO.getDispositivoPrincipal() : "LAPTOP");
        payload.put("texto_libre", requestDTO.getTextoLibre() != null ? requestDTO.getTextoLibre() : "");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, payload, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                String nivelFinal = String.valueOf(body.getOrDefault("nivel_ansiedad_predicho", body.getOrDefault("nivelAnsiedad", "MODERADO")));
                String predRF = String.valueOf(body.getOrDefault("prediccion_random_forest", body.getOrDefault("prediccionRandomForest", nivelFinal)));
                Boolean modRegla = Boolean.TRUE.equals(body.getOrDefault("modificado_por_regla", body.get("modificadoPorRegla")));
                Object reglaObj = body.getOrDefault("regla_clinica_disparada", body.get("reglaClinicaDisparada"));
                String reglaDisparada = reglaObj != null ? reglaObj.toString() : null;

                Map<String, Double> probsRF = new LinkedHashMap<>();
                Object probsRaw = body.getOrDefault("probabilidades_random_forest", body.get("probabilidadesRandomForest"));
                if (probsRaw instanceof Map<?, ?> mapRaw) {
                    for (Map.Entry<?, ?> entry : mapRaw.entrySet()) {
                        if (entry.getKey() != null && entry.getValue() != null) {
                            probsRF.put(entry.getKey().toString(), Double.parseDouble(entry.getValue().toString()));
                        }
                    }
                }

                List<String> estresores = Collections.emptyList();
                Object estRaw = body.get("estresores_detectados");
                if (estRaw instanceof List<?> listRaw) {
                    estresores = listRaw.stream().filter(Objects::nonNull).map(Object::toString).toList();
                }

                return MLInferenceResponseDTO.builder()
                        .nivelAnsiedadPredicho(nivelFinal)
                        .prediccionRandomForest(predRF)
                        .probabilidadesRandomForest(probsRF)
                        .modificadoPorRegla(modRegla)
                        .reglaClinicaDisparada(reglaDisparada)
                        .scoreProbabilidad(body.get("score_probabilidad") != null ? Double.parseDouble(body.get("score_probabilidad").toString()) : 0.0)
                        .esCasoCritico(Boolean.TRUE.equals(body.get("es_caso_critico")))
                        .justificacionCli(String.valueOf(body.getOrDefault("justificacion_cli", "Resultado experimental de orientación preventiva.")))
                        .estresoresDetectados(estresores)
                        .polaridadSentimiento(body.get("polaridad_sentimiento") != null ? Double.parseDouble(body.get("polaridad_sentimiento").toString()) : 0.0)
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de Machine Learning en " + endpoint + ": " + e.getMessage(), e);
        }

        throw new RuntimeException("Respuesta inválida del microservicio de Machine Learning");
    }
}
