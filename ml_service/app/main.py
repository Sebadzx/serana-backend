from datetime import datetime
from typing import Dict, Any
from fastapi import FastAPI, HTTPException, Body
from fastapi.middleware.cors import CORSMiddleware
import numpy as np

from app.schemas import (
    InferenciaRequest,
    HabitosSocioDemo,
    EscalasPsicometricas,
    RetrainRequest,
    RetrainResponse,
    HealthResponse,
)
from app.hybrid_model import HybridInferenceEngine

app = FastAPI(
    title="SERANA - Microservicio de Orientación Preventiva Híbrida (ML + PLN)",
    description=(
        "API REST para tamizaje preventivo de ansiedad digital (STAI-Estado corregido) "
        "mediante ensamble Random Forest (40 predictores reales: 22 TMD + 18 SocioDemo/Uso Digital), "
        "Capa de Reglas Clínicas Experimentales (R1, R2 y R3) y análisis de discurso libre PLN."
    ),
    version="2.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

motor_ia = HybridInferenceEngine()


@app.get("/health", response_model=HealthResponse, tags=["Monitoreo"])
def verificar_salud():
    return HealthResponse(
        status="UP",
        modeloCargado=motor_ia.model is not None,
        version="2.1.0",
        algoritmo="Random Forest (40 predictores reales -> STAI-Estado corregido) + 3 Reglas Clínicas (R1-R3) + PLN",
        numeroPredictores=len(motor_ia.feature_names),
    )


@app.post("/predict", tags=["Inferencia Preventiva"])
@app.post("/predict_hybrid", tags=["Inferencia Preventiva"])
def predecir_ansiedad_digital(payload: Dict[str, Any] = Body(...)):
    """
    Ejecuta el motor de inferencia híbrida recibiendo los predictores reales:
      - 22 ítems de TMD (escala 0..4)
      - Variables de contexto y hábitos digitales (SocioDemo / Uso de Apps)
      - Texto libre emocional opcional
    Conserva la predicción del Random Forest y registra cualquier modificación por reglas de forma auditable.
    """
    try:
        if "escalas" in payload and isinstance(payload["escalas"], dict):
            req = InferenciaRequest(**payload)
        else:
            tmd_raw = payload.get("tmdRespuestas")
            if tmd_raw is None:
                tmd_raw = payload.get("ts4usRespuestas")

            if not isinstance(tmd_raw, list) or len(tmd_raw) != 22:
                raise ValueError(
                    "Se requieren exactamente las 22 respuestas reales de la escala TMD (tmdRespuestas, rango 0 a 4)."
                )

            stai_raw = payload.get("staiRasgoRespuestas") or payload.get("asaidasRespuestas")

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
                    staiRasgoRespuestas=[int(x) for x in stai_raw] if (stai_raw and isinstance(stai_raw, list)) else None,
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
                f"Ajustado a {res.nivelAnsiedad} por regla clínica ({res.reglaClinicaDisparada})."
            )
        else:
            regla_txt = (
                f"Regla clínica verificada ({res.reglaClinicaDisparada}): consistente con el modelo."
                if res.reglaClinicaDisparada
                else "Sin modificación por reglas clínicas."
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
                "justificacionCli": justificacion,
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
        from train_model import entrenar_y_guardar
        import json
        import os

        pipe = entrenar_y_guardar()
        motor_ia.model = pipe

        metrics_file = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "metrics_summary.json")
        with open(metrics_file, "r", encoding="utf-8") as f:
            summary = json.load(f)

        cv_rf = summary["cv_results"]["RF"]
        test_hyb = summary["test_results"]["hybrid"]

        return RetrainResponse(
            mensaje="Modelo Random Forest (40 predictores reales, 1,220 estudiantes) reentrenado y evaluado exitosamente.",
            f1ScoreMacro=float(test_hyb["f1_macro"]),
            accuracy=float(test_hyb["accuracy"]),
            precisionMacro=float(test_hyb["precision_macro"]),
            recallMacro=float(test_hyb["recall_macro"]),
            totalMuestras=int(summary["dataset_size"]),
            timestamp=datetime.utcnow().isoformat() + "Z",
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error durante el reentrenamiento: {str(e)}")
