package com.upc.serana.controller;

import com.upc.serana.dto.EvaluacionRequestDTO;
import com.upc.serana.dto.EvaluacionResponseDTO;
import com.upc.serana.dto.GuardarProgresoDTO;
import com.upc.serana.service.EvaluacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    @PostMapping("/iniciar")
    public ResponseEntity<EvaluacionResponseDTO> iniciarEvaluacion() {
        EvaluacionResponseDTO response = evaluacionService.iniciarEvaluacion();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/guardar-progreso")
    public ResponseEntity<EvaluacionResponseDTO> guardarProgreso(
            @PathVariable("id") Long id,
            @Valid @RequestBody GuardarProgresoDTO progresoDTO) {
        EvaluacionResponseDTO response = evaluacionService.guardarProgreso(id, progresoDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<EvaluacionResponseDTO> completarEvaluacion(
            @PathVariable("id") Long id,
            @Valid @RequestBody EvaluacionRequestDTO evaluacionDTO) {
        EvaluacionResponseDTO response = evaluacionService.completarEvaluacion(id, evaluacionDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resultado")
    public ResponseEntity<EvaluacionResponseDTO> obtenerResultado(@PathVariable("id") Long id) {
        EvaluacionResponseDTO response = evaluacionService.obtenerResultadoPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mis-evaluaciones")
    public ResponseEntity<List<EvaluacionResponseDTO>> obtenerMisEvaluaciones() {
        List<EvaluacionResponseDTO> response = evaluacionService.obtenerMisEvaluaciones();
        return ResponseEntity.ok(response);
    }
}
