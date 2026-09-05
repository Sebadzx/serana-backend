import datetime
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Request, status
from fastapi.middleware.cors import CORSMiddleware

from app.schemas import (
    InferenciaRequest, InferenciaResponse,
    RetrainRequest, RetrainResponse,
    HealthResponse
)
from app.hybrid_model import HybridAnxietyModel


# Issue 8: Migrado de @app.on_event("startup") deprecado a lifespan context manager
# (recomendado desde FastAPI 0.93 / Starlette 0.20).
# Issue 9: El modelo se almacena en app.state en lugar de variable global mutable,
# lo que es thread-safe y compatible con el ciclo de vida de la aplicación.
@asynccontextmanager
async def lifespan(app: FastAPI):
    """Gestiona el ciclo de vida: carga el modelo al inicio y libera recursos al cierre."""
    app.state.modelo_hibrido = HybridAnxietyModel()
    yield
    # Limpieza al apagar el servidor (liberar referencias del modelo)
    app.state.modelo_hibrido = None


app = FastAPI(
    title="Servicio de Machine Learning Hibrido - Deteccion de Ansiedad Digital",
    description="Microservicio predictivo para clasificacion de tecnoestres y ansiedad digital en estudiantes universitarios",
    version="1.0.0",
    lifespan=lifespan  # Issue 8: lifespan reemplaza a @app.on_event
)

# Configuracion de CORS para desarrollo y produccion
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/api/v1/health", response_model=HealthResponse, tags=["Monitoreo"])
def health_check(request: Request):
    # Issue 9: Acceso al modelo a través de app.state en lugar de variable global
    modelo = request.app.state.modelo_hibrido
    modelo_ok = (modelo is not None and modelo.model is not None)
    return HealthResponse(
        status="UP" if modelo_ok else "DEGRADED",
        modeloCargado=modelo_ok,
        version="1.0.0",
        algoritmo="Random Forest + Reglas Clinicas Expertas + PLN Argot Limeno"
    )


@app.post("/api/v1/predict", response_model=InferenciaResponse, tags=["Inferencia"])
def predecir_ansiedad(request: InferenciaRequest, req: Request):
    # Issue 9: Acceso al modelo a través de app.state
    modelo = req.app.state.modelo_hibrido
    if not modelo or not modelo.model:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="El modelo predictivo no se encuentra cargado."
        )
    try:
        resultado = modelo.predecir(request)
        return resultado
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error durante el procesamiento de inferencia: {str(e)}"
        )


@app.post("/api/v1/retrain", response_model=RetrainResponse, tags=["MLOps"])
def reentrenar_modelo(request: RetrainRequest, req: Request):
    """
    Reentrena el modelo Random Forest y recarga el artefacto en memoria.

    Issue 4 — NOTA ACADÉMICA IMPORTANTE:
    El dataset de entrenamiento es **sintético y calibrado** con parámetros estadísticos
    extraídos de investigaciones peruanas (Araya-Ugarte et al. 2025; Solano & Nuñez 2025;
    Morales-Garcia et al. 2025). Las métricas reportadas (F1 ≥ 0.85) se calculan sobre
    datos generados con las mismas distribuciones gaussianas del entrenamiento, lo que puede
    producir sobreestimación del rendimiento real. Para la validación final con datos
    reales de estudiantes UPC, se recomienda una evaluación externa con hold-out set.
    """
    try:
        from train_model import entrenar_y_evaluar_modelo
        metricas = entrenar_y_evaluar_modelo()
        # Issue 9: Recargar modelo en app.state
        modelo = req.app.state.modelo_hibrido
        if modelo:
            modelo._cargar_o_entrenar()

        return RetrainResponse(
            mensaje="Reentrenamiento y validacion cruzada K-Fold completados exitosamente.",
            f1ScoreMacro=round(metricas["f1ScoreMacro"], 4),
            accuracy=round(metricas["accuracy"], 4),
            precisionMacro=round(metricas["precisionMacro"], 4),
            recallMacro=round(metricas["recallMacro"], 4),
            totalMuestras=metricas["totalMuestras"],
            timestamp=datetime.datetime.now(datetime.timezone.utc).isoformat()
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Fallo en el pipeline de reentrenamiento: {str(e)}"
        )

