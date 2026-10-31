from typing import Dict, List, Optional, Tuple


class ExpertRulesEngine:
    """
    Capa de Reglas Clínicas Expertas.
    Evalúa criterios preventivos sobre las respuestas reales de TS4US, ASAIDAS,
    hábitos digitales (incluyendo sueño) e indicadores de PLN.
    IMPORTANTE: No altera ni inventa porcentajes de probabilidad del Random Forest;
    registra su decisión (modificado_por_regla, regla_disparada, alerta_critica) de forma separada.
    """

    @staticmethod
    def evaluar_reglas_clinicas(
        ts4us_respuestas: List[int],
        asaidas_respuestas: List[int],
        horas_pantalla: float,
        habito_sueno_horas: float,
        dispositivo: str,
        indicadores_pln: Dict[str, any],
        prediccion_rf: str
    ) -> Tuple[str, bool, bool, Optional[str], List[str]]:
        """
        Aplica reglas de orientación preventiva sobre la salida del Random Forest.
        Retorna:
            (nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones_icbt)
        """
        promedio_ts4us = sum(ts4us_respuestas) / len(ts4us_respuestas)
        promedio_asaidas = sum(asaidas_respuestas) / len(asaidas_respuestas)
        conteo_extremos_ts4us = sum(1 for r in ts4us_respuestas if r >= 4)

        polaridad = indicadores_pln.get("polaridadSentimiento", 0.0)
        terminos = indicadores_pln.get("terminosDetectados", [])

        nivel_final = prediccion_rf
        alerta_critica = False
        regla_disparada = None

        palabras_alerta = {
            "panico", "angustia", "colapso", "no doy mas", "llanto", "desesperado", "desesperada"
        }
        hay_termino_critico = any(term in palabras_alerta for term in terminos)

        # REGLA 1: Crisis Aguda y Saturación Extrema (Alerta Preventiva Prioritaria)
        if (conteo_extremos_ts4us >= 8 and hay_termino_critico) or (
            promedio_ts4us >= 4.2 and polaridad <= -0.5
        ):
            nivel_final = "ALTO"
            alerta_critica = True
            regla_disparada = "REGLA_1_SATURACION_EXTREMA_Y_ALERTA_DISCURSIVA"

        # REGLA 2: Hiperconectividad, Privación de Sueño y Alta Dependencia de IA
        elif (horas_pantalla >= 10.0 and promedio_asaidas >= 4.0) or (
            horas_pantalla >= 10.0 and habito_sueno_horas <= 5.0 and promedio_ts4us >= 3.5
        ):
            if nivel_final == "BAJO":
                nivel_final = "MODERADO"
            alerta_critica = (nivel_final == "ALTO")
            regla_disparada = "REGLA_2_HIPERCONECTIVIDAD_PRIVACION_SUENO_Y_DEPENDENCIA_IA"

        # REGLA 3: Ajuste por Baja Sobrecarga Autorreportada y Discurso Positivo
        elif nivel_final == "ALTO" and promedio_ts4us <= 2.2 and polaridad >= 0.2 and not hay_termino_critico:
            nivel_final = "MODERADO"
            regla_disparada = "REGLA_3_AJUSTE_POR_BAJA_SOBRECARGA_Y_DISCURSO_POSITIVO"

        modificado_por_regla = (nivel_final != prediccion_rf)

        recomendaciones = ExpertRulesEngine._generar_recomendaciones(
            nivel_final,
            promedio_ts4us,
            promedio_asaidas,
            horas_pantalla,
            habito_sueno_horas,
            alerta_critica
        )

        return nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones

    @staticmethod
    def _generar_recomendaciones(
        nivel: str,
        prom_ts4us: float,
        prom_asaidas: float,
        horas_pantalla: float,
        habito_sueno_horas: float,
        alerta_critica: bool
    ) -> List[str]:
        pautas = []

        if alerta_critica:
            pautas.append(
                "ORIENTACIÓN PRIORITARIA: Se sugiere acudir al servicio psicopedagógico de Bienestar Universitario "
                "para recibir acompañamiento profesional oportuno."
            )

        if nivel == "ALTO":
            pautas.append(
                "TCC Digital (iCBT): Práctica de reestructuración cognitiva mediante registro diario de pensamientos "
                "automáticos vinculados a la autoexigencia académica."
            )
            pautas.append(
                "Higiene Digital: Implementa bloques de desconexión sin notificaciones durante períodos de estudio."
            )
            pautas.append(
                "Regulación Fisiológica: Ejercicios de respiración diafragmática 4-7-8 al inicio y cierre "
                "de cada sesión frente al computador."
            )
        elif nivel == "MODERADO":
            pautas.append(
                "Gestión del Tecnoestrés: Regla 20-20-20 (cada 20 minutos mirar a 20 pies de distancia por 20 segundos) "
                "para mitigar la fatiga visual y cognitiva."
            )
            pautas.append(
                "Estrategia de Autoeficacia: Descomposición de entregas complejas en metas cortas "
                "para fortalecer la percepción de control académico."
            )
        else:
            pautas.append(
                "Mantenimiento Proactivo: Continúa aplicando tus estrategias actuales de organización y autocontrol digital."
            )
            pautas.append(
                "Higiene Mental Preventiva: Integra pausas activas y actividades recreativas desconectadas."
            )

        if horas_pantalla > 8.0 or habito_sueno_horas < 6.0:
            pautas.append(
                "Higiene del Sueño y Pantallas: Establece una desconexión de pantallas al menos 45 minutos "
                "antes de dormir para recuperar al menos 7 horas de descanso nocturno."
            )

        if prom_asaidas >= 3.5:
            pautas.append(
                "Uso Crítico de IA: Utiliza las herramientas generativas como apoyo de consulta y evita la delegación "
                "total que incrementa la inseguridad ante evaluaciones presenciales."
            )

        return pautas
