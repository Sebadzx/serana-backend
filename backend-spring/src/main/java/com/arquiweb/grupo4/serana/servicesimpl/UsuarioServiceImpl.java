package com.arquiweb.grupo4.serana.servicesimpl;

import com.arquiweb.grupo4.serana.dtos.JwtResponseDTO;
import com.arquiweb.grupo4.serana.dtos.LoginRequestDTO;
import com.arquiweb.grupo4.serana.dtos.RegistroEstudianteDTO;
import com.arquiweb.grupo4.serana.entities.Authority;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.exceptions.BadRequestException;
import com.arquiweb.grupo4.serana.repositories.AuthorityRepository;
import com.arquiweb.grupo4.serana.repositories.UsuarioRepository;
import com.arquiweb.grupo4.serana.security.JwtUtils;
import com.arquiweb.grupo4.serana.security.UserDetailsImpl;
import com.arquiweb.grupo4.serana.services.ConsentimientoService;
import com.arquiweb.grupo4.serana.services.UsuarioService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ConsentimientoService consentimientoService;

    public UsuarioServiceImpl(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, ConsentimientoService consentimientoService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.consentimientoService = consentimientoService;
    }

    @Override
    public JwtResponseDTO autenticar(LoginRequestDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JwtResponseDTO.builder()
                .token(jwt)
                .type("Bearer")
                .id(userDetails.getId())
                .codigoUniversitario(userDetails.getCodigoUniversitario())
                .nombreCompleto(userDetails.getNombreCompleto())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public Usuario registrarEstudiante(RegistroEstudianteDTO registroDTO) {
        if (!Boolean.TRUE.equals(registroDTO.getAceptoConsentimiento())) {
            throw new BadRequestException("Debe aceptar el consentimiento informado para registrarse");
        }

        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new BadRequestException("El email ya se encuentra registrado");
        }

        if (usuarioRepository.existsByCodigoUniversitario(registroDTO.getCodigoUniversitario())) {
            throw new BadRequestException("El codigo universitario ya se encuentra registrado");
        }

        Authority rolEstudiante = authorityRepository.findByName("ROLE_ESTUDIANTE")
                .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_ESTUDIANTE").build()));

        List<Authority> authorities = new ArrayList<>();
        authorities.add(rolEstudiante);

        Usuario usuario = Usuario.builder()
                .codigoUniversitario(registroDTO.getCodigoUniversitario())
                .nombreCompleto(registroDTO.getNombreCompleto())
                .email(registroDTO.getEmail())
                .password(passwordEncoder.encode(registroDTO.getPassword()))
                .enabled(true)
                .authorities(authorities)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        consentimientoService.registrarConsentimiento(
                guardado,
                true,
                registroDTO.getIpAddress(),
                registroDTO.getVersionTerminos() != null ? registroDTO.getVersionTerminos() : "1.0"
        );

        return guardado;
    }

    @Override
    public Usuario obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new BadRequestException("No hay usuario autenticado en la sesion");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
    }
}
