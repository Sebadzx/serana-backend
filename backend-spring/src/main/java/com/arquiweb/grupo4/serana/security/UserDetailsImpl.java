package com.arquiweb.grupo4.serana.security;

import com.arquiweb.grupo4.serana.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String codigoUniversitario;
    private String nombreCompleto;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String codigoUniversitario, String nombreCompleto, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.codigoUniversitario = codigoUniversitario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Usuario usuario) {
        List<GrantedAuthority> authorities = usuario.getAuthorities().stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getName()))
                .map(GrantedAuthority.class::cast)
                .toList();

        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getCodigoUniversitario(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getCodigoUniversitario() {
        return codigoUniversitario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
