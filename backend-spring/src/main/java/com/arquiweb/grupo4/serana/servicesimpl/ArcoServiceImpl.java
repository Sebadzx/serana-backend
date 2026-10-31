package com.arquiweb.grupo4.serana.servicesimpl;

import com.arquiweb.grupo4.serana.dtos.RespuestaARCORequestDTO;
import com.arquiweb.grupo4.serana.dtos.SolicitudARCORequestDTO;
import com.arquiweb.grupo4.serana.dtos.SolicitudARCOResponseDTO;
import com.arquiweb.grupo4.serana.entities.SolicitudARCO;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.exceptions.ResourceNotFoundException;
import com.arquiweb.grupo4.serana.repositories.SolicitudARCORepository;
import com.arquiweb.grupo4.serana.services.ArcoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArcoServiceImpl implements ArcoService {

    private final SolicitudARCORepository solicitudARCORepository;
    private final com.arquiweb.grupo4.serana.repositories.EvaluacionPsicometricaRepository evaluacionPsicometricaRepository;
    private final com.arquiweb.grupo4.serana.repositories.ResultadoInferenciaRepository resultadoInferenciaRepository;

    public ArcoServiceImpl(
            SolicitudARCORepository solicitudARCORepository,
            com.arquiweb.grupo4.serana.repositories.EvaluacionPsicometricaRepository evaluacionPsicometricaRepository,
            com.arquiweb.grupo4.serana.repositories.ResultadoInferenciaRepository resultadoInferenciaRepository) {
        this.solicitudARCORepository = solicitudARCORepository;
        this.evaluacionPsicometricaRepository = evaluacionPsicometricaRepository;
        this.resultadoInferenciaRepository = resultadoInferenciaRepository;
    }

    @Override
    @Transactional
    public SolicitudARCOResponseDTO crearSolicitud(SolicitudARCORequestDTO dto, Usuario usuario) {
        SolicitudARCO solicitud = SolicitudARCO.builder()
                .usuario(usuario)
                .tipoSolicitud(dto.getTipoSolicitud().toUpperCase())
                .estado("PENDIENTE")
                .detalleSolicitud(dto.getDetalleSolicitud())
                .fechaSolicitud(LocalDateTime.now())
                .build();

        SolicitudARCO guardada = solicitudARCORepository.save(solicitud);
        return mapToResponseDTO(guardada);
    }

    @Override
    public List<SolicitudARCOResponseDTO> listarMisSolicitudes(Usuario usuario) {
        return solicitudARCORepository.findByUsuarioIdOrderByFechaSolicitudDesc(usuario.getId())
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public List<SolicitudARCOResponseDTO> listarTodasLasSolicitudes() {
        return solicitudARCORepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public SolicitudARCOResponseDTO responderSolicitud(Long id, RespuestaARCORequestDTO dto) {
        SolicitudARCO solicitud = solicitudARCORepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud ARCO no encontrada con ID: " + id));

        solicitud.setEstado(dto.getEstado().toUpperCase());
        solicitud.setRespuestaAdmin(dto.getRespuestaAdmin());
        solicitud.setFechaRespuesta(LocalDateTime.now());

        if ("APROBADA".equalsIgnoreCase(dto.getEstado()) && "CANCELACION".equalsIgnoreCase(solicitud.getTipoSolicitud())) {
            // US-18: Suprimir de forma definitiva las evaluaciones del estudiante (Derecho de Supresión / Olvido)
            Long usuarioId = solicitud.getUsuario().getId();
            List<com.arquiweb.grupo4.serana.entities.EvaluacionPsicometrica> evs = 
                    evaluacionPsicometricaRepository.findByUsuarioIdOrderByFechaEvaluacionDesc(usuarioId);
            for (com.arquiweb.grupo4.serana.entities.EvaluacionPsicometrica ev : evs) {
                resultadoInferenciaRepository.findByEvaluacionId(ev.getId()).ifPresent(resultadoInferenciaRepository::delete);
                evaluacionPsicometricaRepository.delete(ev);
            }
        }

        SolicitudARCO actualizada = solicitudARCORepository.save(solicitud);
        return mapToResponseDTO(actualizada);
    }

    private SolicitudARCOResponseDTO mapToResponseDTO(SolicitudARCO s) {
        return SolicitudARCOResponseDTO.builder()
                .id(s.getId())
                .codigoUsuario(s.getUsuario().getCodigoUniversitario())
                .nombreUsuario(s.getUsuario().getNombreCompleto())
                .tipoSolicitud(s.getTipoSolicitud())
                .estado(s.getEstado())
                .detalleSolicitud(s.getDetalleSolicitud())
                .fechaSolicitud(s.getFechaSolicitud())
                .fechaRespuesta(s.getFechaRespuesta())
                .respuestaAdmin(s.getRespuestaAdmin())
                .build();
    }
}
