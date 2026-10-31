package com.arquiweb.grupo4.serana.controllers;

import com.arquiweb.grupo4.serana.dtos.RespuestaARCORequestDTO;
import com.arquiweb.grupo4.serana.dtos.SolicitudARCORequestDTO;
import com.arquiweb.grupo4.serana.dtos.SolicitudARCOResponseDTO;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.services.ArcoService;
import com.arquiweb.grupo4.serana.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arco")
@CrossOrigin(origins = "*")
public class ArcoController {

    private final ArcoService arcoService;
    private final UsuarioService usuarioService;

    public ArcoController(ArcoService arcoService, UsuarioService usuarioService) {
        this.arcoService = arcoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/solicitudes")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<SolicitudARCOResponseDTO> crearSolicitud(@Valid @RequestBody SolicitudARCORequestDTO dto) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        SolicitudARCOResponseDTO response = arcoService.crearSolicitud(dto, usuario);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<List<SolicitudARCOResponseDTO>> listarMisSolicitudes() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        List<SolicitudARCOResponseDTO> lista = arcoService.listarMisSolicitudes(usuario);
        return ResponseEntity.ok(lista);
    }

    @GetMapping({"/admin/solicitudes", "/admin/solicitude"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SolicitudARCOResponseDTO>> listarTodasLasSolicitudes() {
        List<SolicitudARCOResponseDTO> lista = arcoService.listarTodasLasSolicitudes();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/admin/solicitudes/{id}/responder")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SolicitudARCOResponseDTO> responderSolicitud(
            @PathVariable Long id,
            @Valid @RequestBody RespuestaARCORequestDTO dto) {
        SolicitudARCOResponseDTO response = arcoService.responderSolicitud(id, dto);
        return ResponseEntity.ok(response);
    }
}
