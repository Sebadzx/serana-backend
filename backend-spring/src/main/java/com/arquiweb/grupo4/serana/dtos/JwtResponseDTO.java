package com.arquiweb.grupo4.serana.dtos;

import java.util.List;

public class JwtResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String codigoUniversitario;
    private String nombreCompleto;
    private String email;
    private List<String> roles;

    public JwtResponseDTO() {
    }

    public JwtResponseDTO(String token, String type, Long id, String codigoUniversitario, String nombreCompleto, String email, List<String> roles) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
        this.id = id;
        this.codigoUniversitario = codigoUniversitario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public static JwtResponseDTOBuilder builder() {
        return new JwtResponseDTOBuilder();
    }

    public static class JwtResponseDTOBuilder {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String codigoUniversitario;
        private String nombreCompleto;
        private String email;
        private List<String> roles;

        public JwtResponseDTOBuilder token(String token) {
            this.token = token;
            return this;
        }

        public JwtResponseDTOBuilder type(String type) {
            this.type = type;
            return this;
        }

        public JwtResponseDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public JwtResponseDTOBuilder codigoUniversitario(String codigoUniversitario) {
            this.codigoUniversitario = codigoUniversitario;
            return this;
        }

        public JwtResponseDTOBuilder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public JwtResponseDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public JwtResponseDTOBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public JwtResponseDTO build() {
            return new JwtResponseDTO(token, type, id, codigoUniversitario, nombreCompleto, email, roles);
        }
    }
}
