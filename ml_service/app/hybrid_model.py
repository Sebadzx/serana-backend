import os
import json
import time
import joblib
import numpy as np
import pandas as pd
from typing import List, Dict, Any

from app.schemas import (
    InferenciaRequest,
    InferenciaResponse,
    IndicadoresPLN,
    FeatureImportanceItem,
)
from app.nlp_preprocessor import NLPPreprocessor
from app.expert_rules import ExpertRulesEngine

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MODEL_FILE = os.path.join(BASE_DIR, "model.joblib")
FEATURE_NAMES_FILE = os.path.join(BASE_DIR, "feature_names.json")

CLASES = ["BAJO", "MODERADO", "ALTO"]

DESCRIPCION_FACTORES = {
    "Age": "Edad del estudiante",
    "Semester": "Semestre académico en curso",
    "Sex": "Sexo biológico",
    "Family_Expression": "Clima de comunicación familiar",
    "Family_Activities": "Actividades compartidas en familia",
    "Mobile_Use_Rules": "Reglas familiares sobre uso de pantalla",
    "Phone_Discrimination": "Sentimiento de discriminación o exclusión digital",
    "Phone_Related_Arguments": "Conflictos familiares por tiempo de uso",
    "Type_MD": "Tipo de dispositivo móvil predominante",
    "Average_Time_Use": "Tiempo promedio de exposición diaria a pantallas",
    "Using_Text Messaging_Apps": "Uso de mensajería instantánea (WhatsApp)",
    "Using_Games_Apps": "Uso de videojuegos móviles",
    "Using_social media_Apps": "Uso intensivo de redes sociales",
    "Using_Music_Apps": "Uso de aplicaciones de música",
    "Using_Videos_Apps": "Consumo de videos y streaming",
    "Using_Ttime-management_Apps": "Uso de herramientas de gestión del tiempo",
    "Using_Camara_Apps": "Uso de cámara y fotografía",
    "Using_Mail_Apps": "Uso de correo electrónico académico",
    "TMD_Q1": "Llamadas de atención por uso excesivo",
    "TMD_Q2": "Dificultad para respetar límites de gasto/tiempo",
    "TMD_Q3": "Discusiones familiares por tiempo en el móvil",
    "TMD_Q4": "Inversión involuntaria de mayor tiempo en mensajes",
    "TMD_Q5": "Envío de más de 5 mensajes/cadenas diarias",
    "TMD_Q6": "Pérdida de horas de sueño por uso nocturno",
    "TMD_Q7": "Consumo temporal mayor al planificado",
    "TMD_Q8": "Uso del móvil por aburrimiento o evasión",
    "TMD_Q9": "Uso en situaciones inapropiadas o sociales",
    "TMD_Q10": "Recriminaciones por tiempo dedicado al móvil",
    "TMD_Q11": "Inquietud o impulso de uso tras pausas breves",
    "TMD_Q12": "Incremento progresivo de llamadas/mensajes",
    "TMD_Q13": "Malestar o desasosiego si el móvil se avería",
    "TMD_Q14": "Necesidad creciente de interacción con el móvil",
    "TMD_Q15": "Malestar emocional por no portar el móvil",
    "TMD_Q16": "Pérdida de control para interrumpir el uso",
    "TMD_Q17": "Incremento sostenido de mensajes y chats",
    "TMD_Q18": "Revisión inmediata del móvil al despertar",
    "TMD_Q19": "Mayor demanda de tiempo actual comparado al inicio",
    "TMD_Q20": "Incapacidad percibida de tolerar una semana sin móvil",
    "TMD_Q21": "Uso del móvil motivado por soledad o vacío",
    "TMD_Q22": "Impulso urgente e inmediato de manipulación digital",
}


class HybridInferenceEngine:
    def __init__(self):
        self.model = None
        self.feature_names = []
        self.nlp = NLPPreprocessor()
        self.cargar_o_entrenar_modelo()

    def cargar_o_entrenar_modelo(self):
        if os.path.exists(MODEL_FILE) and os.path.exists(FEATURE_NAMES_FILE):
            with open(FEATURE_NAMES_FILE, "r", encoding="utf-8") as f:
                self.feature_names = json.load(f)
            if len(self.feature_names) == 40:
                self.model = joblib.load(MODEL_FILE)
                return

        from train_model import entrenar_y_guardar
        self.model = entrenar_y_guardar()
        with open(FEATURE_NAMES_FILE, "r", encoding="utf-8") as f:
            self.feature_names = json.load(f)

    def predecir(self, req: InferenciaRequest) -> InferenciaResponse:
        t0 = time.perf_counter()

        # 1. Extracción de respuestas TMD (22 ítems)
        if req.escalas and req.escalas.tmdRespuestas:
            tmd_respuestas = req.escalas.tmdRespuestas
        elif req.tmdRespuestas:
            tmd_respuestas = req.tmdRespuestas
        else:
            tmd_respuestas = [2] * 22

        # 2. Extracción y mapeo de variables sociodemográficas y de hábitos digitales
        hab = req.habitosSocioDemo
        age = getattr(hab, "age", 20) if hab else 20
        semester = getattr(hab, "semester", 5) if hab else 5
        sex = hab.sex if hab else 2

        fam_exp = getattr(hab, "familyExpression", 3) if hab else 3
        fam_act = getattr(hab, "familyActivities", 2) if hab else 2
        reglas_uso = (getattr(hab, "reglasHogarUso", None) or req.reglasHogarUso or 2)
        discrim = (getattr(hab, "percepcionDiscriminacion", None) or req.percepcionDiscriminacion or 1)
        disc_cel = (getattr(hab, "discusionesUsoCelular", None) or req.discusionesUsoCelular or 2)

        tipo_disp = (getattr(hab, "tipoDispositivo", None) or req.tipoDispositivo or 2)
        tiempo_uso = (getattr(hab, "tiempoUsoDiario", None) or req.tiempoUsoDiario or 3)

        app_msj = (getattr(hab, "appMensajeria", None) or req.appMensajeria or 2)
        app_juegos = (getattr(hab, "appVideojuegos", None) or req.appVideojuegos or 1)
        app_redes = (getattr(hab, "appRedesSociales", None) or req.appRedesSociales or 2)
        app_musica = (getattr(hab, "appMusica", None) or req.appMusica or 2)
        app_video = (getattr(hab, "appVideos", None) or req.appVideos or 2)
        app_tiempo = (getattr(hab, "appGestionTiempo", None) or req.appGestionTiempo or 2)
        app_camara = (getattr(hab, "appCamara", None) or req.appCamara or 2)
        app_correo = (getattr(hab, "appCorreo", None) or req.appCorreo or 2)

        # Construcción del diccionario con los 40 predictores canónicos
        fila_dict: Dict[str, Any] = {}
        for idx in range(1, 23):
            val = tmd_respuestas[idx - 1] if idx - 1 < len(tmd_respuestas) else 2
            fila_dict[f"TMD_Q{idx}"] = int(val)

        fila_dict["Age"] = int(age)
        fila_dict["Semester"] = int(semester)
        fila_dict["Sex"] = sex
        fila_dict["Family_Expression"] = int(fam_exp)
        fila_dict["Family_Activities"] = int(fam_act)
        fila_dict["Mobile_Use_Rules"] = int(reglas_uso)
        fila_dict["Phone_Discrimination"] = int(discrim)
        fila_dict["Phone_Related_Arguments"] = int(disc_cel)
        fila_dict["Type_MD"] = int(tipo_disp)
        fila_dict["Average_Time_Use"] = int(tiempo_uso)
        fila_dict["Using_Text Messaging_Apps"] = int(app_msj)
        fila_dict["Using_Games_Apps"] = int(app_juegos)
        fila_dict["Using_social media_Apps"] = int(app_redes)
        fila_dict["Using_Music_Apps"] = int(app_musica)
        fila_dict["Using_Videos_Apps"] = int(app_video)
        fila_dict["Using_Ttime-management_Apps"] = int(app_tiempo)
        fila_dict["Using_Camara_Apps"] = int(app_camara)
        fila_dict["Using_Mail_Apps"] = int(app_correo)

        df_in = pd.DataFrame([fila_dict])[self.feature_names]

        # 3. Inferencia probabilística con Random Forest (Pipeline preprocessor + classifier)
        probas = self.model.predict_proba(df_in)[0]
        rf_class_idx = int(np.argmax(probas))
        pred_rf = CLASES[rf_class_idx]
        prob_rf = round(float(probas[rf_class_idx]), 4)
        probabilidades_rf_dict = {
            CLASES[i]: round(float(probas[i]), 4) for i in range(len(CLASES))
        }

        # 4. Evaluación de la capa de Reglas Clínicas Experimentales (R1, R2, R3)
        nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones = (
            ExpertRulesEngine.evaluar_reglas_clinicas(
                tmd_respuestas=tmd_respuestas,
                habitos_dict=fila_dict,
                rf_class_idx=rf_class_idx,
                probas_rf={clase: float(probas[i]) for i, clase in enumerate(CLASES)},
            )
        )

        # 5. Análisis del discurso libre (PLN dialectal)
        pln_dict = self.nlp.analizar_discurso(req.textoLibre or "")

        # 6. Explicabilidad Feature Importance (Gini del clasificador Random Forest)
        rf_classifier = self.model.named_steps["classifier"]
        importancias = rf_classifier.feature_importances_
        preprocessor = self.model.named_steps["preprocessor"]
        ohe_cols = list(preprocessor.named_transformers_["nominal"].get_feature_names_out(["Sex", "Type_MD"]))
        num_cols = [c for c in self.feature_names if c not in ["Sex", "Type_MD"]]
        encoded_names = ohe_cols + num_cols

        top_indices = np.argsort(importancias)[::-1][:5]
        top_features: List[FeatureImportanceItem] = []
        for idx in top_indices:
            fname = encoded_names[idx] if idx < len(encoded_names) else f"Factor_{idx}"
            desc = DESCRIPCION_FACTORES.get(fname, fname)
            top_features.append(
                FeatureImportanceItem(
                    factor=desc,
                    peso=round(float(importancias[idx]), 4),
                )
            )

        t1 = time.perf_counter()
        tiempo_ms = round((t1 - t0) * 1000.0, 2)

        justificacion = (
            f"Clasificación Random Forest: {pred_rf} ({prob_rf*100:.1f}%). "
            + (f"Modificado por regla clínica {regla_disparada} a {nivel_final}." if modificado_por_regla
               else "Sin activación de reglas clínicas modificadoras.")
        )

        return InferenciaResponse(
            evaluacionId=req.evaluacionId,
            nivelAnsiedad=nivel_final,
            nivelAnsiedadPredicho=nivel_final,
            prediccionRandomForest=pred_rf,
            probabilidadesRandomForest=probabilidades_rf_dict,
            probabilidadPrediccionRF=prob_rf,
            scoreProbabilidad=prob_rf,
            modificadoPorRegla=modificado_por_regla,
            reglaClinicaDisparada=regla_disparada,
            alertaCritica=alerta_critica,
            esCasoCritico=alerta_critica,
            probabilidadRiesgo=prob_rf,
            justificacionCli=justificacion,
            justificacionExclusion=justificacion,
            indicadoresPLN=IndicadoresPLN(
                polaridadSentimiento=pln_dict["polaridadSentimiento"],
                conteoTerminosArgot=pln_dict["conteoTerminosArgot"],
                terminosDetectados=pln_dict["terminosDetectados"],
                densidadEstres=pln_dict["densidadEstres"],
                longitudPalabras=pln_dict["longitudPalabras"],
            ),
            estresoresDetectados=pln_dict.get("terminosDetectados", []),
            polaridadSentimiento=pln_dict.get("polaridadSentimiento", 0.0),
            featureImportance=top_features,
            recomendacionesICBT=recomendaciones,
            tiempoInferenciaMs=tiempo_ms,
        )
