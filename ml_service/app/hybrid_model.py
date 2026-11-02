import os
import time
import json
import numpy as np
import joblib
from typing import Dict, List

from app.schemas import InferenciaRequest, InferenciaResponse, IndicadoresPLN, FeatureImportanceItem, HabitosSocioDemo
from app.nlp_preprocessor import NLPPreprocessor
from app.expert_rules import ExpertRulesEngine

BASE_DIR = os.path.dirname(os.path.dirname(__file__))
MODEL_FILE = os.path.join(BASE_DIR, "model.joblib")
FEATURE_NAMES_FILE = os.path.join(BASE_DIR, "feature_names.json")

CLASES = ["BAJO", "MODERADO", "ALTO"]

DESCRIPCION_FACTORES = {
    "TMD_Q1_LlamadasAtencionUso": "Advertencias recibidas por uso intensivo del celular (TMD-Q1)",
    "TMD_Q2_ExcesoLimiteGasto": "Exceso del limite de consumo mensual en el celular (TMD-Q2)",
    "TMD_Q3_DiscusionesCosto": "Discusiones familiares por costos del celular (TMD-Q3)",
    "TMD_Q4_MasTiempoMensajes": "Tiempo excesivo en llamadas, WhatsApp o chats (TMD-Q4)",
    "TMD_Q5_MasCincoMensajesDia": "Alta frecuencia diaria de envio de mensajes (TMD-Q5)",
    "TMD_Q6_DormirMenosPorCelular": "Acostarse tarde o dormir menos por usar el celular (TMD-Q6)",
    "TMD_Q7_GastoMayorPrevisto": "Gasto mayor al planificado en servicios moviles (TMD-Q7)",
    "TMD_Q8_UsoPorAburrimiento": "Recurrir al celular ante el aburrimiento (TMD-Q8)",
    "TMD_Q9_UsoSituacionesInapropiadas": "Uso del celular en situaciones inapropiadas (TMD-Q9)",
    "TMD_Q10_CriticasPorTiempoUso": "Criticas por el tiempo invertido en el celular (TMD-Q10)",
    "TMD_Q11_NecesidadUsarTrasPausa": "Urgencia por revisar mensajes tras un periodo sin uso (TMD-Q11)",
    "TMD_Q12_IncrementoLlamadas": "Incremento progresivo de llamadas y contacto movil (TMD-Q12)",
    "TMD_Q13_MalestarSiSeAveria": "Malestar intenso si el celular se averiara (TMD-Q13)",
    "TMD_Q14_NecesidadCrecienteUso": "Necesidad creciente de utilizar el celular cada vez mas (TMD-Q14)",
    "TMD_Q15_MalestarSinCelular": "Malestar emocional al no tener el celular consigo (TMD-Q15)",
    "TMD_Q16_NoPoderDejarDeUsarlo": "Dificultad para detener el uso cuando tiene el celular en mano (TMD-Q16)",
    "TMD_Q17_IncrementoMensajesWhatsApp": "Incremento sostenido de mensajes de WhatsApp y chats (TMD-Q17)",
    "TMD_Q18_RevisarAlDespertar": "Revisar llamadas o WhatsApp apenas se despierta (TMD-Q18)",
    "TMD_Q19_MayorGastoActual": "Incremento del gasto actual respecto al inicial (TMD-Q19)",
    "TMD_Q20_IncapazSemanaSinCelular": "Incapacidad percibida de pasar una semana sin celular (TMD-Q20)",
    "TMD_Q21_LlamarMensajearPorSoledad": "Uso del celular o WhatsApp ante sentimientos de soledad (TMD-Q21)",
    "TMD_Q22_ImpulsoUsarAhoraMismo": "Impulso inmediato de tomar el celular y enviar mensajes (TMD-Q22)",
    "STAI_R21_MalestarEmocional": "Disminucion de sensacion de bienestar cotidiano (STAI-21)",
    "STAI_R22_NerviosismoInquietud": "Sentimiento de nerviosismo e inquietud (STAI-22)",
    "STAI_R23_InsatisfaccionPersonal": "Baja satisfaccion consigo mismo (STAI-23)",
    "STAI_R24_DeseoFelicidadAjena": "Comparacion con la felicidad percibida en otros (STAI-24)",
    "STAI_R25_SentimientoFracaso": "Sensacion de fracaso ante exigencias (STAI-25)",
    "STAI_R26_FaltaDescanso": "Sensacion de falta de descanso reparador (STAI-26)",
    "STAI_R27_FaltaCalmaSerenidad": "Dificultad para mantener la calma y serenidad (STAI-27)",
    "STAI_R28_AcumulacionDificultades": "Sensacion de que las dificultades se acumulan sin poder superarlas (STAI-28)",
    "STAI_R29_PreocupacionExcesiva": "Preocupacion excesiva por asuntos cotidianos (STAI-29)",
    "STAI_R30_FaltaFelicidad": "Disminucion del estado de animo positivo (STAI-30)",
    "STAI_R31_PensamientosPerturbadores": "Presencia de pensamientos perturbadores recurrentes (STAI-31)",
    "STAI_R32_FaltaAutoconfianza": "Falta de confianza en si mismo (STAI-32)",
    "STAI_R33_InseguridadPersonal": "Disminucion de la sensacion de seguridad personal (STAI-33)",
    "STAI_R34_DificultadDecisiones": "Dificultad para tomar decisiones con facilidad (STAI-34)",
    "STAI_R35_SentimientoIncompetencia": "Sentimiento de insuficiencia ante retos (STAI-35)",
    "STAI_R36_InsatisfaccionCotidiana": "Disminucion de conformidad y tranquilidad general (STAI-36)",
    "STAI_R37_PensamientosIntrusivos": "Pensamientos intrusivos que generan incomodidad (STAI-37)",
    "STAI_R38_RumiacionDecepciones": "Dificultad para apartar decepciones de la mente (STAI-38)",
    "STAI_R39_InestabilidadEmocional": "Menor estabilidad emocional percibida (STAI-39)",
    "STAI_R40_TensionPreocupacionesRecientes": "Estado de tension o agitacion al pensar en preocupaciones recientes (STAI-40)",
    "Habito_TipoDispositivo": "Tipo de dispositivo tecnologico de uso mas frecuente",
    "Habito_TiempoUsoDiario": "Tiempo promedio diario de uso del dispositivo",
    "Habito_LugarUsoFrecuente": "Entorno principal de uso del dispositivo",
    "Habito_TipoAccesoRed": "Modalidad de acceso a internet (datos/Wi-Fi)",
    "Habito_ReglasHogarUso": "Existencia de limites o reglas de horario en el hogar",
    "Habito_PercepcionDiscriminacion": "Presion social por tenencia/uso de celular",
    "Habito_DiscusionesUsoCelular": "Frecuencia de discusiones por tiempo de uso del celular",
    "App_MensajeriaInstantanea": "Uso frecuente de aplicaciones de mensajeria instantanea (WhatsApp)",
    "App_Videojuegos": "Uso frecuente de videojuegos en el dispositivo",
    "App_RedesSociales": "Uso frecuente de aplicaciones de redes sociales",
    "App_Musica": "Uso frecuente de aplicaciones de musica",
    "App_VideosStreaming": "Uso frecuente de aplicaciones de video/streaming",
    "App_GestionTiempoAcademico": "Uso de herramientas de gestion del tiempo (calendario/recordatorios)",
    "App_Camara": "Uso frecuente de camara y fotografia movil",
    "App_CorreoElectronico": "Uso frecuente de correo electronico y academico",
}


class HybridTriajeModel:
    """
    Motor de Inferencia Híbrido entrenado sobre el dataset empírico real (1,220 universitarios):
      1. Random Forest de 57 variables reales (22 TMD + 20 STAI-Rasgo + 15 Hábitos SocioDemo)
         con desenlace independiente STAI-Estado (BAJO, MODERADO, ALTO).
      2. Preprocesador PLN (normalización de tildes + manejo de negaciones).
      3. Capa de Reglas Clínicas Expertas registrada de forma independiente de las probabilidades RF.
    """

    def __init__(self):
        self.model = None
        self.feature_names = []
        self.nlp = NLPPreprocessor()
        self.cargar_o_entrenar_modelo()

    def cargar_o_entrenar_modelo(self):
        if os.path.exists(MODEL_FILE) and os.path.exists(FEATURE_NAMES_FILE):
            with open(FEATURE_NAMES_FILE, "r", encoding="utf-8") as f:
                self.feature_names = json.load(f)
            if len(self.feature_names) == 57:
                self.model = joblib.load(MODEL_FILE)
                return

        from train_model import entrenar_y_guardar
        self.model = entrenar_y_guardar()
        with open(FEATURE_NAMES_FILE, "r", encoding="utf-8") as f:
            self.feature_names = json.load(f)

    def predecir(self, req: InferenciaRequest) -> InferenciaResponse:
        t0 = time.perf_counter()

        # 1. Análisis léxico del discurso libre (con normalización de tildes y negaciones)
        pln_dict = self.nlp.analizar_discurso(req.textoLibre or "")

        # 2. Construcción del vector real de 57 características (sin conversiones simuladas)
        hab = req.habitosSocioDemo if req.habitosSocioDemo else HabitosSocioDemo()
        habitos_vector = [
            float(hab.tipoDispositivo),
            float(hab.tiempoUsoDiario),
            float(hab.lugarUsoFrecuente),
            float(hab.tipoAccesoRed),
            float(hab.reglasHogarUso),
            float(hab.percepcionDiscriminacion),
            float(hab.discusionesUsoCelular),
            float(hab.appMensajeria),
            float(hab.appVideojuegos),
            float(hab.appRedesSociales),
            float(hab.appMusica),
            float(hab.appVideos),
            float(hab.appGestionTiempo),
            float(hab.appCamara),
            float(hab.appCorreo),
        ]

        x_vector = np.array(
            req.escalas.tmdRespuestas
            + req.escalas.staiRasgoRespuestas
            + habitos_vector,
            dtype=float,
        ).reshape(1, -1)

        # 3. Inferencia probabilística exclusiva del Random Forest (desenlace STAI-Estado)
        probas = self.model.predict_proba(x_vector)[0]
        idx_max = int(np.argmax(probas))
        pred_rf = CLASES[idx_max]
        prob_rf = round(float(probas[idx_max]), 4)
        probabilidades_rf_dict = {
            CLASES[i]: round(float(probas[i]), 4) for i in range(len(CLASES))
        }

        # 4. Evaluación de reglas clínicas expertas (separada de las probabilidades del RF)
        nivel_final, modificado_por_regla, alerta_critica, regla_disparada, recomendaciones = (
            ExpertRulesEngine.evaluar_reglas_clinicas(
                tmd_respuestas=req.escalas.tmdRespuestas,
                stai_rasgo_respuestas=req.escalas.staiRasgoRespuestas,
                tiempo_uso_diario=hab.tiempoUsoDiario,
                indicadores_pln=pln_dict,
                prediccion_rf=pred_rf,
            )
        )

        # 5. Explicabilidad local (Feature Importance ponderado por activación de la instancia)
        importancias_globales = self.model.feature_importances_
        x_norm = np.abs(x_vector[0]) / (np.max(np.abs(x_vector[0])) + 1e-6)
        contribucion_local = importancias_globales * (0.6 + 0.4 * x_norm)
        contribucion_local = contribucion_local / (np.sum(contribucion_local) + 1e-9)

        top_indices = np.argsort(contribucion_local)[::-1][:5]
        top_features: List[FeatureImportanceItem] = []
        for idx in top_indices:
            fname = self.feature_names[idx] if idx < len(self.feature_names) else f"Factor_{idx}"
            desc = DESCRIPCION_FACTORES.get(fname, fname)
            top_features.append(
                FeatureImportanceItem(
                    factor=desc,
                    peso=round(float(contribucion_local[idx]), 4),
                )
            )

        t1 = time.perf_counter()
        tiempo_ms = round((t1 - t0) * 1000.0, 2)

        return InferenciaResponse(
            evaluacionId=req.evaluacionId,
            nivelAnsiedad=nivel_final,
            prediccionRandomForest=pred_rf,
            probabilidadesRandomForest=probabilidades_rf_dict,
            probabilidadPrediccionRF=prob_rf,
            modificadoPorRegla=modificado_por_regla,
            reglaClinicaDisparada=regla_disparada,
            alertaCritica=alerta_critica,
            probabilidadRiesgo=prob_rf,
            indicadoresPLN=IndicadoresPLN(
                polaridadSentimiento=pln_dict["polaridadSentimiento"],
                conteoTerminosArgot=pln_dict["conteoTerminosArgot"],
                terminosDetectados=pln_dict["terminosDetectados"],
                densidadEstres=pln_dict["densidadEstres"],
                longitudPalabras=pln_dict["longitudPalabras"],
            ),
            featureImportance=top_features,
            recomendacionesICBT=recomendaciones,
            tiempoInferenciaMs=tiempo_ms,
        )
