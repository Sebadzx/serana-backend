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

    private List<Integer> extraerYValidarListaRango(Object raw, int cantidadEsperada, int minVal, int maxVal, String nombreEscala) {
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
            if (valor < minVal || valor > maxVal) {
                throw new BadRequestException("El ítem #" + (i + 1) + " de " + nombreEscala + " debe estar entre " + minVal + " y " + maxVal + ".");
            }
            resultado.add(valor);
        }
        return resultado;
    }

    private int extraerEnteroEnRango(Map<String, Object> map, String key, int minVal, int maxVal, int defaultVal) {
        if (map == null || map.get(key) == null) {
            return defaultVal;
        }
        int val = Integer.parseInt(map.get(key).toString());
        if (val < minVal || val > maxVal) {
            throw new BadRequestException("El campo " + key + " debe estar entre " + minVal + " y " + maxVal + ".");
        }
        return val;
    }

    private String serializarJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
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

    private Map<String, Integer> deserializarMapaHabitos(String json) {
        if (json != null && !json.isBlank()) {
            try {
                return objectMapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
            } catch (Exception ignored) {
            }
        }
        return Collections.emptyMap();
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

        List<Integer> tmd = Collections.nCopies(22, 2);
        List<Integer> staiRasgo = Collections.nCopies(20, 2);

        EvaluacionPsicometrica evaluacion = EvaluacionPsicometrica.builder()
                .usuario(usuario)
                .fechaEvaluacion(LocalDateTime.now())
                .nivelEstres(dto.getNivelEstres())
                .nivelAnsiedad(dto.getNivelAnsiedad())
                .puntuacionGad7(40)
                .puntuacionPss10(44)
                .ts4usRespuestasJson(serializarJson(tmd))
                .asaidasRespuestasJson(serializarJson(staiRasgo))
                .habitoSuenoHoras(dto.getHabitoSuenoHoras())
                .tiempoPantallaHoras(dto.getTiempoPantallaHoras())
                .horasRedesSociales(2.0)
                .horasEstudioVirtual(4.0)
                .dispositivoPrincipal("SMARTPHONE")
                .build();

        EvaluacionPsicometrica evaluacionGuardada = evaluacionRepository.save(evaluacion);

        MLInferenceRequestDTO mlRequest = MLInferenceRequestDTO.builder()
                .tmdRespuestas(tmd)
                .staiRasgoRespuestas(staiRasgo)
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

        Object rawTmd = datos.get("tmdRespuestas") != null ? datos.get("tmdRespuestas") : datos.get("ts4usRespuestas");
        Object rawStai = datos.get("staiRasgoRespuestas") != null ? datos.get("staiRasgoRespuestas") : datos.get("asaidasRespuestas");

        List<Integer> tmd = extraerYValidarListaRango(rawTmd, 22, 0, 4, "TMD (Test de Dependencia al Móvil)");
        List<Integer> staiRasgo = extraerYValidarListaRango(rawStai, 20, 1, 4, "STAI-Rasgo");

        int sumaTmd = tmd.stream().mapToInt(Integer::intValue).sum();             // Rango oficial TMD: 0..88
        int sumaStaiRasgo = staiRasgo.stream().mapToInt(Integer::intValue).sum(); // Rango oficial STAI-Rasgo: 20..80
        int promDependencia10 = (int) Math.round((sumaTmd / 88.0) * 10.0);
        int promAnsiedad10 = (int) Math.round(((sumaStaiRasgo - 20.0) / 60.0) * 10.0);

        EvaluacionPsicometrica evaluacion = EvaluacionPsicometrica.builder()
                .usuario(usuario)
                .fechaEvaluacion(LocalDateTime.now())
                .nivelEstres(promDependencia10)
                .nivelAnsiedad(promAnsiedad10)
                .puntuacionPss10(sumaTmd)
                .puntuacionGad7(sumaStaiRasgo)
                .ts4usRespuestasJson(serializarJson(tmd))
                .asaidasRespuestasJson(serializarJson(staiRasgo))
                .habitoSuenoHoras(7.0)
                .tiempoPantallaHoras(4.0)
                .horasRedesSociales(2.0)
                .horasEstudioVirtual(3.0)
                .dispositivoPrincipal("SMARTPHONE")
                .build();

        EvaluacionPsicometrica guardada = evaluacionRepository.save(evaluacion);

        Map<String, Object> resp = new HashMap<>();
        resp.put("evaluacionId", guardada.getId());
        resp.put("estado", "PROGRESO_PSICOMETRICO_GUARDADO");
        resp.put("puntajeTmdTotal", sumaTmd);
        resp.put("puntajeStaiRasgoTotal", sumaStaiRasgo);
        resp.put("puntajeTs4usTotal", sumaTmd);
        resp.put("puntajeAsaidasTotal", sumaStaiRasgo);
        resp.put("mensaje", "Respuestas reales de TMD (22 ítems) y STAI-Rasgo (20 ítems) guardadas exitosamente.");
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

        Map<String, Integer> habitosMap = new LinkedHashMap<>();
        habitosMap.put("tipoDispositivo", extraerEnteroEnRango(datos, "tipoDispositivo", 1, 4, 2));
        habitosMap.put("tiempoUsoDiario", extraerEnteroEnRango(datos, "tiempoUsoDiario", 1, 4, 3));
        habitosMap.put("lugarUsoFrecuente", extraerEnteroEnRango(datos, "lugarUsoFrecuente", 1, 4, 2));
        habitosMap.put("tipoAccesoRed", extraerEnteroEnRango(datos, "tipoAccesoRed", 1, 3, 3));
        habitosMap.put("reglasHogarUso", extraerEnteroEnRango(datos, "reglasHogarUso", 1, 4, 2));
        habitosMap.put("percepcionDiscriminacion", extraerEnteroEnRango(datos, "percepcionDiscriminacion", 1, 4, 1));
        habitosMap.put("discusionesUsoCelular", extraerEnteroEnRango(datos, "discusionesUsoCelular", 1, 4, 2));
        habitosMap.put("appMensajeria", extraerEnteroEnRango(datos, "appMensajeria", 1, 2, 2));
        habitosMap.put("appVideojuegos", extraerEnteroEnRango(datos, "appVideojuegos", 1, 2, 1));
        habitosMap.put("appRedesSociales", extraerEnteroEnRango(datos, "appRedesSociales", 1, 2, 2));
        habitosMap.put("appMusica", extraerEnteroEnRango(datos, "appMusica", 1, 2, 2));
        habitosMap.put("appVideos", extraerEnteroEnRango(datos, "appVideos", 1, 2, 2));
        habitosMap.put("appGestionTiempo", extraerEnteroEnRango(datos, "appGestionTiempo", 1, 2, 2));
        habitosMap.put("appCamara", extraerEnteroEnRango(datos, "appCamara", 1, 2, 2));
        habitosMap.put("appCorreo", extraerEnteroEnRango(datos, "appCorreo", 1, 2, 2));

        String[] dispLabels = {"CELULAR", "SMARTPHONE", "TABLET", "LAPTOP"};
        double[] horasEquiv = {1.0, 2.5, 4.5, 7.5};
        int idxDisp = habitosMap.get("tipoDispositivo") - 1;
        int idxTiempo = habitosMap.get("tiempoUsoDiario") - 1;

        evaluacion.setHabitosSocioDemoJson(serializarJson(habitosMap));
        evaluacion.setDispositivoPrincipal(dispLabels[idxDisp]);
        evaluacion.setTiempoPantallaHoras(horasEquiv[idxTiempo]);
        evaluacionRepository.save(evaluacion);

        Map<String, Object> resp = new HashMap<>(habitosMap);
        resp.put("evaluacionId", evaluacion.getId());
        resp.put("estado", "HABITOS_DIGITALES_GUARDADOS");
        resp.put("mensaje", "Hábitos de uso digital y contexto sociofamiliar (15 variables SocioDemo) registrados correctamente.");
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
        List<Integer> tmd = deserializarListaEnteros(evaluacion.getTs4usRespuestasJson(), 22, 2);
        List<Integer> staiRasgo = deserializarListaEnteros(evaluacion.getAsaidasRespuestasJson(), 20, 2);
        Map<String, Integer> hab = deserializarMapaHabitos(evaluacion.getHabitosSocioDemoJson());

        MLInferenceRequestDTO mlRequest = MLInferenceRequestDTO.builder()
                .tmdRespuestas(tmd)
                .staiRasgoRespuestas(staiRasgo)
                .tipoDispositivo(hab.getOrDefault("tipoDispositivo", 2))
                .tiempoUsoDiario(hab.getOrDefault("tiempoUsoDiario", 3))
                .lugarUsoFrecuente(hab.getOrDefault("lugarUsoFrecuente", 2))
                .tipoAccesoRed(hab.getOrDefault("tipoAccesoRed", 3))
                .reglasHogarUso(hab.getOrDefault("reglasHogarUso", 2))
                .percepcionDiscriminacion(hab.getOrDefault("percepcionDiscriminacion", 1))
                .discusionesUsoCelular(hab.getOrDefault("discusionesUsoCelular", 2))
                .appMensajeria(hab.getOrDefault("appMensajeria", 2))
                .appVideojuegos(hab.getOrDefault("appVideojuegos", 1))
                .appRedesSociales(hab.getOrDefault("appRedesSociales", 2))
                .appMusica(hab.getOrDefault("appMusica", 2))
                .appVideos(hab.getOrDefault("appVideos", 2))
                .appGestionTiempo(hab.getOrDefault("appGestionTiempo", 2))
                .appCamara(hab.getOrDefault("appCamara", 2))
                .appCorreo(hab.getOrDefault("appCorreo", 2))
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
                    .titulo("Gestión del Tiempo y Control de Dependencia Móvil (TMD)")
                    .moduloICBT("Módulo 3: Planificación de Actividades y Pausas Digitales")
                    .descripcion("Organiza tus sesiones de estudio en bloques libres de notificaciones y establece horarios definidos para revisar mensajes.")
                    .completada(false)
                    .build());
        } else {
            lista.add(RecomendacionICBT.builder()
                    .resultadoInferencia(resultado)
                    .titulo("Higiene del Sueño y Desconexión Digital")
                    .moduloICBT("Módulo 4: Prevención y Bienestar Digital")
                    .descripcion("Mantén tus horarios de sueño estables y evita el uso del teléfono móvil al menos 45 minutos antes de dormir.")
                    .completada(false)
                    .build());
        }

        return lista;
    }
}
