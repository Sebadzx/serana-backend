package com.arquiweb.grupo4.serana.servicesimpl;

import com.arquiweb.grupo4.serana.dtos.EvaluacionRequestDTO;
import com.arquiweb.grupo4.serana.dtos.EvaluacionResponseDTO;
import com.arquiweb.grupo4.serana.dtos.MLInferenceRequestDTO;
import com.arquiweb.grupo4.serana.dtos.MLInferenceResponseDTO;
import com.arquiweb.grupo4.serana.entities.*;
import com.arquiweb.grupo4.serana.exceptions.BadRequestException;
import com.arquiweb.grupo4.serana.exceptions.ResourceNotFoundException;
import com.arquiweb.grupo4.serana.repositories.*;
import com.arquiweb.grupo4.serana.services.ConsentimientoService;
import com.arquiweb.grupo4.serana.services.EvaluacionService;
import com.arquiweb.grupo4.serana.services.InferenciaMLClientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EvaluacionServiceImpl implements EvaluacionService {

    private static final String SIN_TERMINOS_MSG = "No se detectaron términos del diccionario";

    private final EvaluacionPsicometricaRepository evaluacionRepository;
    private final TextoLibreEmocionalRepository textoLibreRepository;
    private final ResultadoInferenciaRepository resultadoInferenciaRepository;
    private final RecomendacionICBTRepository recomendacionRepository;
    private final ConsentimientoService consentimientoService;
    private final InferenciaMLClientService mlClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EvaluacionServiceImpl(
            EvaluacionPsicometricaRepository evaluacionRepository,
            TextoLibreEmocionalRepository textoLibreRepository,
            ResultadoInferenciaRepository resultadoInferenciaRepository,
            RecomendacionICBTRepository recomendacionRepository,
            ConsentimientoService consentimientoService,
            InferenciaMLClientService mlClientService) {
        this.evaluacionRepository = evaluacionRepository;
        this.textoLibreRepository = textoLibreRepository;
        this.resultadoInferenciaRepository = resultadoInferenciaRepository;
        this.recomendacionRepository = recomendacionRepository;
        this.consentimientoService = consentimientoService;
        this.mlClientService = mlClientService;
    }

    private List<Integer> extraerYValidarListaLikert(Object raw, int cantidadEsperada, String nombreEscala) {
        if (!(raw instanceof List<?> listaRaw) || listaRaw.size() != cantidadEsperada) {
            throw new BadRequestException("La escala " + nombreEscala + " requiere exactamente " + cantidadEsperada + " respuestas reales.");
        }
        List<Integer> resultado = new ArrayList<>(cantidadEsperada);
        for (int i = 0; i < listaRaw.size(); i++) {
            Object item = listaRaw.get(i);
            if (item == null) {
                throw new BadRequestException("El ítem #" + (i + 1) + " de " + nombreEscala + " no fue respondido.");
            }
            int valor = Integer.parseInt(item.toString());
            if (valor < 1 || valor > 5) {
                throw new BadRequestException("El ítem #" + (i + 1) + " de " + nombreEscala + " debe estar entre 1 y 5.");
            }
            resultado.add(valor);
        }
        return resultado;
    }

    private String serializarJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<Integer> deserializarListaEnteros(String json, int cantidadEsperada, int valorPorDefecto) {
        if (json != null && !json.isBlank()) {
            try {
                List<Integer> parsed = objectMapper.readValue(json, new TypeReference<List<Integer>>() {});
                if (parsed != null && parsed.size() == cantidadEsperada) {
                    return parsed;
                }
            } catch (Exception ignored) {
            }
        }
        return Collections.nCopies(cantidadEsperada, valorPorDefecto);
    }

    private Map<String, Double> deserializarMapaProbabilidades(String json) {
        if (json != null && !json.isBlank()) {
            try {
                return objectMapper.readValue(json, new TypeReference<Map<String, Double>>() {});
            } catch (Exception ignored) {
            }
        }
        return Collections.emptyMap();
    }

    @Override
    @Transactional
    public EvaluacionResponseDTO registrarEvaluacion(EvaluacionRequestDTO dto, Usuario usuario) {
        if (!consentimientoService.tieneConsentimientoVigente(usuario.getId())) {
            throw new BadRequestException("El usuario debe aceptar el consentimiento informado antes de realizar una evaluacion (Ley N° 29733)");
        }

        List<Integer> ts4us = Collections.nCopies(13, 3);
        List<Integer> asaidas = Collections.nCopies(8, 3);

        EvaluacionPsicometrica evaluacion = EvaluacionPsicometrica.builder()
                .usuario(usuario)
                .fechaEvaluacion(LocalDateTime.now())
                .nivelEstres(dto.getNivelEstres())
                .nivelAnsiedad(dto.getNivelAnsiedad())
                .puntuacionGad7(dto.getPuntuacionGad7())
                .puntuacionPss10(dto.getPuntuacionPss10())
                .ts4usRespuestasJson(serializarJson(ts4us))
                .asaidasRespuestasJson(serializarJson(asaidas))
                .habitoSuenoHoras(dto.getHabitoSuenoHoras())
                .tiempoPantallaHoras(dto.getTiempoPantallaHoras())
                .horasRedesSociales(3.0)
                .horasEstudioVirtual(5.0)
                .dispositivoPrincipal("LAPTOP")
                .build();

        EvaluacionPsicometrica evaluacionGuardada = evaluacionRepository.save(evaluacion);

        MLInferenceRequestDTO mlRequest = MLInferenceRequestDTO.builder()
                .ts4usRespuestas(ts4us)
                .asaidasRespuestas(asaidas)
                .habitoSuenoHoras(dto.getHabitoSuenoHoras())
                .tiempoPantallaHoras(dto.getTiempoPantallaHoras())
                .horasRedesSociales(3.0)
                .horasEstudioVirtual(5.0)
                .dispositivoPrincipal("LAPTOP")
                .textoLibre(dto.getTextoLibre())
                .build();

        MLInferenceResponseDTO mlResponse = mlClientService.realizarInferencia(mlRequest);
        return persistirResultadoEIntervenciones(evaluacionGuardada, dto.getTextoLibre(), mlResponse);
    }

    @Override
    @Transactional
    public Map<String, Object> paso1CuestionarioPsicometrico(Map<String, Object> datos, Usuario usuario) {
        if (!consentimientoService.tieneConsentimientoVigente(usuario.getId())) {
            throw new BadRequestException("El usuario debe aceptar el consentimiento informado antes de realizar una evaluacion (Ley N° 29733)");
        }

        List<Integer> ts4us = extraerYValidarListaLikert(datos.get("ts4usRespuestas"), 13, "TS4US");
        List<Integer> asaidas = extraerYValidarListaLikert(datos.get("asaidasRespuestas"), 8, "ASAIDAS");

        int sumaTs4us = ts4us.stream().mapToInt(Integer::intValue).sum();       // Rango real: 13..65
        int sumaAsaidas = asaidas.stream().mapToInt(Integer::intValue).sum();   // Rango real: 8..40
        int promEstres10 = (int) Math.round((sumaTs4us / 65.0) * 10.0);
        int promAnsiedad10 = (int) Math.round((sumaAsaidas / 40.0) * 10.0);

        EvaluacionPsicometrica evaluacion = EvaluacionPsicometrica.builder()
                .usuario(usuario)
                .fechaEvaluacion(LocalDateTime.now())
                .nivelEstres(promEstres10)
                .nivelAnsiedad(promAnsiedad10)
                .puntuacionPss10(sumaTs4us)
                .puntuacionGad7(sumaAsaidas)
                .ts4usRespuestasJson(serializarJson(ts4us))
                .asaidasRespuestasJson(serializarJson(asaidas))
                .habitoSuenoHoras(7.0)
                .tiempoPantallaHoras(8.0)
                .horasRedesSociales(3.0)
                .horasEstudioVirtual(5.0)
                .dispositivoPrincipal("LAPTOP")
                .build();

        EvaluacionPsicometrica guardada = evaluacionRepository.save(evaluacion);

        Map<String, Object> resp = new HashMap<>();
        resp.put("evaluacionId", guardada.getId());
        resp.put("estado", "PROGRESO_PSICOMETRICO_GUARDADO");
        resp.put("puntajeTs4usTotal", sumaTs4us);
        resp.put("puntajeAsaidasTotal", sumaAsaidas);
        resp.put("mensaje", "Respuestas reales de TS4US (13 ítems) y ASAIDAS (8 ítems) guardadas exitosamente.");
        resp.put("siguientePaso", "PUT /api/evaluaciones/" + guardada.getId() + "/paso2-habitos");
        return resp;
    }

    @Override
    @Transactional
    public Map<String, Object> paso2HabitosDigitales(Long evaluacionId, Map<String, Object> datos, Usuario usuario) {
        EvaluacionPsicometrica evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluacion no encontrada con ID: " + evaluacionId));

        if (!evaluacion.getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("No tiene permisos para modificar esta evaluacion");
        }

        Double habitoSueno = datos.get("habitoSuenoHoras") != null ? Double.parseDouble(datos.get("habitoSuenoHoras").toString()) : 6.5;
        Double tiempoPantalla = datos.get("tiempoPantallaHoras") != null ? Double.parseDouble(datos.get("tiempoPantallaHoras").toString()) : 8.0;
        Double horasRedes = datos.get("horasRedesSociales") != null ? Double.parseDouble(datos.get("horasRedesSociales").toString()) : 3.0;
        Double horasEstudio = datos.get("horasEstudioVirtual") != null ? Double.parseDouble(datos.get("horasEstudioVirtual").toString()) : 5.0;
        String dispositivo = datos.get("dispositivoPrincipal") != null ? datos.get("dispositivoPrincipal").toString().toUpperCase() : "LAPTOP";

        evaluacion.setHabitoSuenoHoras(habitoSueno);
        evaluacion.setTiempoPantallaHoras(tiempoPantalla);
        evaluacion.setHorasRedesSociales(horasRedes);
        evaluacion.setHorasEstudioVirtual(horasEstudio);
        evaluacion.setDispositivoPrincipal(dispositivo);
        evaluacionRepository.save(evaluacion);

        Map<String, Object> resp = new HashMap<>();
        resp.put("evaluacionId", evaluacion.getId());
        resp.put("estado", "HABITOS_DIGITALES_GUARDADOS");
        resp.put("habitoSuenoHoras", habitoSueno);
        resp.put("tiempoPantallaHoras", tiempoPantalla);
        resp.put("horasRedesSociales", horasRedes);
        resp.put("horasEstudioVirtual", horasEstudio);
        resp.put("dispositivoPrincipal", dispositivo);
        resp.put("mensaje", "Hábitos de uso digital y sueño registrados correctamente.");
        resp.put("siguientePaso", "POST /api/evaluaciones/" + evaluacion.getId() + "/paso3-texto-libre");
        return resp;
    }

    @Override
    @Transactional
    public EvaluacionResponseDTO paso3TextoLibreYFinalizar(Long evaluacionId, Map<String, Object> datos, Usuario usuario) {
        EvaluacionPsicometrica evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluacion no encontrada con ID: " + evaluacionId));

        if (!evaluacion.getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("No tiene permisos para modificar esta evaluacion");
        }

        String textoLibre = datos.get("textoLibre") != null ? datos.get("textoLibre").toString() : "";
        List<Integer> ts4us = deserializarListaEnteros(evaluacion.getTs4usRespuestasJson(), 13, 3);
        List<Integer> asaidas = deserializarListaEnteros(evaluacion.getAsaidasRespuestasJson(), 8, 3);

        MLInferenceRequestDTO mlRequest = MLInferenceRequestDTO.builder()
                .nivelEstres(evaluacion.getNivelEstres())
                .nivelAnsiedad(evaluacion.getNivelAnsiedad())
                .puntuacionGad7(evaluacion.getPuntuacionGad7())
                .puntuacionPss10(evaluacion.getPuntuacionPss10())
                .ts4usRespuestas(ts4us)
                .asaidasRespuestas(asaidas)
                .habitoSuenoHoras(evaluacion.getHabitoSuenoHoras())
                .tiempoPantallaHoras(evaluacion.getTiempoPantallaHoras())
                .horasRedesSociales(evaluacion.getHorasRedesSociales() != null ? evaluacion.getHorasRedesSociales() : 3.0)
                .horasEstudioVirtual(evaluacion.getHorasEstudioVirtual() != null ? evaluacion.getHorasEstudioVirtual() : 5.0)
                .dispositivoPrincipal(evaluacion.getDispositivoPrincipal() != null ? evaluacion.getDispositivoPrincipal() : "LAPTOP")
                .textoLibre(textoLibre)
                .build();

        MLInferenceResponseDTO mlResponse = mlClientService.realizarInferencia(mlRequest);
        return persistirResultadoEIntervenciones(evaluacion, textoLibre, mlResponse);
    }

    private EvaluacionResponseDTO persistirResultadoEIntervenciones(
            EvaluacionPsicometrica evaluacion,
            String textoLibre,
            MLInferenceResponseDTO mlResponse) {

        List<String> listaEstresores = mlResponse.getEstresoresDetectados();
        String textoEstresores = (listaEstresores != null && !listaEstresores.isEmpty())
                ? String.join(", ", listaEstresores)
                : SIN_TERMINOS_MSG;

        TextoLibreEmocional tLibre = textoLibreRepository.findByEvaluacionId(evaluacion.getId())
                .orElse(TextoLibreEmocional.builder().evaluacion(evaluacion).build());
        tLibre.setTextoLibre(textoLibre != null ? textoLibre : "");
        tLibre.setEstresoresDetectados(textoEstresores);
        tLibre.setPolaridadSentimiento(mlResponse.getPolaridadSentimiento());
        textoLibreRepository.save(tLibre);

        ResultadoInferencia resultado = resultadoInferenciaRepository.findByEvaluacionId(evaluacion.getId())
                .orElse(ResultadoInferencia.builder().evaluacion(evaluacion).build());
        resultado.setNivelAnsiedadPredicho(mlResponse.getNivelAnsiedadPredicho());
        resultado.setPrediccionRandomForest(mlResponse.getPrediccionRandomForest());
        resultado.setProbabilidadesRfJson(serializarJson(mlResponse.getProbabilidadesRandomForest()));
        resultado.setModificadoPorRegla(mlResponse.getModificadoPorRegla());
        resultado.setReglaClinicaDisparada(mlResponse.getReglaClinicaDisparada());
        resultado.setScoreProbabilidad(mlResponse.getScoreProbabilidad());
        resultado.setEsCasoCritico(mlResponse.getEsCasoCritico());
        resultado.setJustificacionCli(mlResponse.getJustificacionCli());
        resultado.setFechaInferencia(LocalDateTime.now());

        ResultadoInferencia resultadoGuardado = resultadoInferenciaRepository.save(resultado);

        List<RecomendacionICBT> anteriores = recomendacionRepository.findByResultadoInferenciaId(resultadoGuardado.getId());
        if (!anteriores.isEmpty()) {
            recomendacionRepository.deleteAll(anteriores);
        }

        List<RecomendacionICBT> recomendaciones = generarRecomendaciones(resultadoGuardado, mlResponse.getNivelAnsiedadPredicho());
        List<RecomendacionICBT> recomendacionesGuardadas = recomendacionRepository.saveAll(recomendaciones);

        List<EvaluacionResponseDTO.RecomendacionDTO> recDTOs = recomendacionesGuardadas.stream()
                .map(r -> EvaluacionResponseDTO.RecomendacionDTO.builder()
                        .id(r.getId())
                        .titulo(r.getTitulo())
                        .moduloICBT(r.getModuloICBT())
                        .descripcion(r.getDescripcion())
                        .completada(r.getCompletada())
                        .build())
                .toList();

        return EvaluacionResponseDTO.builder()
                .idEvaluacion(evaluacion.getId())
                .fechaEvaluacion(evaluacion.getFechaEvaluacion())
                .nivelAnsiedadPredicho(resultadoGuardado.getNivelAnsiedadPredicho())
                .prediccionRandomForest(resultadoGuardado.getPrediccionRandomForest())
                .probabilidadesRandomForest(mlResponse.getProbabilidadesRandomForest())
                .modificadoPorRegla(resultadoGuardado.getModificadoPorRegla())
                .reglaClinicaDisparada(resultadoGuardado.getReglaClinicaDisparada())
                .scoreProbabilidad(resultadoGuardado.getScoreProbabilidad())
                .esCasoCritico(resultadoGuardado.getEsCasoCritico())
                .justificacionCli(resultadoGuardado.getJustificacionCli())
                .estresoresDetectados(tLibre.getEstresoresDetectados())
                .polaridadSentimiento(tLibre.getPolaridadSentimiento())
                .recomendacionesICBT(recDTOs)
                .build();
    }

    @Override
    public List<EvaluacionResponseDTO> listarEvaluacionesUsuario(Usuario usuario) {
        List<EvaluacionPsicometrica> evaluaciones = evaluacionRepository.findByUsuarioIdOrderByFechaEvaluacionDesc(usuario.getId());

        return evaluaciones.stream().map(e -> {
            TextoLibreEmocional t = textoLibreRepository.findByEvaluacionId(e.getId()).orElse(null);
            ResultadoInferencia r = resultadoInferenciaRepository.findByEvaluacionId(e.getId()).orElse(null);

            List<EvaluacionResponseDTO.RecomendacionDTO> recDTOs = new ArrayList<>();
            if (r != null) {
                recDTOs = recomendacionRepository.findByResultadoInferenciaId(r.getId()).stream()
                        .map(rec -> EvaluacionResponseDTO.RecomendacionDTO.builder()
                                .id(rec.getId())
                                .titulo(rec.getTitulo())
                                .moduloICBT(rec.getModuloICBT())
                                .descripcion(rec.getDescripcion())
                                .completada(rec.getCompletada())
                                .build())
                        .toList();
            }

            String estresores = (t != null && t.getEstresoresDetectados() != null && !t.getEstresoresDetectados().isBlank())
                    ? t.getEstresoresDetectados()
                    : SIN_TERMINOS_MSG;

            return EvaluacionResponseDTO.builder()
                    .idEvaluacion(e.getId())
                    .fechaEvaluacion(e.getFechaEvaluacion())
                    .nivelAnsiedadPredicho(r != null ? r.getNivelAnsiedadPredicho() : "PENDIENTE")
                    .prediccionRandomForest(r != null && r.getPrediccionRandomForest() != null ? r.getPrediccionRandomForest() : (r != null ? r.getNivelAnsiedadPredicho() : "PENDIENTE"))
                    .probabilidadesRandomForest(r != null ? deserializarMapaProbabilidades(r.getProbabilidadesRfJson()) : Collections.emptyMap())
                    .modificadoPorRegla(r != null && Boolean.TRUE.equals(r.getModificadoPorRegla()))
                    .reglaClinicaDisparada(r != null ? r.getReglaClinicaDisparada() : null)
                    .scoreProbabilidad(r != null ? r.getScoreProbabilidad() : 0.0)
                    .esCasoCritico(r != null && Boolean.TRUE.equals(r.getEsCasoCritico()))
                    .justificacionCli(r != null ? r.getJustificacionCli() : "")
                    .estresoresDetectados(estresores)
                    .polaridadSentimiento(t != null ? t.getPolaridadSentimiento() : 0.0)
                    .recomendacionesICBT(recDTOs)
                    .build();
        }).toList();
    }

    private List<RecomendacionICBT> generarRecomendaciones(ResultadoInferencia resultado, String nivelAnsiedad) {
        List<RecomendacionICBT> lista = new ArrayList<>();

        if ("ALTO".equalsIgnoreCase(nivelAnsiedad)) {
            lista.add(RecomendacionICBT.builder()
                    .resultadoInferencia(resultado)
                    .titulo("Reestructuración Cognitiva Preventiva")
                    .moduloICBT("Módulo 1: Identificación de Pensamientos Automáticos")
                    .descripcion("Identifica pensamientos catastróficos asociados a la sobrecarga académica y reemplázalos por interpretaciones funcionales.")
                    .completada(false)
                    .build());
            lista.add(RecomendacionICBT.builder()
                    .resultadoInferencia(resultado)
                    .titulo("Técnica de Respiración Diafragmática 4-7-8")
                    .moduloICBT("Módulo 2: Autorregulación Fisiológica")
                    .descripcion("Practica la respiración pausada 3 veces al día para reducir la hiperactivación frente al uso continuo de pantallas.")
                    .completada(false)
                    .build());
        } else if ("MODERADO".equalsIgnoreCase(nivelAnsiedad)) {
            lista.add(RecomendacionICBT.builder()
                    .resultadoInferencia(resultado)
                    .titulo("Gestión del Tiempo y Técnica Pomodoro")
                    .moduloICBT("Módulo 3: Planificación de Actividades Académicas")
                    .descripcion("Organiza tus sesiones de estudio en bloques de 25 minutos con 5 minutos de descanso activo fuera de la pantalla.")
                    .completada(false)
                    .build());
        } else {
            lista.add(RecomendacionICBT.builder()
                    .resultadoInferencia(resultado)
                    .titulo("Higiene del Sueño y Desconexión Digital")
                    .moduloICBT("Módulo 4: Prevención y Bienestar Digital")
                    .descripcion("Mantén tus horarios de sueño estables y evita el uso de pantallas al menos 45 minutos antes de dormir.")
                    .completada(false)
                    .build());
        }

        return lista;
    }
}
