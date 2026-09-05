package com.upc.serana.controller;

import com.upc.serana.dto.SolicitudARCODTO;
import com.upc.serana.entity.SolicitudARCO;
import com.upc.serana.service.ArcoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/privacidad")
@RequiredArgsConstructor
public class ArcoController {

    private final ArcoService arcoService;

    @PostMapping("/solicitud-arco")
    public ResponseEntity<?> registrarSolicitud(@Valid @RequestBody SolicitudARCODTO dto) {
        SolicitudARCO sol = arcoService.registrarSolicitud(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "solicitudId", sol.getId(),
                        "tipoDerecho", sol.getTipoDerecho().name(),
                        "estado", sol.getEstado().name(),
                        "mensaje", "Solicitud ARCO tramitada en cumplimiento de la Ley N° 29733 de Proteccion de Datos Personales del Peru."
                )
        );
    }
}
