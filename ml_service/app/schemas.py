from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field, field_validator


class PerfilEstudiante(BaseModel):
    carrera: str = Field(default="Ingenieria de Sistemas de Informacion", description="Carrera universitaria")
    ciclo: int = Field(default=7, ge=1, le=12, description="Ciclo academico actual (1-12)")
    horasPantallaDia: float = Field(default=8.0, ge=0.0, le=24.0, description="Promedio de horas diarias frente a pantallas")
    horasRedesSociales: float = Field(default=3.0, ge=0.0, le=24.0, description="Horas diarias en redes sociales")
    horasEstudioVirtual: float = Field(default=5.0, ge=0.0, le=24.0, description="Horas diarias de estudio virtual")
    habitoSuenoHoras: float = Field(default=6.5, ge=0.0, le=24.0, description="Promedio de horas diarias de sueno nocturno")
    dispositivoPrincipal: str = Field(default="LAPTOP", description="LAPTOP, SMARTPHONE, TABLET, DESKTOP")


class EscalasPsicometricas(BaseModel):
    ts4usRespuestas: List[int] = Field(
        ...,
        min_length=13,
        max_length=13,
        description="13 items reales de la escala TS4US (1=Totalmente en desacuerdo, 5=Totalmente de acuerdo)"
    )
    asaidasRespuestas: List[int] = Field(
        ...,
        min_length=8,
        max_length=8,
        description="8 items reales de la escala ASAIDAS (1=Nunca, 5=Siempre)"
    )

    @field_validator("ts4usRespuestas", "asaidasRespuestas")
    @classmethod
    def validar_rango_likert(cls, respuestas: List[int]) -> List[int]:
        for idx, val in enumerate(respuestas, start=1):
            if not isinstance(val, int) or val < 1 or val > 5:
                raise ValueError(f"El item #{idx} tiene valor {val}; cada respuesta debe estar en el rango entero 1 a 5.")
        return respuestas


class InferenciaRequest(BaseModel):
    evaluacionId: int = Field(default=1, description="ID de la evaluacion generado por el backend de Spring Boot")
    perfilEstudiante: Optional[PerfilEstudiante] = Field(default_factory=PerfilEstudiante)
    escalas: EscalasPsicometricas
    textoLibre: Optional[str] = Field(
        default="",
        description="Discurso libre del estudiante sobre su sentir emocional y experiencia con la tecnologia"
    )


class IndicadoresPLN(BaseModel):
    polaridadSentimiento: float = Field(..., description="Puntaje de polaridad entre -1.0 (muy negativo) y 1.0 (muy positivo)")
    conteoTerminosArgot: int = Field(..., description="Cantidad de terminos de estres/tecnoestres detectados en argot universitario limeño")
    terminosDetectados: List[str] = Field(default_factory=list, description="Lista de palabras de argot o estres encontradas")
    densidadEstres: float = Field(..., description="Proporcion de palabras de estres respecto al total de palabras")
    longitudPalabras: int = Field(..., description="Cantidad total de palabras del texto analizado")


class FeatureImportanceItem(BaseModel):
    factor: str = Field(..., description="Nombre descriptivo de la variable predictora")
    peso: float = Field(..., description="Importancia relativa normalizada (0.0 - 1.0)")


class InferenciaResponse(BaseModel):
    evaluacionId: int
    # Resultado final de orientacion preventiva
    nivelAnsiedad: str = Field(..., description="Nivel final de orientacion: BAJO, MODERADO o ALTO")
    # Separacion explicita entre Random Forest y Reglas Clinicas Expertas
    prediccionRandomForest: str = Field(..., description="Clase predicha exclusivamente por el modelo Random Forest")
    probabilidadesRandomForest: Dict[str, float] = Field(..., description="Probabilidades por clase del Random Forest (BAJO, MODERADO, ALTO)")
    probabilidadPrediccionRF: float = Field(..., description="Probabilidad asignada por el Random Forest a su propia clase predicha")
    modificadoPorRegla: bool = Field(default=False, description="True si una regla clinica modifico la categoria del Random Forest")
    reglaClinicaDisparada: Optional[str] = Field(default=None, description="Identificador de la regla clinica aplicada")
    alertaCritica: bool = Field(default=False, description="True si amerita orientacion psicopedagogica prioritaria")
    # Campo de compatibilidad que refleja estrictamente la probabilidad de la clase predicha por el RF
    probabilidadRiesgo: float = Field(..., description="Probabilidad de la clase predicha por el Random Forest (0.0 - 1.0)")
    indicadoresPLN: IndicadoresPLN
    featureImportance: List[FeatureImportanceItem]
    recomendacionesICBT: List[str] = Field(default_factory=list, description="Pautas de orientacion preventiva TCC Digital")
    tiempoInferenciaMs: float


class RetrainRequest(BaseModel):
    nuevosRegistros: Optional[List[Dict[str, Any]]] = Field(
        default=None,
        description="Lote de nuevos registros de estudiantes para ajuste fino"
    )
    forzarReentrenamiento: bool = Field(default=False)


class RetrainResponse(BaseModel):
    mensaje: str
    f1ScoreMacro: float
    accuracy: float
    precisionMacro: float
    recallMacro: float
    totalMuestras: int
    timestamp: str


class HealthResponse(BaseModel):
    status: str
    modeloCargado: bool
    version: str
    algoritmo: str
