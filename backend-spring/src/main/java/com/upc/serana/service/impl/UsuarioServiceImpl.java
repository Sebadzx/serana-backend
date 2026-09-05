package com.upc.serana.service.impl;

import com.upc.serana.dto.JwtResponseDTO;
import com.upc.serana.dto.LoginRequestDTO;
import com.upc.serana.dto.RegistroEstudianteDTO;
import com.upc.serana.entity.Rol;
import com.upc.serana.entity.Usuario;
import com.upc.serana.repository.ConsentimientoRepository;
import com.upc.serana.repository.UsuarioRepository;
import com.upc.serana.security.JwtTokenProvider;
import com.upc.serana.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ConsentimientoRepository consentimientoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public JwtResponseDTO autenticar(LoginRequestDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generarToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + loginDTO.getEmail()));

        boolean tieneConsentimiento = consentimientoRepository.existsByUsuarioIdAndAceptadoTrue(usuario.getId());

        return JwtResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol().name())
                .tieneConsentimiento(tieneConsentimiento)
                .build();
    }

    @Override
    @Transactional
    public Usuario registrarEstudiante(RegistroEstudianteDTO registroDTO) {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new IllegalArgumentException("El correo electronico ya se encuentra registrado.");
        }

        Usuario usuario = Usuario.builder()
                .email(registroDTO.getEmail())
                .password(passwordEncoder.encode(registroDTO.getPassword()))
                .nombreCompleto(registroDTO.getNombreCompleto())
                .codigoUniversitario(registroDTO.getCodigoUniversitario())
                .facultad(registroDTO.getFacultad())
                .carrera(registroDTO.getCarrera())
                .ciclo(registroDTO.getCiclo())
                .campus(registroDTO.getCampus())
                .rol(Rol.ROLE_ESTUDIANTE)
                .activo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No existe una sesion autenticada activa.");
        }
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
