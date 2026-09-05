package com.upc.serana.controller;

import com.upc.serana.dto.JwtResponseDTO;
import com.upc.serana.dto.LoginRequestDTO;
import com.upc.serana.dto.RegistroEstudianteDTO;
import com.upc.serana.entity.Usuario;
import com.upc.serana.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        JwtResponseDTO response = usuarioService.autenticar(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroEstudianteDTO registroDTO) {
        Usuario usuario = usuarioService.registrarEstudiante(registroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                java.util.Map.of(
                        "mensaje", "Estudiante registrado exitosamente.",
                        "usuarioId", usuario.getId(),
                        "email", usuario.getEmail()
                )
        );
    }
}
