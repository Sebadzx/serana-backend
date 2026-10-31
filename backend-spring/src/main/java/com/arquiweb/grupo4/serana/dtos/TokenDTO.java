package com.arquiweb.grupo4.serana.dtos;

import java.util.List;

public class TokenDTO {

    private String jwtToken;
    private String bearer = "Bearer";
    private Long id;
    private String codigoUniversitario;
    private String nombreCompleto;
    private String email;
    private List<String> roles;

    public TokenDTO() {
    }

    public TokenDTO(String jwtToken, String bearer, Long id, String codigoUniversitario, String nombreCompleto, String email, List<String> roles) {
        this.jwtToken = jwtToken;
        this.bearer = bearer != null ? bearer : "Bearer";
        this.id = id;
        this.codigoUniversitario = codigoUniversitario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.roles = roles;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getBearer() {
        return bearer;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoUniversitario() {
        return codigoUniversitario;
    }

    public void setCodigoUniversitario(String codigoUniversitario) {
        this.codigoUniversitario = codigoUniversitario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public static TokenDTOBuilder builder() {
        return new TokenDTOBuilder();
    }

    public static class TokenDTOBuilder {
        private String jwtToken;
        private String bearer = "Bearer";
        private Long id;
        private String codigoUniversitario;
        private String nombreCompleto;
        private String email;
        private List<String> roles;

        public TokenDTOBuilder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public TokenDTOBuilder bearer(String bearer) {
            this.bearer = bearer;
            return this;
        }

        public TokenDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TokenDTOBuilder codigoUniversitario(String codigoUniversitario) {
            this.codigoUniversitario = codigoUniversitario;
            return this;
        }

        public TokenDTOBuilder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public TokenDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public TokenDTOBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public TokenDTO build() {
            return new TokenDTO(jwtToken, bearer, id, codigoUniversitario, nombreCompleto, email, roles);
        }
    }
}
