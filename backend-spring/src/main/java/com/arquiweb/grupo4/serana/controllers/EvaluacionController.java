package com.arquiweb.grupo4.serana.controllers;

import com.arquiweb.grupo4.serana.dtos.EvaluacionRequestDTO;
import com.arquiweb.grupo4.serana.dtos.EvaluacionResponseDTO;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.services.EvaluacionService;
import com.arquiweb.grupo4.serana.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin(origins = "*")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;
    private final UsuarioService usuarioService;

    public EvaluacionController(EvaluacionService evaluacionService, UsuarioService usuarioService) {
        this.evaluacionService = evaluacionService;
        this.usuarioService = usuarioService;
    }

    // Flujo Integral (Todo en uno)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<EvaluacionResponseDTO> registrarEvaluacion(@Valid @RequestBody EvaluacionRequestDTO dto) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        EvaluacionResponseDTO response = evaluacionService.registrarEvaluacion(dto, usuario);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PASO 1 (US-04 y US-06): Cuestionario Psicométrico y Guardado de Progreso
    @PostMapping("/paso1-psicometrico")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> paso1Psicometrico(@RequestBody Map<String, Object> body) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        Map<String, Object> response = evaluacionService.paso1CuestionarioPsicometrico(body, usuario);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PASO 2 (US-05 y US-06): Hábitos Digitales (Pantalla y Sueño)
    @PutMapping("/{id}/paso2-habitos")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> paso2Habitos(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        Map<String, Object> response = evaluacionService.paso2HabitosDigitales(id, body, usuario);
        return ResponseEntity.ok(response);
    }

    // PASO 3 (US-07 y US-10 a US-14): Texto Libre Emocional y Finalización con ML
    @PostMapping("/{id}/paso3-texto-libre")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<EvaluacionResponseDTO> paso3TextoLibre(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        EvaluacionResponseDTO response = evaluacionService.paso3TextoLibreYFinalizar(id, body, usuario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mis-evaluaciones")
    @PreAuthorize("hasAuthority('ROLE_ESTUDIANTE')")
    public ResponseEntity<List<EvaluacionResponseDTO>> listarMisEvaluaciones() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        List<EvaluacionResponseDTO> lista = evaluacionService.listarEvaluacionesUsuario(usuario);
        return ResponseEntity.ok(lista);
    }
}
