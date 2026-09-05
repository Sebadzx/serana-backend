from typing import Dict, List, Optional, Tuple

class ExpertRulesEngine:
    """
    Capa de Reglas Clinicas Expertas.
    Diseñada con criterios psicologicos para la deteccion temprana de ansiedad
    digital y tecnoestres en estudiantes universitarios de Lima Metropolitana.
    """

    @staticmethod
    def evaluar_reglas_clinicas(
        ts4us_respuestas: List[int],
        asaidas_respuestas: List[int],
        horas_pantalla: float,
        dispositivo: str,
        indicadores_pln: Dict[str, any],
        prediccion_rf: str,
        probabilidad_rf: float
    ) -> Tuple[str, float, bool, Optional[str], List[str]]:
        """
        Aplica reglas de negocio clinico sobre la salida del Random Forest.
        Retorna:
            (nivel_final, probabilidad_final, alerta_critica, regla_disparada, recomendaciones_icbt)
        """
        # Calcular sumatorias y promedios psicometricos
        promedio_ts4us = sum(ts4us_respuestas) / len(ts4us_respuestas)
        promedio_asaidas = sum(asaidas_respuestas) / len(asaidas_respuestas)
        conteo_extremos_ts4us = sum(1 for r in ts4us_respuestas if r >= 4)
        
        polaridad = indicadores_pln.get("polaridadSentimiento", 0.0)
        terminos = indicadores_pln.get("terminosDetectados", [])
        
        nivel_final = prediccion_rf
        prob_final = probabilidad_rf
        alerta_critica = False
        regla_disparada = None
        
        # Palabras de riesgo psicologico severo
        palabras_alerta = {"panico", "angustia", "colapso", "no doy mas", "llanto", "desesperado", "desesperada"}
        hay_termino_critico = any(term in palabras_alerta for term in terminos)

        # REGLA 1: Crisis Aguda y Saturacion Extrema (Alerta Roja)
        # Si mas de 9 de los 13 items de TS4US estan en nivel 4 o 5 Y hay discurso critico
        if (conteo_extremos_ts4us >= 8 and hay_termino_critico) or (promedio_ts4us >= 4.2 and polaridad <= -0.5):
            nivel_final = "ALTO"
            prob_final = max(prob_final, 0.92)
            alerta_critica = True
            regla_disparada = "REGLA_1_SATURACION_EXTREMA_Y_CRISIS_DISCURSIVA"

        # REGLA 2: Hiperconectividad Nocturna y Alta Dependencia de IA
        # Horas de pantalla extremas (>10h) y puntajes altos en ASAIDAS
        elif horas_pantalla >= 10.0 and promedio_asaidas >= 4.0:
            if nivel_final == "BAJO":
                nivel_final = "MODERADO"
                prob_final = 0.65
            alerta_critica = (nivel_final == "ALTO")
            regla_disparada = "REGLA_2_HIPERCONECTIVIDAD_Y_DEPENDENCIA_IA"

        # REGLA 3: Discrepancia Protectora por Alta Autoeficacia
        # Si el modelo predice ALTO pero el estudiante reporta bajos estresores en TS4US (<2.5) y discurso positivo
        elif nivel_final == "ALTO" and promedio_ts4us <= 2.2 and polaridad >= 0.2:
            nivel_final = "MODERADO"
            prob_final = 0.55
            regla_disparada = "REGLA_3_AJUSTE_DISCREPANCIA_AUTOEFICACIA_PROTECTORA"

        # Generar recomendaciones iCBT personalizadas segun los factores detectados
        recomendaciones = ExpertRulesEngine._generar_recomendaciones(
            nivel_final,
            promedio_ts4us,
            promedio_asaidas,
            horas_pantalla,
            alerta_critica
        )

        return nivel_final, prob_final, alerta_critica, regla_disparada, recomendaciones

    @staticmethod
    def _generar_recomendaciones(
        nivel: str,
        prom_ts4us: float,
        prom_asaidas: float,
        horas_pantalla: float,
        alerta_critica: bool
    ) -> List[str]:
        pautas = []

        if alerta_critica:
            pautas.append(
                "URGENTE: Se recomienda acudir de manera prioritaria al Departamento de Bienestar Universitario "
                "o solicitar una sesion de contencion psicopedagogica en linea."
            )

        if nivel == "ALTO":
            pautas.append(
                "TCC Digital (iCBT): Practica de reestructuracion cognitiva mediante registro diario de pensamientos "
                "automaticos vinculados a la autoexigencia y el miedo a reprobar asignaturas."
            )
            pautas.append(
                "Higiene Digital: Implementa bloques rigurosos de desconexion 'Deep Work' sin notificaciones "
                "durante periodos de estudio virtual."
            )
            pautas.append(
                "Regulacion Fisiologica: Ejercicios de respiracion diafragmatica 4-7-8 al inicio y cierre "
                "de cada sesion frente al computador."
            )
        elif nivel == "MODERADO":
            pautas.append(
                "Gestion del Tecnoestres: Regla 20-20-20 (cada 20 minutos mirar a 20 pies de distancia por 20 segundos) "
                "para mitigar la fatiga cognitiva y visual."
            )
            pautas.append(
                "Estrategia de Autoeficacia: Descomposicion de tareas complejas en metas de micro-aprendizaje "
                "para fortalecer la percepcion de control academico."
            )
            if horas_pantalla > 8.0:
                pautas.append(
                    "Limites de Conectividad: Establecer toque de queda digital apagando pantallas al menos 45 minutos "
                    "antes de acostarse para proteger la arquitectura del sueño."
                )
        else: # BAJO
            pautas.append(
                "Mantenimiento Proactivo: Continua aplicando tus estrategias actuales de organizacion y autocontrol digital."
            )
            pautas.append(
                "Higiene Mental Preventiva: Integra pausas activas y actividades recreativas desconectadas "
                "para preservar tu autoeficacia academica."
            )

        if prom_asaidas >= 3.5:
            pautas.append(
                "Uso Critico de IA: Fomenta el uso de herramientas generativas como asistentes de lluvia de ideas, "
                "evitando la dependencia delegativa que incrementa la ansiedad ante evaluaciones presenciales."
            )

        return pautas
