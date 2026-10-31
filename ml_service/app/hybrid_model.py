import os
import time
import json
import numpy as np
import joblib
from typing import Dict, List

from app.schemas import InferenciaRequest, InferenciaResponse, IndicadoresPLN, FeatureImportanceItem
from app.nlp_preprocessor import NLPPreprocessor
from app.expert_rules import ExpertRulesEngine

BASE_DIR = os.path.dirname(os.path.dirname(__file__))
MODEL_FILE = os.path.join(BASE_DIR, "model.joblib")
FEATURE_NAMES_FILE = os.path.join(BASE_DIR, "feature_names.json")

CLASES = ["BAJO", "MODERADO", "ALTO"]
DISPOSITIVO_MAP = {"SMARTPHONE": 1.0, "LAPTOP": 2.0, "DESKTOP": 3.0, "TABLET": 4.0}

DESCRIPCION_FACTORES = {
    "TS4US_1_SobrecargaInfo": "Sobrecarga de informacion digital y tareas virtuales",
    "TS4US_2_InvasionVidaPrivada": "Invasion del tiempo personal por demandas digitales",
    "TS4US_3_ComplejidadSistemas": "Dificultad y fatiga ante plataformas universitarias",
    "TS4US_4_InseguridadDigital": "Inseguridad y estres ante fallas tecnicas",
    "TS4US_5_IncertidumbreTecnologica": "Incertidumbre por cambios continuos en herramientas",
    "TS4US_6_FatigaPantalla": "Fatiga visual y agotamiento fisico frente a pantallas",
    "TS4US_7_PresionConectividad": "Presion constante de respuesta inmediata",
    "TS4US_8_MultitareaVirtual": "Sobrecarga por multitarea academica virtual",
    "TS4US_9_ObsolescenciaHabilidades": "Temor a quedar desactualizado tecnologicamente",
    "TS4US_10_DependenciaPlataformas": "Dependencia excesiva a plataformas de la institucion",
    "TS4US_11_SaturacionTareas": "Saturacion de entregas virtuales continuas",
    "TS4US_12_MiedoDesconexion": "Miedo a perder eventos o avisos al desconectarse",
    "TS4US_13_FaltaControlDigital": "Sensacion de perdida de control del tiempo digital",
    "ASAIDAS_1_UsoCompulsivoIA": "Uso compulsivo de herramientas de Inteligencia Artificial",
    "ASAIDAS_2_AnsiedadEvaluaciones": "Ansiedad ante examenes sin soporte tecnologico",
    "ASAIDAS_3_DelegacionCognitiva": "Delegacion cognitiva excesiva en modelos generativos",
    "ASAIDAS_4_MiedoFalloAlgoritmico": "Temor al fallo o sesgo en sistemas inteligentes",
    "ASAIDAS_5_PerdidaAutonomia": "Perdida de autonomia en el razonamiento critico",
    "ASAIDAS_6_DudaCapacidadPropia": "Duda en la propia capacidad academica (baja autoeficacia)",
    "ASAIDAS_7_SaturacionNotificaciones": "Saturacion emocional por notificaciones constantes",
    "ASAIDAS_8_ComparacionDigital": "Comparacion social nociva en entornos digitales",
    "Habito_HorasPantallaDia": "Exposicion diaria prolongada frente a pantallas",
    "Habito_HorasRedesSociales": "Alto tiempo de uso en redes sociales no academicas",
    "Habito_HorasEstudioVirtual": "Jornada excesiva de estudio virtual sin pausas",
    "Habito_SuenoHoras": "Horas de sueno y descanso nocturno",
    "Habito_DispositivoPrincipal": "Tipo de dispositivo tecnologico predominante",
    "PLN_PolaridadSentimiento": "Tono emocional en el discurso del estudiante",
    "PLN_ScorePonderadoEstres": "Presencia de terminos de estres/tecnoestres",
    "PLN_DensidadEstres": "Concentracion de expresiones de agotamiento en el texto"
}


class HybridAnxietyModel:
    """
    Núcleo del modelo híbrido de orientación preventiva:
    - Random Forest (29 variables: 13 TS4US + 8 ASAIDAS + 5 Hábitos + 3 PLN)
    - Procesamiento de Lenguaje Natural con normalización de tildes y manejo de negaciones
    - Motor de Reglas Clínicas Expertas registrado separadamente de las probabilidades del RF
    """

    def __init__(self):
        self.nlp = NLPPreprocessor()
        self.rules = ExpertRulesEngine()
        self.model = None
        self.feature_names: List[str] = []
        self._cargar_o_entrenar()

    def _cargar_o_entrenar(self):
        if os.path.exists(FEATURE_NAMES_FILE):
            with open(FEATURE_NAMES_FILE, "r", encoding="utf-8") as f:
                self.feature_names = json.load(f)

        if not os.path.exists(MODEL_FILE) or len(self.feature_names) != 29:
            print("Entrenando modelo Random Forest con 29 variables y desenlace STAI-Estado...")
            from train_model import entrenar_y_evaluar_modelo
            entrenar_y_evaluar_modelo()
            with open(FEATURE_NAMES_FILE, "r", encoding="utf-8") as f:
                self.feature_names = json.load(f)

        self.model = joblib.load(MODEL_FILE)
        print("Modelo Random Forest cargado exitosamente.")

    def predecir(self, request: InferenciaRequest) -> InferenciaResponse:
        t0 = time.time()

        # 1. Análisis de PLN sobre el texto libre emocional
        indicadores_pln_dict = self.nlp.analizar_discurso(request.textoLibre or "")
        vector_pln = self.nlp.extraer_vector_pln(request.textoLibre or "")

        # 2. Construcción del vector multimodal real (29 características)
        disp_num = DISPOSITIVO_MAP.get(
            request.perfilEstudiante.dispositivoPrincipal.upper(),
            2.0
        )

        vector_habitos = [
            request.perfilEstudiante.horasPantallaDia,
            request.perfilEstudiante.horasRedesSociales,
            request.perfilEstudiante.horasEstudioVirtual,
            request.perfilEstudiante.habitoSuenoHoras,
            disp_num
        ]

        vector_completo = (
            request.escalas.ts4usRespuestas
            + request.escalas.asaidasRespuestas
            + vector_habitos
            + vector_pln
        )

        X_eval = np.array([vector_completo], dtype=float)

        # 3. Predicción probabilística pura con Random Forest
        probabilidades = self.model.predict_proba(X_eval)[0]
        probs_por_clase: Dict[str, float] = {
            CLASES[i]: round(float(probabilidades[i]), 4) for i in range(len(CLASES))
        }
        clase_idx = int(np.argmax(probabilidades))
        prediccion_rf = CLASES[clase_idx]
        probabilidad_rf = probs_por_clase[prediccion_rf]

        # 4. Evaluación separada de la Capa de Reglas Clínicas Expertas
        (
            nivel_final,
            modificado_por_regla,
            alerta_critica,
            regla_disparada,
            recomendaciones_icbt
        ) = self.rules.evaluar_reglas_clinicas(
            ts4us_respuestas=request.escalas.ts4usRespuestas,
            asaidas_respuestas=request.escalas.asaidasRespuestas,
            horas_pantalla=request.perfilEstudiante.horasPantallaDia,
            habito_sueno_horas=request.perfilEstudiante.habitoSuenoHoras,
            dispositivo=request.perfilEstudiante.dispositivoPrincipal,
            indicadores_pln=indicadores_pln_dict,
            prediccion_rf=prediccion_rf
        )

        # 5. Cálculo de Feature Importance Local
        global_importances = self.model.feature_importances_
        local_weights = []
        for i, (nombre, val) in enumerate(zip(self.feature_names, vector_completo)):
            val_norm = val / 5.0 if val <= 5.0 else min(val / 14.0, 1.0)
            impacto = global_importances[i] * max(val_norm, 0.1)
            desc = DESCRIPCION_FACTORES.get(nombre, nombre)
            local_weights.append((desc, impacto))

        local_weights.sort(key=lambda item: item[1], reverse=True)
        suma_top = sum(item[1] for item in local_weights[:4]) or 1.0

        top_factores = [
            FeatureImportanceItem(
                factor=item[0],
                peso=round(float(item[1] / suma_top), 3)
            )
            for item in local_weights[:4]
        ]

        tiempo_ms = round((time.time() - t0) * 1000, 2)

        return InferenciaResponse(
            evaluacionId=request.evaluacionId,
            nivelAnsiedad=nivel_final,
            prediccionRandomForest=prediccion_rf,
            probabilidadesRandomForest=probs_por_clase,
            probabilidadPrediccionRF=probabilidad_rf,
            modificadoPorRegla=modificado_por_regla,
            reglaClinicaDisparada=regla_disparada,
            alertaCritica=alerta_critica,
            probabilidadRiesgo=probabilidad_rf,
            indicadoresPLN=IndicadoresPLN(**indicadores_pln_dict),
            featureImportance=top_factores,
            recomendacionesICBT=recomendaciones_icbt,
            tiempoInferenciaMs=tiempo_ms
        )
