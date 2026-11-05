from typing import Dict, List, Optional, Tuple, Any
import numpy as np


class ExpertRulesEngine:
    """
    Capa de Reglas Clínicas Experimentales (R1, R2 y R3) unificada entre el Paper,
    el Notebook de experimentación (ml_service.ipynb) y el Microservicio de Inferencia SERANA.
    
    Reglas aplicadas sobre las predicciones probabilísticas del Random Forest (40 predictores):
      - R1: Sensibilización de Clase Moderada en frontera de incertidumbre con alta exposición.
      - R2: Escalamiento a Clase Alta por hiperconectividad, pérdida de control y baja cohesión familiar.
      - R3: Atenuación a Clase Baja por ausencia de dependencia TMD (<=29) y entorno familiar protector.
    """

    @staticmethod
    def evaluar_reglas_clinicas(
        tmd_respuestas: List[int],
        habitos_dict: Dict[str, Any],
        rf_class_idx: int,
        probas_rf: Dict[str, float]
    ) -> Tuple[str, bool, bool, Optional[str], List[str]]:
        """
        Aplica las tres reglas clínicas experimentales sobre la salida del Random Forest.
        Retorna:
            (nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones_icbt)
        """
        clases = ["BAJO", "MODERADO", "ALTO"]
        p_bajo = probas_rf.get("BAJO", 0.0)
        p_mod = probas_rf.get("MODERADO", 0.0)
        p_alto = probas_rf.get("ALTO", 0.0)
        p_max = probas_rf.get(clases[rf_class_idx], 0.0)

        tmd_sum = sum(tmd_respuestas)
        q3_conflicto = tmd_respuestas[2] if len(tmd_respuestas) >= 3 else 0  # Q3
        q16_control = tmd_respuestas[15] if len(tmd_respuestas) >= 16 else 0  # Q16

        avg_time = int(habitos_dict.get("Average_Time_Use", habitos_dict.get("tiempoUsoDiario", 3)))
        fam_act = int(habitos_dict.get("Family_Activities", 2))
        fam_exp = int(habitos_dict.get("Family_Expression", 3))
        soc_apps = int(habitos_dict.get("Using_social media_Apps", habitos_dict.get("appRedesSociales", 2)))

        nivel_idx = rf_class_idx
        regla_disparada = None

        # Regla 1: Sensibilización a Moderado (1) ante frontera de incertidumbre y alta exposición diaria (>=3h/día)
        if rf_class_idx != 1 and p_mod >= 0.32 and (p_max - p_mod) <= 0.045 and avg_time >= 3 and fam_act <= 3:
            nivel_idx = 1
            regla_disparada = "R1_TRANSICION_MODERADA_ALTA_EXPOSICION"

        # Regla 2: Escalamiento a Alto (2) por hiperconectividad, pérdida de control y baja cohesión familiar
        elif rf_class_idx == 1 and p_alto >= 0.37 and avg_time >= 3 and soc_apps == 2 and (q3_conflicto >= 2 or q16_control >= 3) and fam_act <= 2:
            nivel_idx = 2
            regla_disparada = "R2_ESCALAMIENTO_ALTO_HIPERCONECTIVIDAD"

        # Regla 3: Atenuación a Bajo (0) por ausencia de dependencia TMD (<=29) y entorno familiar protector
        elif rf_class_idx == 1 and p_bajo >= 0.37 and fam_exp >= 3 and fam_act >= 3 and tmd_sum <= 29:
            nivel_idx = 0
            regla_disparada = "R3_FACTOR_PROTECTOR_FAMILIAR_BAJA_DEPENDENCIA"

        nivel_final = clases[nivel_idx]
        modificado_por_regla = (nivel_idx != rf_class_idx)
        alerta_critica = (nivel_final == "ALTO")

        recomendaciones = ExpertRulesEngine._generar_recomendaciones(
            nivel_final=nivel_final,
            tmd_sum=tmd_sum,
            avg_time=avg_time,
            q16_control=q16_control,
            alerta_critica=alerta_critica,
            regla_disparada=regla_disparada
        )

        return nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones

    @staticmethod
    def _generar_recomendaciones(
        nivel_final: str,
        tmd_sum: int,
        avg_time: int,
        q16_control: int,
        alerta_critica: bool,
        regla_disparada: Optional[str]
    ) -> List[str]:
        pautas = []
        if alerta_critica or nivel_final == "ALTO":
            pautas.append(
                "ORIENTACIÓN PRIORITARIA: Se sugiere solicitar una sesión de acompañamiento psicoeducativo con el Servicio de Tutoría y Bienestar Universitario."
            )
            pautas.append(
                "TCC Digital - Regulación Emocional: Practica respiración diafragmática consciente (técnica 4-7-8) durante 5 minutos antes de iniciar sesiones académicas exigentes."
            )

        if tmd_sum >= 39 or avg_time >= 3:
            pautas.append(
                "Higiene Digital: Establece bloques de desconexión nocturna al menos 45 minutos antes de dormir para reducir la activación neurocognitiva."
            )
            pautas.append(
                "Gestión de Estímulos: Desactiva notificaciones push innecesarias durante las horas de estudio concentrado y utiliza el modo 'No Molestar'."
            )

        if q16_control >= 3:
            pautas.append(
                "Técnica de Demora de Impulso (TCC): Cuando sientas el impulso automático de desbloquear el móvil, espera 2 minutos realizando una respiración profunda antes de interactuar con la pantalla."
            )

        if nivel_final == "MODERADO":
            pautas.append(
                "Pausas Activas: Aplica la regla 20-20-20 (cada 20 minutos de pantalla, mira un punto a 6 metros durante 20 segundos) para prevenir fatiga y reactividad emocional."
            )
        elif nivel_final == "BAJO":
            pautas.append(
                "Pauta de Mantenimiento: Tus patrones de uso del dispositivo móvil muestran un nivel saludable y controlado. Mantén tus hábitos de interacción consciente."
            )

        return pautas
