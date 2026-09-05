package com.upc.serana.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo institucional invalido")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;
}
