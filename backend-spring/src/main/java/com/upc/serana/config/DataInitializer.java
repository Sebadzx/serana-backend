package com.upc.serana.config;

import com.upc.serana.entity.Rol;
import com.upc.serana.entity.Usuario;
import com.upc.serana.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Issue 12: Los emails y contraseñas de los usuarios de prueba se leen
 * desde variables de entorno para evitar credenciales hardcodeadas en código fuente.
 * Definir en .env o como variables de entorno del contenedor.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Issue 12: Credenciales inyectadas desde variables de entorno
    @Value("${serana.student.email:u20201f737@upc.edu.pe}")
    private String studentEmail;

    @Value("${serana.student.password}")
    private String studentPassword;

    @Value("${serana.bienestar.email:bienestar@upc.edu.pe}")
    private String bienestarEmail;

    @Value("${serana.bienestar.password}")
    private String bienestarPassword;

    @Value("${serana.admin.email:admin@serana.upc.edu.pe}")
    private String adminEmail;

    @Value("${serana.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // Inicializar usuarios base para pruebas si no existen
        if (usuarioRepository.count() == 0) {
            // Estudiante de prueba
            Usuario estudiante = Usuario.builder()
                    .email(studentEmail)
                    .password(passwordEncoder.encode(studentPassword))
                    .nombreCompleto("Sebastian Andre Nuñez Alvarez")
                    .codigoUniversitario("U20201F737")
                    .facultad("Facultad de Ingenieria")
                    .carrera("Ingenieria de Sistemas de Informacion")
                    .ciclo(7)
                    .campus("Monterrico")
                    .rol(Rol.ROLE_ESTUDIANTE)
                    .activo(true)
                    .build();
            usuarioRepository.save(estudiante);

            // Profesional de Bienestar Universitario
            Usuario bienestar = Usuario.builder()
                    .email(bienestarEmail)
                    .password(passwordEncoder.encode(bienestarPassword))
                    .nombreCompleto("Dra. Psicologia Bienestar Estudiantil")
                    .codigoUniversitario("DOC-PSI-001")
                    .facultad("Departamento de Bienestar Universitario")
                    .carrera("Psicologia Clinica")
                    .ciclo(null)
                    .campus("Monterrico")
                    .rol(Rol.ROLE_BIENESTAR)
                    .activo(true)
                    .build();
            usuarioRepository.save(bienestar);

            // Administrador del Sistema
            Usuario admin = Usuario.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .nombreCompleto("Administrador Serana TI")
                    .codigoUniversitario("ADM-001")
                    .facultad("Facultad de Ingenieria")
                    .carrera("Ingenieria de Sistemas de Informacion")
                    .ciclo(null)
                    .campus("Monterrico")
                    .rol(Rol.ROLE_ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);

            System.out.println(">>> DATA INITIALIZER: Usuarios base de demostracion sembrados exitosamente.");
        }
    }
}

