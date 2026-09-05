package com.upc.serana.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.serana.dto.*;
import com.upc.serana.entity.*;
import com.upc.serana.repository.ConsentimientoRepository;
import com.upc.serana.repository.EvaluacionRepository;
import com.upc.serana.repository.ResultadoInferenciaRepository;
import com.upc.serana.service.EvaluacionService;
import com.upc.serana.service.InferenciaMLClientService;
import com.upc.serana.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluacionServiceImpl implements EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final ResultadoInferenciaRepository resultadoInferenciaRepository;
    private final ConsentimientoRepository consentimientoRepository;
    private final UsuarioService usuarioService;
    private final InferenciaMLClientService inferenciaMLClientService;
    // Issue 5: ObjectMapper inyectado como bean Spring en lugar de instanciarse
    // como campo (new ObjectMapper()). Spring Boot configura este bean con todos
    // los módulos Jackson necesarios (JavaTimeModule, etc.) de forma automática.
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public EvaluacionResponseDTO iniciarEvaluacion() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();

        // Validacion estricta de consentimiento informado (Ley N° 29733 - US-03)
        boolean tieneConsentimiento = consentimientoRepository.existsByUsuarioIdAndAceptadoTrue(usuario.getId());
        if (!tieneConsentimiento) {
            throw new IllegalStateException("Debe aceptar el Consentimiento Informado previo antes de iniciar una evaluacion.");
        }

        // Si ya tiene una evaluacion en progreso, retornarla
        List<EvaluacionPsicometrica> enProgreso = evaluacionRepository.findEnProgresoByUsuarioId(usuario.getId());
        if (!enProgreso.isEmpty()) {
            EvaluacionPsicometrica existente = enProgreso.get(0);
            return mapearAResponseDTO(existente, null);
        }

        EvaluacionPsicometrica nueva = EvaluacionPsicometrica.builder()
                .usuario(usuario)
                .estado(EvaluacionPsicometrica.EstadoEvaluacion.EN_PROGRESO)
                .fechaInicio(LocalDateTime.now())
                .build();

        EvaluacionPsicometrica guardada = evaluacionRepository.save(nueva);
        return mapearAResponseDTO(guardada, null);
    }

    @Override
    @Transactional
    public EvaluacionResponseDTO guardarProgreso(Long evaluacionId, GuardarProgresoDTO progresoDTO) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        EvaluacionPsicometrica evaluacion = evaluacionRepository.findByIdAndUsuarioId(evaluacionId, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Evaluacion no encontrada o no pertenece al usuario."));

        if (evaluacion.getEstado() == EvaluacionPsicometrica.EstadoEvaluacion.COMPLETADA) {
            throw new IllegalStateException("No se puede modificar una evaluacion que ya fue completada.");
        }

        if (progresoDTO.getTs4usRespuestas() != null) {
            evaluacion.setTs4usRespuestas(progresoDTO.getTs4usRespuestas().stream()
                    .map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (progresoDTO.getAsaidasRespuestas() != null) {
            evaluacion.setAsaidasRespuestas(progresoDTO.getAsaidasRespuestas().stream()
                    .map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (progresoDTO.getHorasPantallaDia() != null) evaluacion.setHorasPantallaDia(progresoDTO.getHorasPantallaDia());
        if (progresoDTO.getHorasRedesSociales() != null) evaluacion.setHorasRedesSociales(progresoDTO.getHorasRedesSociales());
        if (progresoDTO.getHorasEstudioVirtual() != null) evaluacion.setHorasEstudioVirtual(progresoDTO.getHorasEstudioVirtual());
        if (progresoDTO.getDispositivoPrincipal() != null) evaluacion.setDispositivoPrincipal(progresoDTO.getDispositivoPrincipal());

        if (progresoDTO.getTextoLibre() != null && !progresoDTO.getTextoLibre().isBlank()) {
            if (evaluacion.getTextoLibre() == null) {
                TextoLibreEmocional texto = TextoLibreEmocional.builder()
                        .evaluacion(evaluacion)
                        .texto(progresoDTO.getTextoLibre())
                        .fechaRegistro(LocalDateTime.now())
                        .build();
                evaluacion.setTextoLibre(texto);
            } else {
                evaluacion.getTextoLibre().setTexto(progresoDTO.getTextoLibre());
            }
        }

        EvaluacionPsicometrica actualizada = evaluacionRepository.save(evaluacion);
        return mapearAResponseDTO(actualizada, null);
    }

    @Override
    @Transactional
    public EvaluacionResponseDTO completarEvaluacion(Long evaluacionId, EvaluacionRequestDTO evaluacionDTO) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        EvaluacionPsicometrica evaluacion = evaluacionRepository.findByIdAndUsuarioId(evaluacionId, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Evaluacion no encontrada o no pertenece al usuario."));

        if (evaluacion.getEstado() == EvaluacionPsicometrica.EstadoEvaluacion.COMPLETADA) {
            throw new IllegalStateException("Esta evaluacion ya ha sido procesada previamente.");
        }

        // 1. Persistir respuestas psicometricas y texto
        evaluacion.setTs4usRespuestas(evaluacionDTO.getTs4usRespuestas().stream().map(String::valueOf).collect(Collectors.joining(",")));
        evaluacion.setAsaidasRespuestas(evaluacionDTO.getAsaidasRespuestas().stream().map(String::valueOf).collect(Collectors.joining(",")));
        evaluacion.setHorasPantallaDia(evaluacionDTO.getHorasPantallaDia());
        evaluacion.setHorasRedesSociales(evaluacionDTO.getHorasRedesSociales() != null ? evaluacionDTO.getHorasRedesSociales() : 3.0);
        evaluacion.setHorasEstudioVirtual(evaluacionDTO.getHorasEstudioVirtual() != null ? evaluacionDTO.getHorasEstudioVirtual() : 4.0);
        evaluacion.setDispositivoPrincipal(evaluacionDTO.getDispositivoPrincipal());
        evaluacion.setEstado(EvaluacionPsicometrica.EstadoEvaluacion.COMPLETADA);
        evaluacion.setFechaFinalizacion(LocalDateTime.now());

        if (evaluacionDTO.getTextoLibre() != null) {
            TextoLibreEmocional texto = TextoLibreEmocional.builder()
                    .evaluacion(evaluacion)
                    .texto(evaluacionDTO.getTextoLibre())
                    .fechaRegistro(LocalDateTime.now())
                    .build();
            evaluacion.setTextoLibre(texto);
        }

        evaluacionRepository.save(evaluacion);

        // 2. Construir payload de solicitud para el Microservicio de ML
        InferenciaMLRequestDTO mlRequest = InferenciaMLRequestDTO.builder()
                .evaluacionId(evaluacion.getId())
                .perfilEstudiante(InferenciaMLRequestDTO.PerfilEstudianteDTO.builder()
                        .carrera(usuario.getCarrera())
                        .ciclo(usuario.getCiclo() != null ? usuario.getCiclo() : 7)
                        .horasPantallaDia(evaluacion.getHorasPantallaDia())
                        .horasRedesSociales(evaluacion.getHorasRedesSociales())
                        .horasEstudioVirtual(evaluacion.getHorasEstudioVirtual())
                        .dispositivoPrincipal(evaluacion.getDispositivoPrincipal())
                        .build())
                .escalas(InferenciaMLRequestDTO.EscalasDTO.builder()
                        .ts4usRespuestas(evaluacionDTO.getTs4usRespuestas())
                        .asaidasRespuestas(evaluacionDTO.getAsaidasRespuestas())
                        .build())
                .textoLibre(evaluacionDTO.getTextoLibre())
                .build();

        // 3. Invocar Microservicio de ML Hibrido
        InferenciaMLResponseDTO mlResponse = inferenciaMLClientService.solicitarInferencia(mlRequest);

        // 4. Persistir ResultadoInferencia en Base de Datos
        String topFactoresJson = "";
        try {
            topFactoresJson = objectMapper.writeValueAsString(mlResponse.getFeatureImportance());
        } catch (JsonProcessingException e) {
            log.warn("Error serializando topFactores: {}", e.getMessage());
        }

        String terminosStr = (mlResponse.getIndicadoresPLN() != null && mlResponse.getIndicadoresPLN().getTerminosDetectados() != null)
                ? String.join(", ", mlResponse.getIndicadoresPLN().getTerminosDetectados())
                : "";

        ResultadoInferencia.NivelAnsiedad nivelEnum = ResultadoInferencia.NivelAnsiedad.valueOf(mlResponse.getNivelAnsiedad());

        ResultadoInferencia resultado = ResultadoInferencia.builder()
                .evaluacion(evaluacion)
                .nivelAnsiedad(nivelEnum)
                .probabilidadRiesgo(mlResponse.getProbabilidadRiesgo())
                .alertaCritica(mlResponse.getAlertaCritica() != null && mlResponse.getAlertaCritica())
                .reglaClinicaAplicada(mlResponse.getReglaClinicaDisparada())
                .polaridadSentimiento(mlResponse.getIndicadoresPLN() != null ? mlResponse.getIndicadoresPLN().getPolaridadSentimiento() : 0.0)
                .terminosDetectados(terminosStr)
                .topFactoresJson(topFactoresJson)
                .fechaInferencia(LocalDateTime.now())
                .build();

        // Asignar recomendaciones iCBT
        if (mlResponse.getRecomendacionesICBT() != null) {
            for (String pauta : mlResponse.getRecomendacionesICBT()) {
                RecomendacionICBT rec = RecomendacionICBT.builder()
                        .resultadoInferencia(resultado)
                        .pauta(pauta)
                        .categoria("iCBT")
                        .leida(false)
                        .build();
                resultado.getRecomendaciones().add(rec);
            }
        }

        ResultadoInferencia resultadoGuardado = resultadoInferenciaRepository.save(resultado);
        evaluacion.setResultadoInferencia(resultadoGuardado);

        return mapearAResponseDTO(evaluacion, resultadoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluacionResponseDTO obtenerResultadoPorId(Long evaluacionId) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        EvaluacionPsicometrica evaluacion = evaluacionRepository.findByIdAndUsuarioId(evaluacionId, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Evaluacion no encontrada."));

        ResultadoInferencia resultado = resultadoInferenciaRepository.findByEvaluacionId(evaluacionId).orElse(null);
        return mapearAResponseDTO(evaluacion, resultado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluacionResponseDTO> obtenerMisEvaluaciones() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        List<EvaluacionPsicometrica> evaluaciones = evaluacionRepository.findByUsuarioIdOrderByFechaInicioDesc(usuario.getId());

        return evaluaciones.stream().map(e -> {
            ResultadoInferencia res = resultadoInferenciaRepository.findByEvaluacionId(e.getId()).orElse(null);
            return mapearAResponseDTO(e, res);
        }).collect(Collectors.toList());
    }

    private EvaluacionResponseDTO mapearAResponseDTO(EvaluacionPsicometrica e, ResultadoInferencia r) {
        List<String> pautas = (r != null && r.getRecomendaciones() != null)
                ? r.getRecomendaciones().stream().map(RecomendacionICBT::getPauta).collect(Collectors.toList())
                : new ArrayList<>();

        return EvaluacionResponseDTO.builder()
                .evaluacionId(e.getId())
                .estado(e.getEstado().name())
                .fechaInicio(e.getFechaInicio())
                .fechaFinalizacion(e.getFechaFinalizacion())
                .nivelAnsiedad(r != null ? r.getNivelAnsiedad().name() : null)
                .probabilidadRiesgo(r != null ? r.getProbabilidadRiesgo() : null)
                .alertaCritica(r != null ? r.getAlertaCritica() : null)
                .reglaClinicaAplicada(r != null ? r.getReglaClinicaAplicada() : null)
                .recomendaciones(pautas)
                .topFactores(r != null ? r.getTopFactoresJson() : null)
                .indicadoresPLN(r != null ? r.getTerminosDetectados() : null)
                .build();
    }
}
