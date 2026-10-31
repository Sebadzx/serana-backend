import datetime
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Request, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import ValidationError

from app.schemas import (
    InferenciaRequest, InferenciaResponse,
    RetrainRequest, RetrainResponse,
    HealthResponse, EscalasPsicometricas, PerfilEstudiante
)
from app.hybrid_model import HybridAnxietyModel


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Gestiona el ciclo de vida: carga el modelo al inicio y libera recursos al cierre."""
    app.state.modelo_hibrido = HybridAnxietyModel()
    yield
    app.state.modelo_hibrido = None


app = FastAPI(
    title="Servicio de Machine Learning Hibrido - Orientacion Preventiva de Ansiedad Digital",
    description="Microservicio experimental de tamizaje y orientacion preventiva para tecnoestres y ansiedad digital en estudiantes universitarios",
    version="1.1.0",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health", response_model=HealthResponse, tags=["Monitoreo"])
@app.get("/api/v1/health", response_model=HealthResponse, tags=["Monitoreo"])
def health_check(request: Request):
    modelo = request.app.state.modelo_hibrido
    modelo_ok = (modelo is not None and modelo.model is not None)
    return HealthResponse(
        status="UP" if modelo_ok else "DEGRADED",
        modeloCargado=modelo_ok,
        version="1.1.0",
        algoritmo="Random Forest (29 vars, STAI-Estado) + Reglas Clinicas Separadas + PLN"
    )


@app.post("/predict", tags=["Inferencia"])
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

        # Caso 1: Estructura anidada InferenciaRequest {"escalas": {...}, "perfilEstudiante": {...}}
        if "escalas" in body:
            inf_request = InferenciaRequest(**body)
        else:
            # Caso 2: Payload con respuestas reales enviado desde el backend Spring Boot
            eval_id = int(body.get("evaluacion_id", body.get("evaluacionId", 1)))
            ts4us_raw = body.get("ts4us_respuestas", body.get("ts4usRespuestas"))
            asaidas_raw = body.get("asaidas_respuestas", body.get("asaidasRespuestas"))

            if not isinstance(ts4us_raw, list) or len(ts4us_raw) != 13:
                raise HTTPException(
                    status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                    detail="Se requieren las 13 respuestas reales de la escala TS4US (rango 1 a 5)."
                )
            if not isinstance(asaidas_raw, list) or len(asaidas_raw) != 8:
                raise HTTPException(
                    status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                    detail="Se requieren las 8 respuestas reales de la escala ASAIDAS (rango 1 a 5)."
                )

            pantalla = float(body.get("tiempo_pantalla_horas", body.get("horasPantallaDia", 8.0)))
            redes = float(body.get("horas_redes_sociales", body.get("horasRedesSociales", 3.0)))
            estudio = float(body.get("horas_estudio_virtual", body.get("horasEstudioVirtual", 5.0)))
            sueno = float(body.get("habito_sueno_horas", body.get("habitoSuenoHoras", 6.5)))
            dispositivo = str(body.get("dispositivo_principal", body.get("dispositivoPrincipal", "LAPTOP")))
            texto = str(body.get("texto_libre", body.get("textoLibre", "")))
            carrera = str(body.get("carrera", "Ingenieria de Sistemas de Informacion"))
            ciclo = int(body.get("ciclo", 7))

            escalas = EscalasPsicometricas(
                ts4usRespuestas=[int(x) for x in ts4us_raw],
                asaidasRespuestas=[int(x) for x in asaidas_raw]
            )
            perfil = PerfilEstudiante(
                carrera=carrera,
                ciclo=ciclo,
                horasPantallaDia=pantalla,
                horasRedesSociales=redes,
                horasEstudioVirtual=estudio,
                habitoSuenoHoras=sueno,
                dispositivoPrincipal=dispositivo
            )
            inf_request = InferenciaRequest(
                evaluacionId=eval_id,
                perfilEstudiante=perfil,
                escalas=escalas,
                textoLibre=texto
            )

        resultado = modelo.predecir(inf_request)
        res_dict = resultado.model_dump()

        # Campos separados para el backend Spring Boot y el frontend Angular
        probs_rf = resultado.probabilidadesRandomForest
        probs_str = (
            f"BAJO: {round(probs_rf['BAJO'] * 100, 1)}%, "
            f"MODERADO: {round(probs_rf['MODERADO'] * 100, 1)}%, "
            f"ALTO: {round(probs_rf['ALTO'] * 100, 1)}%"
        )

        if resultado.modificadoPorRegla:
            detalle_regla = (
                f"Regla clínica aplicada ({resultado.reglaClinicaDisparada}): modificó la orientación de "
                f"{resultado.prediccionRandomForest} a {resultado.nivelAnsiedad}. "
                f"(La probabilidad del Random Forest corresponde a la clase {resultado.prediccionRandomForest})."
            )
        elif resultado.reglaClinicaDisparada:
            detalle_regla = f"Regla clínica verificada ({resultado.reglaClinicaDisparada}): consistente con el modelo."
        else:
            detalle_regla = "Sin modificación por reglas clínicas expertas."

        res_dict["nivel_ansiedad_predicho"] = resultado.nivelAnsiedad
        res_dict["prediccion_random_forest"] = resultado.prediccionRandomForest
        res_dict["probabilidades_random_forest"] = resultado.probabilidadesRandomForest
        res_dict["modificado_por_regla"] = resultado.modificadoPorRegla
        res_dict["regla_clinica_disparada"] = resultado.reglaClinicaDisparada
        res_dict["score_probabilidad"] = resultado.probabilidadPrediccionRF
        res_dict["es_caso_critico"] = resultado.alertaCritica
        res_dict["justificacion_cli"] = (
            f"Resultado experimental de orientación preventiva (no constituye diagnóstico clínico). "
            f"Predicción Random Forest: {resultado.prediccionRandomForest} "
            f"({round(resultado.probabilidadPrediccionRF * 100, 1)}% | Distribución RF: {probs_str}). "
            f"{detalle_regla}"
        )

        # Punto 6: No atribuir estresores ficticios si no se detectaron términos del diccionario
        terminos_detectados = resultado.indicadoresPLN.terminosDetectados
        res_dict["estresores_detectados"] = terminos_detectados if terminos_detectados else []
        res_dict["estresores_texto"] = (
            ", ".join(terminos_detectados)
            if terminos_detectados
            else "No se detectaron términos del diccionario"
        )
        res_dict["polaridad_sentimiento"] = resultado.indicadoresPLN.polaridadSentimiento

        return res_dict
    except ValidationError as ve:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(ve)
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error durante el procesamiento de inferencia: {str(e)}"
        )


@app.post("/api/v1/retrain", response_model=RetrainResponse, tags=["MLOps"])
def reentrenar_modelo(request: RetrainRequest, req: Request):
    try:
        from train_model import entrenar_y_evaluar_modelo
        metricas = entrenar_y_evaluar_modelo()
        modelo = req.app.state.modelo_hibrido
        if modelo:
            modelo._cargar_o_entrenar()

        return RetrainResponse(
            mensaje="Reentrenamiento y evaluacion con desenlace STAI-Estado completados exitosamente.",
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
