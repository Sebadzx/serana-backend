import datetime
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Request, status
from fastapi.middleware.cors import CORSMiddleware

from app.schemas import (
    InferenciaRequest, InferenciaResponse,
    RetrainRequest, RetrainResponse,
    HealthResponse, EscalasPsicometricas, PerfilEstudiante
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


@app.post("/api/v1/predict", tags=["Inferencia"])
async def predecir_ansiedad(req: Request):
    modelo = req.app.state.modelo_hibrido
    if not modelo or not modelo.model:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="El modelo predictivo no se encuentra cargado."
        )
    try:
        body = await req.json()

        # Caso 1: Viene con la estructura completa InferenciaRequest
        if "escalas" in body:
            inf_request = InferenciaRequest(**body)
        else:
            # Caso 2: Viene con el payload plano del backend Spring Boot
            eval_id = int(body.get("evaluacion_id", body.get("evaluacionId", 1)))
            gad7 = int(body.get("puntuacion_gad7", 10))
            pss10 = int(body.get("puntuacion_pss10", 20))
            pantalla = float(body.get("tiempo_pantalla_horas", 8.0))
            sueno = float(body.get("habito_sueno_horas", 6.0))
            texto = str(body.get("texto_libre", ""))
            carrera = str(body.get("carrera", "Ingenieria de Sistemas de Informacion"))
            ciclo = int(body.get("ciclo", 6))

            val_ts = min(5, max(1, round(pss10 / 8.0)))
            val_as = min(5, max(1, round(gad7 / 4.2)))

            escalas = EscalasPsicometricas(
                ts4usRespuestas=[val_ts] * 13,
                asaidasRespuestas=[val_as] * 8
            )
            perfil = PerfilEstudiante(
                carrera=carrera,
                ciclo=ciclo,
                horasPantallaDia=pantalla,
                horasRedesSociales=min(pantalla * 0.4, 4.0),
                horasEstudioVirtual=min(pantalla * 0.6, 6.0),
                dispositivoPrincipal="LAPTOP"
            )
            inf_request = InferenciaRequest(
                evaluacionId=eval_id,
                perfilEstudiante=perfil,
                escalas=escalas,
                textoLibre=texto
            )

        resultado = modelo.predecir(inf_request)

        # Retornar formato compatible tanto con Spring Boot como con clientes REST directos
        res_dict = resultado.model_dump()
        res_dict["nivel_ansiedad_predicho"] = resultado.nivelAnsiedad
        res_dict["score_probabilidad"] = resultado.probabilidadRiesgo
        res_dict["es_caso_critico"] = resultado.alertaCritica
        res_dict["justificacion_cli"] = (
            f"Inferencia Híbrida IA (Random Forest + PLN + Reglas Clínicas). "
            f"Nivel: {resultado.nivelAnsiedad}, Confianza: {round(resultado.probabilidadRiesgo * 100)}%. "
            f"{resultado.reglaClinicaDisparada or 'Evaluación multimodal completada'}."
        )
        res_dict["estresores_detectados"] = (
            resultado.indicadoresPLN.terminosDetectados
            if resultado.indicadoresPLN.terminosDetectados
            else ["sobrecarga_virtual"]
        )
        res_dict["polaridad_sentimiento"] = resultado.indicadoresPLN.polaridadSentimiento

        return res_dict
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

