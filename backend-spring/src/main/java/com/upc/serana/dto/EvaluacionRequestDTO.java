package com.upc.serana.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class EvaluacionRequestDTO {

    /**
     * Issue 7: Validación de rango [1-5] en cada ítem de la escala psicométrica.
     * Antes solo se validaba tamaño de lista; ahora cada valor es validado
     * en el servidor Java (no solo en el esquema Pydantic del ML service).
     */
    @NotEmpty(message = "Las respuestas de TS4US son obligatorias")
    @Size(min = 13, max = 13, message = "La escala TS4US requiere exactamente 13 respuestas")
    @Valid
    private List<@Min(value = 1, message = "Cada respuesta TS4US debe ser mínimo 1")
                 @Max(value = 5, message = "Cada respuesta TS4US debe ser máximo 5") Integer> ts4usRespuestas;

    @NotEmpty(message = "Las respuestas de ASAIDAS son obligatorias")
    @Size(min = 8, max = 8, message = "La escala ASAIDAS requiere exactamente 8 respuestas")
    @Valid
    private List<@Min(value = 1, message = "Cada respuesta ASAIDAS debe ser mínimo 1")
                 @Max(value = 5, message = "Cada respuesta ASAIDAS debe ser máximo 5") Integer> asaidasRespuestas;

    @NotNull(message = "Las horas de pantalla diaria son obligatorias")
    @DecimalMin(value = "0.0", message = "Las horas de pantalla no pueden ser negativas")
    @DecimalMax(value = "24.0", message = "Las horas de pantalla no pueden superar 24h")
    private Double horasPantallaDia;

    @DecimalMin(value = "0.0", message = "Las horas en redes sociales no pueden ser negativas")
    @DecimalMax(value = "24.0", message = "Las horas en redes sociales no pueden superar 24h")
    private Double horasRedesSociales;

    @DecimalMin(value = "0.0", message = "Las horas de estudio virtual no pueden ser negativas")
    @DecimalMax(value = "24.0", message = "Las horas de estudio virtual no pueden superar 24h")
    private Double horasEstudioVirtual;

    @NotNull(message = "El dispositivo principal es obligatorio")
    @Pattern(regexp = "LAPTOP|SMARTPHONE|DESKTOP|TABLET",
             message = "El dispositivo debe ser: LAPTOP, SMARTPHONE, DESKTOP o TABLET")
    private String dispositivoPrincipal;

    // Texto libre emocional: opcional pero acotado para evitar payloads excessivos
    @Size(max = 3000, message = "El texto libre no puede superar los 3000 caracteres")
    private String textoLibre;
}

