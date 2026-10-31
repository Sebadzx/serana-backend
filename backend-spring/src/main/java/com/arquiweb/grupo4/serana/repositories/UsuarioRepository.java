package com.arquiweb.grupo4.serana.repositories;

import com.arquiweb.grupo4.serana.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCodigoUniversitario(String codigoUniversitario);
    boolean existsByEmail(String email);
    boolean existsByCodigoUniversitario(String codigoUniversitario);
}
