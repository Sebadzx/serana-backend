package com.arquiweb.grupo4.serana.controllers;

import com.arquiweb.grupo4.serana.entities.ConsentimientoInformado;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.services.ConsentimientoService;
import com.arquiweb.grupo4.serana.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/consentimiento")
@CrossOrigin(origins = "*")
public class ConsentimientoController {

    private final ConsentimientoService consentimientoService;
    private final UsuarioService usuarioService;

    public ConsentimientoController(ConsentimientoService consentimientoService, UsuarioService usuarioService) {
        this.consentimientoService = consentimientoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/firmar")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> firmarConsentimiento(@RequestBody(required = false) Map<String, Object> body) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        String ipAddress = body != null && body.containsKey("ipAddress") ? String.valueOf(body.get("ipAddress")) : "127.0.0.1";
        String versionTerminos = body != null && body.containsKey("versionTerminos") ? String.valueOf(body.get("versionTerminos")) : "1.0-Ley29733";

        ConsentimientoInformado c = consentimientoService.registrarConsentimiento(usuario, true, ipAddress, versionTerminos);
        Map<String, Object> response = new HashMap<>();
        response.put("id", c.getId());
        response.put("usuarioId", usuario.getId());
        response.put("aceptado", c.isAceptado());
        response.put("fechaAceptacion", c.getFechaAceptacion());
        response.put("ipAddress", c.getIpAddress());
        response.put("versionTerminos", c.getVersionTerminos());
        response.put("mensaje", "Consentimiento informado firmado exitosamente conforme a Ley N° 29733");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estado")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> verificarEstado() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        boolean vigente = consentimientoService.tieneConsentimientoVigente(usuario.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("usuarioId", usuario.getId());
        response.put("consentimientoVigente", vigente);
        return ResponseEntity.ok(response);
    }
}
