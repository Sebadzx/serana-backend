from typing import Dict, List, Optional, Tuple


class ExpertRulesEngine:
    """
    Capa de Reglas Clínicas Expertas basada en los puntos de corte psicométricos oficiales
    del dataset empírico:
      - TMD (Test of Mobile Dependence, Chóliz 2012, rango 0-88):
          0-29: Ausencia | 30-38: Bajo | 39-48: Medio | 49-88: Alto
      - STAI-Rasgo (Spielberger, rango 20-80):
          20-37: Bajo | 38-44: Moderado | 45-80: Alto
      - Indicadores discursivos de PLN (tildes normalizadas y negaciones controladas).
    IMPORTANTE: No altera ni inventa porcentajes de probabilidad del Random Forest;
    registra su decisión (modificado_por_regla, regla_disparada, alerta_critica) de forma separada.
    """

    @staticmethod
    def evaluar_reglas_clinicas(
        tmd_respuestas: List[int],
        stai_rasgo_respuestas: List[int],
        tiempo_uso_diario: int,
        indicadores_pln: Dict[str, any],
        prediccion_rf: str
    ) -> Tuple[str, bool, bool, Optional[str], List[str]]:
        """
        Aplica reglas de orientación preventiva sobre la salida del Random Forest.
        Retorna:
            (nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones_icbt)
        """
        puntaje_tmd = sum(tmd_respuestas)  # Rango 0..88
        puntaje_stai_rasgo = sum(stai_rasgo_respuestas)  # Rango 20..80
        item_q6_sueno = tmd_respuestas[5] if len(tmd_respuestas) >= 6 else 0  # Q6: dormir menos por el celular (0..4)

        polaridad = indicadores_pln.get("polaridadSentimiento", 0.0)
        terminos = indicadores_pln.get("terminosDetectados", [])

        nivel_final = prediccion_rf
        alerta_critica = False
        regla_disparada = None

        palabras_alerta = {
            "panico", "angustia", "colapso", "no doy mas", "llanto", "desesperado", "desesperada"
        }
        hay_termino_critico = any(term in palabras_alerta for term in terminos)

        # REGLA 1: Dependencia Móvil Moderada/Alta o STAI-Rasgo Elevado + Expresión Discursiva Crítica
        if (
            (puntaje_tmd >= 39 and hay_termino_critico)
            or (puntaje_stai_rasgo >= 45 and hay_termino_critico)
            or (puntaje_stai_rasgo >= 52 and puntaje_tmd >= 49 and polaridad <= -0.4)
        ):
            nivel_final = "ALTO"
            alerta_critica = True
            regla_disparada = "REGLA_1_DEPENDENCIA_ALTA_Y_ALERTA_DISCURSIVA"

        # REGLA 2: Hiperconectividad (>3h diarias), Privación de Sueño por Celular (Q6 >= 3) y Dependencia Media/Alta
        elif (tiempo_uso_diario >= 3 and puntaje_tmd >= 49) or (
            tiempo_uso_diario >= 3 and item_q6_sueno >= 3 and puntaje_stai_rasgo >= 38
        ):
            if nivel_final == "BAJO":
                nivel_final = "MODERADO"
            alerta_critica = (nivel_final == "ALTO")
            regla_disparada = "REGLA_2_HIPERCONECTIVIDAD_Y_ALTERACION_SUENO_TMD"

        # REGLA 3: Ajuste por Ausencia de Dependencia (TMD <= 29), STAI-Rasgo Bajo (<= 37) y Discurso Positivo
        elif (
            nivel_final == "ALTO"
            and puntaje_tmd <= 29
            and puntaje_stai_rasgo <= 37
            and polaridad >= 0.2
            and not hay_termino_critico
        ):
            nivel_final = "MODERADO"
            regla_disparada = "REGLA_3_AJUSTE_POR_BAJA_DEPENDENCIA_Y_DISCURSO_POSITIVO"

        modificado_por_regla = (nivel_final != prediccion_rf)

        recomendaciones = ExpertRulesEngine._generar_recomendaciones(
            nivel_final,
            puntaje_tmd,
            puntaje_stai_rasgo,
            tiempo_uso_diario,
            item_q6_sueno,
            alerta_critica
        )

        return nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones

    @staticmethod
    def _generar_recomendaciones(
        nivel: str,
        puntaje_tmd: int,
        puntaje_stai_rasgo: int,
        tiempo_uso_diario: int,
        item_q6_sueno: int,
        alerta_critica: bool
    ) -> List[str]:
        pautas = []

        if alerta_critica or nivel == "ALTO":
            pautas.append(
                "ORIENTACIÓN PRIORITARIA: Se sugiere agendar una sesión de acompañamiento con el Servicio de Tutoría y Bienestar Estudiantil."
            )
            pautas.append(
                "TCC Digital - Regulación Fisiológica: Practica respiración diafragmática (técnica 4-7-8) durante 5 minutos ante episodios de tensión aguda."
            )

        if puntaje_tmd >= 39 or tiempo_uso_diario >= 3:
            pautas.append(
                "Higiene Digital (Control de Dependencia Móvil - TMD): Establece bloques libres de notificaciones durante tus horas de estudio y desactiva alertas no urgentes."
            )

        if item_q6_sueno >= 2:
            pautas.append(
                "Higiene del Sueño: Evita utilizar el teléfono móvil o revisar mensajes al menos 45 minutos antes de dormir (Ítem TMD-Q6)."
            )

        if puntaje_stai_rasgo >= 38:
            pautas.append(
                "Reestructuración Cognitiva (STAI): Registra las preocupaciones recurrentes del día y distingue entre pendientes accionables y rumiación anticipatoria."
            )

        if len(pautas) == 0:
            pautas.append(
                "Prevención y Bienestar: Mantén tus hábitos actuales de equilibrio entre actividades académicas, descanso y uso moderado de dispositivos."
            )

        return pautas
