package com.upc.serana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String tipo;
    private Long id;
    private String email;
    private String nombreCompleto;
    private String rol;
    private boolean tieneConsentimiento;
}
