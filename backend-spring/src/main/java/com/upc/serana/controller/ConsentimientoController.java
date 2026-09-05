package com.upc.serana.controller;

import com.upc.serana.dto.ConsentimientoRequestDTO;
import com.upc.serana.dto.ConsentimientoResponseDTO;
import com.upc.serana.service.ConsentimientoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consentimiento")
@RequiredArgsConstructor
public class ConsentimientoController {

    private final ConsentimientoService consentimientoService;

    @PostMapping("/firmar")
    public ResponseEntity<ConsentimientoResponseDTO> firmar(
            @Valid @RequestBody ConsentimientoRequestDTO requestDTO,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        ConsentimientoResponseDTO response = consentimientoService.registrarConsentimiento(requestDTO, ip);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estado")
    public ResponseEntity<ConsentimientoResponseDTO> consultarEstado() {
        ConsentimientoResponseDTO response = consentimientoService.consultarEstadoActual();
        return ResponseEntity.ok(response);
    }
}
