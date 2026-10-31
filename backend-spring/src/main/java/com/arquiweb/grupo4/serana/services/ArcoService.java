package com.arquiweb.grupo4.serana.services;

import com.arquiweb.grupo4.serana.dtos.RespuestaARCORequestDTO;
import com.arquiweb.grupo4.serana.dtos.SolicitudARCORequestDTO;
import com.arquiweb.grupo4.serana.dtos.SolicitudARCOResponseDTO;
import com.arquiweb.grupo4.serana.entities.Usuario;

import java.util.List;

public interface ArcoService {
    SolicitudARCOResponseDTO crearSolicitud(SolicitudARCORequestDTO dto, Usuario usuario);
    List<SolicitudARCOResponseDTO> listarMisSolicitudes(Usuario usuario);
    List<SolicitudARCOResponseDTO> listarTodasLasSolicitudes();
    SolicitudARCOResponseDTO responderSolicitud(Long id, RespuestaARCORequestDTO dto);
}
