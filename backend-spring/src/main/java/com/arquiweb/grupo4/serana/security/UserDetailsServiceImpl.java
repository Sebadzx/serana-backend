package com.arquiweb.grupo4.serana.security;

import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseGet(() -> usuarioRepository.findByCodigoUniversitario(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email/codigo: " + username)));

        return UserDetailsImpl.build(usuario);
    }
}
