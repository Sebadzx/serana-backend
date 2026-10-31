package com.arquiweb.grupo4.serana;

import com.arquiweb.grupo4.serana.entities.Authority;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.repositories.AuthorityRepository;
import com.arquiweb.grupo4.serana.repositories.UsuarioRepository;
import com.arquiweb.grupo4.serana.services.ConsentimientoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class SeranaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeranaApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            AuthorityRepository authorityRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder encoder,
            ConsentimientoService consentimientoService) {
        return args -> {
            // 1. Inicializar Autoridades / Roles
            Authority rolEstudiante = authorityRepository.findByName("ROLE_ESTUDIANTE")
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_ESTUDIANTE").build()));

            Authority rolBienestar = authorityRepository.findByName("ROLE_BIENESTAR")
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_BIENESTAR").build()));

            Authority rolAdmin = authorityRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_ADMIN").build()));

            // 2. Inicializar Usuario Estudiante Demo (US-01, US-02) con Consentimiento Informado (US-03)
            Usuario estudiante = usuarioRepository.findByEmail("estudiante@upc.edu.pe").orElse(null);
            if (estudiante == null) {
                estudiante = usuarioRepository.save(Usuario.builder()
                        .codigoUniversitario("u20261001")
                        .nombreCompleto("Juan Perez Estudiante")
                        .email("estudiante@upc.edu.pe")
                        .password(encoder.encode("123456"))
                        .enabled(true)
                        .authorities(List.of(rolEstudiante))
                        .build());
            }

            if (!consentimientoService.tieneConsentimientoVigente(estudiante.getId())) {
                consentimientoService.registrarConsentimiento(estudiante, true, "127.0.0.1", "1.0-Ley29733");
            }

            // 3. Inicializar Usuario Bienestar Psicológico Demo
            if (!usuarioRepository.existsByEmail("bienestar@upc.edu.pe")) {
                usuarioRepository.save(Usuario.builder()
                        .codigoUniversitario("u20261002")
                        .nombreCompleto("Dra. Maria Psicologa Bienestar")
                        .email("bienestar@upc.edu.pe")
                        .password(encoder.encode("123456"))
                        .enabled(true)
                        .authorities(List.of(rolBienestar))
                        .build());
            }

            // 4. Inicializar Usuario Admin Demo
            if (!usuarioRepository.existsByEmail("admin@upc.edu.pe")) {
                usuarioRepository.save(Usuario.builder()
                        .codigoUniversitario("u20261003")
                        .nombreCompleto("Administrador Sistema SERANA")
                        .email("admin@upc.edu.pe")
                        .password(encoder.encode("123456"))
                        .enabled(true)
                        .authorities(List.of(rolAdmin))
                        .build());
            }
        };
    }
}
