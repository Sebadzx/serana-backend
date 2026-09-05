package com.upc.serana.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

/**
 * Issue 7: DTO de progreso parcial. Los campos son opcionales (se envían solo
 * los que el estudiante completó en ese paso del wizard), pero cuando están
 * presentes se validan sus rangos en el servidor.
 */
@Data
public class GuardarProgresoDTO {

    @Valid
    @Size(min = 13, max = 13, message = "La escala TS4US requiere exactamente 13 respuestas si se envía")
    private List<@Min(value = 1, message = "Cada respuesta TS4US debe ser mínimo 1")
                 @Max(value = 5, message = "Cada respuesta TS4US debe ser máximo 5") Integer> ts4usRespuestas;

    @Valid
    @Size(min = 8, max = 8, message = "La escala ASAIDAS requiere exactamente 8 respuestas si se envía")
    private List<@Min(value = 1, message = "Cada respuesta ASAIDAS debe ser mínimo 1")
                 @Max(value = 5, message = "Cada respuesta ASAIDAS debe ser máximo 5") Integer> asaidasRespuestas;

    @DecimalMin(value = "0.0", message = "Las horas de pantalla no pueden ser negativas")
    @DecimalMax(value = "24.0", message = "Las horas de pantalla no pueden superar 24h")
    private Double horasPantallaDia;

    @DecimalMin(value = "0.0", message = "Las horas en redes sociales no pueden ser negativas")
    @DecimalMax(value = "24.0", message = "Las horas en redes sociales no pueden superar 24h")
    private Double horasRedesSociales;

    @DecimalMin(value = "0.0", message = "Las horas de estudio virtual no pueden ser negativas")
    @DecimalMax(value = "24.0", message = "Las horas de estudio virtual no pueden superar 24h")
    private Double horasEstudioVirtual;

    @Pattern(regexp = "LAPTOP|SMARTPHONE|DESKTOP|TABLET",
             message = "El dispositivo debe ser: LAPTOP, SMARTPHONE, DESKTOP o TABLET")
    private String dispositivoPrincipal;

    @Size(max = 3000, message = "El texto libre no puede superar los 3000 caracteres")
    private String textoLibre;
}

