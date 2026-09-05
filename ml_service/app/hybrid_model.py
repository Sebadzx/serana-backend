import os
import time
import json
import numpy as np
import joblib
from typing import Dict, List, Any

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
    "Habito_DispositivoPrincipal": "Tipo de dispositivo tecnologico predominante",
    "PLN_PolaridadSentimiento": "Tono emocional negativo en el discurso del estudiante",
    "PLN_ScorePonderadoEstres": "Presencia recurrente de terminos de estres/tecnoestres",
    "PLN_DensidadEstres": "Concentracion de modismos y quejas de agotamiento limeño"
}

class HybridAnxietyModel:
    """
    Nucleo del modelo hibrido:
    - Random Forest (Bagging de arboles de decision)
    - Procesamiento de Lenguaje Natural para el discurso libre
    - Motor de Reglas Clinicas Expertas
    """

    def __init__(self):
        self.nlp = NLPPreprocessor()
        self.rules = ExpertRulesEngine()
        self.model = None
        self.feature_names = []
        self._cargar_o_entrenar()

    def _cargar_o_entrenar(self):
        if not os.path.exists(MODEL_FILE):
            print("Artefacto model.joblib no encontrado. Entrenando modelo inicial...")
            from train_model import entrenar_y_evaluar_modelo
            entrenar_y_evaluar_modelo()
            
        self.model = joblib.load(MODEL_FILE)
        if os.path.exists(FEATURE_NAMES_FILE):
            with open(FEATURE_NAMES_FILE, "r", encoding="utf-8") as f:
                self.feature_names = json.load(f)
        print("Modelo Random Forest cargado exitosamente.")

    def predecir(self, request: InferenciaRequest) -> InferenciaResponse:
        t0 = time.time()
        
        # 1. Analisis de PLN sobre el texto libre emocional
        indicadores_pln_dict = self.nlp.analizar_discurso(request.textoLibre or "")
        vector_pln = self.nlp.extraer_vector_pln(request.textoLibre or "")
        
        # 2. Construccion del vector multimodal denso (28 caracteristicas)
        disp_num = DISPOSITIVO_MAP.get(
            request.perfilEstudiante.dispositivoPrincipal.upper(),
            2.0 # Default LAPTOP
        )
        
        vector_habitos = [
            request.perfilEstudiante.horasPantallaDia,
            request.perfilEstudiante.horasRedesSociales,
            request.perfilEstudiante.horasEstudioVirtual,
            disp_num
        ]
        
        vector_completo = (
            request.escalas.ts4usRespuestas +
            request.escalas.asaidasRespuestas +
            vector_habitos +
            vector_pln
        )
        
        X_eval = np.array([vector_completo])
        
        # 3. Prediccion probabilistica con Random Forest
        probabilidades = self.model.predict_proba(X_eval)[0]
        clase_idx = int(np.argmax(probabilidades))
        prediccion_rf = CLASES[clase_idx]
        probabilidad_rf = round(float(probabilidades[clase_idx]), 4)
        
        # 4. Evaluacion de la Capa de Reglas Clinicas Expertas
        (
            nivel_final,
            prob_final,
            alerta_critica,
            regla_disparada,
            recomendaciones_icbt
        ) = self.rules.evaluar_reglas_clinicas(
            ts4us_respuestas=request.escalas.ts4usRespuestas,
            asaidas_respuestas=request.escalas.asaidasRespuestas,
            horas_pantalla=request.perfilEstudiante.horasPantallaDia,
            dispositivo=request.perfilEstudiante.dispositivoPrincipal,
            indicadores_pln=indicadores_pln_dict,
            prediccion_rf=prediccion_rf,
            probabilidad_rf=probabilidad_rf
        )
        
        # 5. Calculo de Feature Importance Local (Explicabilidad clinica)
        global_importances = self.model.feature_importances_
        # Ponderar la importancia global por la intensidad de la respuesta del usuario
        local_weights = []
        for i, (nombre, val) in enumerate(zip(self.feature_names, vector_completo)):
            # Normalizar valor relativo a escala 1-5
            val_norm = val / 5.0 if val <= 5.0 else min(val / 14.0, 1.0)
            impacto = global_importances[i] * max(val_norm, 0.1)
            desc = DESCRIPCION_FACTORES.get(nombre, nombre)
            local_weights.append((desc, impacto))
            
        # Ordenar y seleccionar los 4 factores mas determinantes
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
            probabilidadRiesgo=round(prob_final, 3),
            alertaCritica=alerta_critica,
            reglaClinicaDisparada=regla_disparada,
            indicadoresPLN=IndicadoresPLN(**indicadores_pln_dict),
            featureImportance=top_factores,
            recomendacionesICBT=recomendaciones_icbt,
            tiempoInferenciaMs=tiempo_ms
        )
