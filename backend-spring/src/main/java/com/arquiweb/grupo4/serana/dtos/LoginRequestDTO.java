package com.arquiweb.grupo4.serana.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "Debe proporcionar un email valido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacia")
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static LoginRequestDTOBuilder builder() {
        return new LoginRequestDTOBuilder();
    }

    public static class LoginRequestDTOBuilder {
        private String email;
        private String password;

        public LoginRequestDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public LoginRequestDTOBuilder password(String password) {
            this.password = password;
            return this;
        }

        public LoginRequestDTO build() {
            return new LoginRequestDTO(email, password);
        }
    }
}
