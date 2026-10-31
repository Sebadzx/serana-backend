package com.arquiweb.grupo4.serana.controllers;

import com.arquiweb.grupo4.serana.dtos.JwtResponseDTO;
import com.arquiweb.grupo4.serana.dtos.LoginRequestDTO;
import com.arquiweb.grupo4.serana.dtos.RegistroEstudianteDTO;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO loginDTO) {
        JwtResponseDTO response = usuarioService.autenticar(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarEstudiante(@Valid @RequestBody RegistroEstudianteDTO registroDTO) {
        Usuario usuario = usuarioService.registrarEstudiante(registroDTO);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }
}
