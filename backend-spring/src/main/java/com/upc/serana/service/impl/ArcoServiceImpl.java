package com.upc.serana.service.impl;

import com.upc.serana.dto.SolicitudARCODTO;
import com.upc.serana.entity.EvaluacionPsicometrica;
import com.upc.serana.entity.SolicitudARCO;
import com.upc.serana.entity.Usuario;
import com.upc.serana.repository.EvaluacionRepository;
import com.upc.serana.repository.SolicitudARCORepository;
import com.upc.serana.repository.UsuarioRepository;
import com.upc.serana.service.ArcoService;
import com.upc.serana.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArcoServiceImpl implements ArcoService {

    private final SolicitudARCORepository solicitudARCORepository;
    private final EvaluacionRepository evaluacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Override
    @Transactional
    public SolicitudARCO registrarSolicitud(SolicitudARCODTO dto) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();

        SolicitudARCO.TipoDerecho tipo = SolicitudARCO.TipoDerecho.valueOf(dto.getTipoDerecho().toUpperCase());

        SolicitudARCO solicitud = SolicitudARCO.builder()
                .usuario(usuario)
                .tipoDerecho(tipo)
                .motivo(dto.getMotivo())
                .estado(SolicitudARCO.EstadoSolicitud.RECIBIDA)
                .fechaSolicitud(LocalDateTime.now())
                .build();

        SolicitudARCO guardada = solicitudARCORepository.save(solicitud);

        // Si es solicitud de cancelacion o supresion, ejecutar disociacion irreversible inmediata
        if (tipo == SolicitudARCO.TipoDerecho.CANCELACION) {
            ejecutarCancelacionDisociacion(usuario.getId());
            guardada.setEstado(SolicitudARCO.EstadoSolicitud.ATENDIDA);
            guardada.setFechaResolucion(LocalDateTime.now());
            guardada.setRespuestaDetalle("Datos sensibles disociados y anonimizados conforme a la Ley N° 29733.");
            solicitudARCORepository.save(guardada);
        }

        return guardada;
    }

    @Override
    @Transactional
    public void ejecutarCancelacionDisociacion(Long usuarioId) {
        log.info("Ejecutando procedimiento ARCO de Cancelacion/Disociacion para usuario ID: {}", usuarioId);

        // 1. Disociar evaluaciones clinicas (dejar usuario_id en NULL para preservar estadistica sin vincular a persona)
        List<EvaluacionPsicometrica> evaluaciones = evaluacionRepository.findByUsuarioIdOrderByFechaInicioDesc(usuarioId);
        for (EvaluacionPsicometrica ev : evaluaciones) {
            ev.setUsuario(null);
            ev.setEstado(EvaluacionPsicometrica.EstadoEvaluacion.CANCELADA_ARCO);
            evaluacionRepository.save(ev);
        }

        // 2. Desactivar y anonimizar datos identificables del usuario
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario != null) {
            usuario.setNombreCompleto("USUARIO_ANONIMIZADO_ARCO");
            usuario.setCodigoUniversitario("ARCO-" + usuario.getId());
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        }
    }
}
