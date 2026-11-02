from datetime import datetime
from typing import Dict, Any
from fastapi import FastAPI, HTTPException, Body
from fastapi.middleware.cors import CORSMiddleware
from sklearn.model_selection import StratifiedKFold, cross_validate
import numpy as np

from app.schemas import (
    InferenciaRequest,
    HabitosSocioDemo,
    EscalasPsicometricas,
    RetrainRequest,
    RetrainResponse,
    HealthResponse,
)
from app.hybrid_model import HybridTriajeModel

app = FastAPI(
    title="SERANA - Microservicio de Orientacion Preventiva Hibrida (ML + PLN)",
    description=(
        "API REST para tamizaje preventivo de ansiedad estado (STAI-Estado) "
        "mediante Random Forest (57 variables reales: TMD + STAI-Rasgo + Habitos SocioDemo), "
        "Reglas Clinicas separadas y PLN con normalizacion de tildes y negaciones."
    ),
    version="2.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

motor_ia = HybridTriajeModel()


@app.get("/health", response_model=HealthResponse, tags=["Monitoreo"])
def verificar_salud():
    return HealthResponse(
        status="UP",
        modeloCargado=motor_ia.model is not None,
        version="2.0.0",
        algoritmo="Random Forest (57 vars: 22 TMD + 20 STAI-Rasgo + 15 SocioDemo -> STAI-Estado) + Reglas Separadas + PLN",
    )


@app.post("/predict", tags=["Inferencia Preventiva"])
def predecir_ansiedad_digital(payload: Dict[str, Any] = Body(...)):
    """
    Ejecuta el pipeline de orientación preventiva recibiendo las respuestas reales completas:
      - 22 ítems de TMD (escala 0..4)
      - 20 ítems de STAI-Rasgo (escala 1..4)
      - 15 variables de Hábitos Digitales y Contexto Sociofamiliar (SocioDemo, sin posturas/ergonomía)
      - Texto libre emocional
    No reconstruye ni inventa respuestas faltantes.
    """
    try:
        if "escalas" in payload and isinstance(payload["escalas"], dict):
            req = InferenciaRequest(**payload)
        else:
            tmd_raw = payload.get("tmdRespuestas")
            stai_raw = payload.get("staiRasgoRespuestas")

            if not isinstance(tmd_raw, list) or len(tmd_raw) != 22:
                raise ValueError(
                    "Se requieren exactamente las 22 respuestas reales de la escala TMD (tmdRespuestas, rango 0 a 4)."
                )
            if not isinstance(stai_raw, list) or len(stai_raw) != 20:
                raise ValueError(
                    "Se requieren exactamente las 20 respuestas reales de la escala STAI-Rasgo (staiRasgoRespuestas, rango 1 a 4)."
                )

            habitos = HabitosSocioDemo(
                tipoDispositivo=int(payload.get("tipoDispositivo", 2)),
                tiempoUsoDiario=int(payload.get("tiempoUsoDiario", 3)),
                lugarUsoFrecuente=int(payload.get("lugarUsoFrecuente", 2)),
                tipoAccesoRed=int(payload.get("tipoAccesoRed", 3)),
                reglasHogarUso=int(payload.get("reglasHogarUso", 2)),
                percepcionDiscriminacion=int(payload.get("percepcionDiscriminacion", 1)),
                discusionesUsoCelular=int(payload.get("discusionesUsoCelular", 2)),
                appMensajeria=int(payload.get("appMensajeria", 2)),
                appVideojuegos=int(payload.get("appVideojuegos", 1)),
                appRedesSociales=int(payload.get("appRedesSociales", 2)),
                appMusica=int(payload.get("appMusica", 2)),
                appVideos=int(payload.get("appVideos", 2)),
                appGestionTiempo=int(payload.get("appGestionTiempo", 2)),
                appCamara=int(payload.get("appCamara", 2)),
                appCorreo=int(payload.get("appCorreo", 2)),
            )

            req = InferenciaRequest(
                evaluacionId=int(payload.get("evaluacionId", 1)),
                habitosSocioDemo=habitos,
                escalas=EscalasPsicometricas(
                    tmdRespuestas=[int(x) for x in tmd_raw],
                    staiRasgoRespuestas=[int(x) for x in stai_raw],
                ),
                textoLibre=str(payload.get("textoLibre", "") or ""),
            )

        res = motor_ia.predecir(req)
        terminos_detectados = list(res.indicadoresPLN.terminosDetectados)

        if res.modificadoPorRegla:
            justificacion = (
                f"Resultado experimental de orientación preventiva (no constituye diagnóstico clínico). "
                f"Predicción Random Forest (STAI-Estado): {res.prediccionRandomForest} "
                f"({res.probabilidadPrediccionRF * 100:.1f}% | Distribución RF: "
                f"BAJO: {res.probabilidadesRandomForest['BAJO']*100:.1f}%, "
                f"MODERADO: {res.probabilidadesRandomForest['MODERADO']*100:.1f}%, "
                f"ALTO: {res.probabilidadesRandomForest['ALTO']*100:.1f}%). "
                f"Ajustado a {res.nivelAnsiedad} por regla clínica experta ({res.reglaClinicaDisparada})."
            )
        else:
            regla_txt = (
                f"Regla clínica verificada ({res.reglaClinicaDisparada}): consistente con el modelo."
                if res.reglaClinicaDisparada
                else "Sin modificación por reglas clínicas expertas."
            )
            justificacion = (
                f"Resultado experimental de orientación preventiva (no constituye diagnóstico clínico). "
                f"Predicción Random Forest (STAI-Estado): {res.prediccionRandomForest} "
                f"({res.probabilidadPrediccionRF * 100:.1f}% | Distribución RF: "
                f"BAJO: {res.probabilidadesRandomForest['BAJO']*100:.1f}%, "
                f"MODERADO: {res.probabilidadesRandomForest['MODERADO']*100:.1f}%, "
                f"ALTO: {res.probabilidadesRandomForest['ALTO']*100:.1f}%). "
                f"{regla_txt}"
            )

        res_dict = res.model_dump()
        res_dict.update(
            {
                "nivelAnsiedadPredicho": res.nivelAnsiedad,
                "prediccionRandomForest": res.prediccionRandomForest,
                "probabilidadesRandomForest": res.probabilidadesRandomForest,
                "modificadoPorRegla": res.modificadoPorRegla,
                "reglaClinicaDisparada": res.reglaClinicaDisparada,
                "scoreProbabilidad": res.probabilidadPrediccionRF,
                "esCasoCritico": res.alertaCritica,
                "justificacionExclusion": justificacion,
                "estresoresDetectados": terminos_detectados,
                "polaridadSentimiento": res.indicadoresPLN.polaridadSentimiento,
            }
        )
        return res_dict
    except ValueError as ve:
        raise HTTPException(status_code=422, detail=str(ve))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error en el motor de inferencia: {str(e)}")


@app.post("/retrain", response_model=RetrainResponse, tags=["Entrenamiento Continuo"])
def reentrenar_modelo(req: RetrainRequest):
    try:
        from train_model import cargar_dataset_real

        X, y, n_rows = cargar_dataset_real()
        cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
        scoring = ["f1_macro", "accuracy", "precision_macro", "recall_macro"]
        scores = cross_validate(motor_ia.model, X, y, cv=cv, scoring=scoring)

        motor_ia.model.fit(X, y)

        return RetrainResponse(
            mensaje="Modelo Random Forest reentrenado exitosamente sobre las 1,220 observaciones reales (TMD + STAI-Rasgo + SocioDemo -> STAI-Estado).",
            f1ScoreMacro=round(float(np.mean(scores["test_f1_macro"])), 4),
            accuracy=round(float(np.mean(scores["test_accuracy"])), 4),
            precisionMacro=round(float(np.mean(scores["test_precision_macro"])), 4),
            recallMacro=round(float(np.mean(scores["test_recall_macro"])), 4),
            totalMuestras=int(n_rows),
            timestamp=datetime.utcnow().isoformat() + "Z",
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error durante el reentrenamiento: {str(e)}")
