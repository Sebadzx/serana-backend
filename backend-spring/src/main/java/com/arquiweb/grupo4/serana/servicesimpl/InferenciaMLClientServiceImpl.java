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
        payload.put("tmdRespuestas", requestDTO.getTmdRespuestas());
        payload.put("staiRasgoRespuestas", requestDTO.getStaiRasgoRespuestas());
        payload.put("tipoDispositivo", requestDTO.getTipoDispositivo() != null ? requestDTO.getTipoDispositivo() : 2);
        payload.put("tiempoUsoDiario", requestDTO.getTiempoUsoDiario() != null ? requestDTO.getTiempoUsoDiario() : 3);
        payload.put("lugarUsoFrecuente", requestDTO.getLugarUsoFrecuente() != null ? requestDTO.getLugarUsoFrecuente() : 2);
        payload.put("tipoAccesoRed", requestDTO.getTipoAccesoRed() != null ? requestDTO.getTipoAccesoRed() : 3);
        payload.put("reglasHogarUso", requestDTO.getReglasHogarUso() != null ? requestDTO.getReglasHogarUso() : 2);
        payload.put("percepcionDiscriminacion", requestDTO.getPercepcionDiscriminacion() != null ? requestDTO.getPercepcionDiscriminacion() : 1);
        payload.put("discusionesUsoCelular", requestDTO.getDiscusionesUsoCelular() != null ? requestDTO.getDiscusionesUsoCelular() : 2);
        payload.put("appMensajeria", requestDTO.getAppMensajeria() != null ? requestDTO.getAppMensajeria() : 2);
        payload.put("appVideojuegos", requestDTO.getAppVideojuegos() != null ? requestDTO.getAppVideojuegos() : 1);
        payload.put("appRedesSociales", requestDTO.getAppRedesSociales() != null ? requestDTO.getAppRedesSociales() : 2);
        payload.put("appMusica", requestDTO.getAppMusica() != null ? requestDTO.getAppMusica() : 2);
        payload.put("appVideos", requestDTO.getAppVideos() != null ? requestDTO.getAppVideos() : 2);
        payload.put("appGestionTiempo", requestDTO.getAppGestionTiempo() != null ? requestDTO.getAppGestionTiempo() : 2);
        payload.put("appCamara", requestDTO.getAppCamara() != null ? requestDTO.getAppCamara() : 2);
        payload.put("appCorreo", requestDTO.getAppCorreo() != null ? requestDTO.getAppCorreo() : 2);
        payload.put("textoLibre", requestDTO.getTextoLibre() != null ? requestDTO.getTextoLibre() : "");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, payload, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                String nivelFinal = String.valueOf(body.getOrDefault("nivelAnsiedadPredicho", body.getOrDefault("nivelAnsiedad", "MODERADO")));
                String predRF = String.valueOf(body.getOrDefault("prediccionRandomForest", nivelFinal));
                Boolean modRegla = Boolean.TRUE.equals(body.get("modificadoPorRegla"));
                Object reglaObj = body.get("reglaClinicaDisparada");
                String reglaDisparada = reglaObj != null ? reglaObj.toString() : null;

                Map<String, Double> probsRF = new LinkedHashMap<>();
                Object probsRaw = body.get("probabilidadesRandomForest");
                if (probsRaw instanceof Map<?, ?> mapRaw) {
                    for (Map.Entry<?, ?> entry : mapRaw.entrySet()) {
                        if (entry.getKey() != null && entry.getValue() != null) {
                            probsRF.put(entry.getKey().toString(), Double.parseDouble(entry.getValue().toString()));
                        }
                    }
                }

                List<String> estresores = Collections.emptyList();
                Object estRaw = body.get("estresoresDetectados");
                if (estRaw instanceof List<?> listRaw) {
                    estresores = listRaw.stream().filter(Objects::nonNull).map(Object::toString).toList();
                }

                Object scoreObj = body.getOrDefault("scoreProbabilidad", body.get("probabilidadPrediccionRF"));
                double scoreProb = scoreObj != null ? Double.parseDouble(scoreObj.toString()) : 0.0;

                Object justObj = body.getOrDefault("justificacionExclusion", body.get("justificacionCli"));
                String justificacion = justObj != null ? justObj.toString() : "Resultado experimental de orientación preventiva.";

                Object polObj = body.get("polaridadSentimiento");
                double polaridad = polObj != null ? Double.parseDouble(polObj.toString()) : 0.0;

                return MLInferenceResponseDTO.builder()
                        .nivelAnsiedadPredicho(nivelFinal)
                        .prediccionRandomForest(predRF)
                        .probabilidadesRandomForest(probsRF)
                        .modificadoPorRegla(modRegla)
                        .reglaClinicaDisparada(reglaDisparada)
                        .scoreProbabilidad(scoreProb)
                        .esCasoCritico(Boolean.TRUE.equals(body.getOrDefault("esCasoCritico", body.get("alertaCritica"))))
                        .justificacionCli(justificacion)
                        .estresoresDetectados(estresores)
                        .polaridadSentimiento(polaridad)
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de Machine Learning en " + endpoint + ": " + e.getMessage(), e);
        }

        throw new RuntimeException("Respuesta inválida del microservicio de Machine Learning");
    }
}
