from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field, field_validator


class HabitosSocioDemo(BaseModel):
    """
    15 variables reales del cuestionario SocioDemo_MobileUsage_CollegeStudents_v01
    (excluyendo por completo posturas y molestias musculoesqueléticas).
    """
    tipoDispositivo: int = Field(default=2, ge=1, le=4, description="1=Celular basico, 2=Smartphone, 3=Tablet, 4=Laptop")
    tiempoUsoDiario: int = Field(default=3, ge=1, le=4, description="1=<1h, 2=1-3h, 3=3-6h, 4=>6h")
    lugarUsoFrecuente: int = Field(default=2, ge=1, le=4, description="1=Hogar, 2=Universidad, 3=Centros comerciales, 4=Todos")
    tipoAccesoRed: int = Field(default=3, ge=1, le=3, description="1=Plan de datos, 2=Wi-Fi, 3=Ambos")
    reglasHogarUso: int = Field(default=2, ge=1, le=4, description="1=Nunca, 2=A veces, 3=Frecuentemente, 4=Siempre")
    percepcionDiscriminacion: int = Field(default=1, ge=1, le=4, description="1=Nunca, 2=A veces, 3=Frecuentemente, 4=Siempre")
    discusionesUsoCelular: int = Field(default=2, ge=1, le=4, description="1=Nunca, 2=A veces, 3=Frecuentemente, 4=Siempre")
    appMensajeria: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (WhatsApp / Mensajeria instantanea)")
    appVideojuegos: int = Field(default=1, ge=1, le=2, description="1=No, 2=Si (Videojuegos)")
    appRedesSociales: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Redes sociales)")
    appMusica: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Musica)")
    appVideos: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Videos / Streaming)")
    appGestionTiempo: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Calendario, alarmas, recordatorios)")
    appCamara: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Camara)")
    appCorreo: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Correo electronico / academico)")


class EscalasPsicometricas(BaseModel):
    tmdRespuestas: List[int] = Field(
        ...,
        min_length=22,
        max_length=22,
        description="22 items reales del Test of Mobile Dependence - TMD (escala 0 a 4)"
    )
    staiRasgoRespuestas: List[int] = Field(
        ...,
        min_length=20,
        max_length=20,
        description="20 items reales de la escala STAI-Rasgo item_21..item_40 (escala 1 a 4)"
    )

    @field_validator("tmdRespuestas")
    @classmethod
    def validar_rango_tmd(cls, respuestas: List[int]) -> List[int]:
        for idx, val in enumerate(respuestas, start=1):
            if not isinstance(val, int) or isinstance(val, bool) or val < 0 or val > 4:
                raise ValueError(f"El item TMD #{idx} tiene valor {val}; cada respuesta debe estar en el rango entero 0 a 4.")
        return respuestas

    @field_validator("staiRasgoRespuestas")
    @classmethod
    def validar_rango_stai(cls, respuestas: List[int]) -> List[int]:
        for idx, val in enumerate(respuestas, start=1):
            if not isinstance(val, int) or isinstance(val, bool) or val < 1 or val > 4:
                raise ValueError(f"El item STAI-Rasgo #{idx} tiene valor {val}; cada respuesta debe estar en el rango entero 1 a 4.")
        return respuestas


class InferenciaRequest(BaseModel):
    evaluacionId: int = Field(default=1, description="ID de la evaluacion generado por el backend de Spring Boot")
    habitosSocioDemo: Optional[HabitosSocioDemo] = Field(default_factory=HabitosSocioDemo)
    escalas: EscalasPsicometricas
    textoLibre: Optional[str] = Field(
        default="",
        description="Discurso libre del estudiante sobre su sentir emocional y experiencia con el uso de dispositivos"
    )


class IndicadoresPLN(BaseModel):
    polaridadSentimiento: float = Field(..., description="Puntaje de polaridad entre -1.0 (muy negativo) y 1.0 (muy positivo)")
    conteoTerminosArgot: int = Field(..., description="Cantidad de terminos de estres detectados en el diccionario")
    terminosDetectados: List[str] = Field(default_factory=list, description="Lista de palabras de estres encontradas")
    densidadEstres: float = Field(..., description="Proporcion de palabras de estres respecto al total de palabras")
    longitudPalabras: int = Field(..., description="Cantidad total de palabras del texto analizado")


class FeatureImportanceItem(BaseModel):
    factor: str = Field(..., description="Nombre descriptivo de la variable predictora")
    peso: float = Field(..., description="Importancia relativa normalizada (0.0 - 1.0)")


class InferenciaResponse(BaseModel):
    evaluacionId: int
    nivelAnsiedad: str = Field(..., description="Nivel final de orientacion: BAJO, MODERADO o ALTO")
    prediccionRandomForest: str = Field(..., description="Clase predicha exclusivamente por el modelo Random Forest (desenlace STAI-Estado)")
    probabilidadesRandomForest: Dict[str, float] = Field(..., description="Probabilidades por clase del Random Forest (BAJO, MODERADO, ALTO)")
    probabilidadPrediccionRF: float = Field(..., description="Probabilidad asignada por el Random Forest a su propia clase predicha")
    modificadoPorRegla: bool = Field(default=False, description="True si una regla clinica modifico la categoria del Random Forest")
    reglaClinicaDisparada: Optional[str] = Field(default=None, description="Identificador de la regla clinica aplicada")
    alertaCritica: bool = Field(default=False, description="True si amerita orientacion psicopedagogica prioritaria")
    probabilidadRiesgo: float = Field(..., description="Probabilidad de la clase predicha por el Random Forest (0.0 - 1.0)")
    indicadoresPLN: IndicadoresPLN
    featureImportance: List[FeatureImportanceItem]
    recomendacionesICBT: List[str] = Field(default_factory=list, description="Pautas de orientacion preventiva TCC Digital")
    tiempoInferenciaMs: float


class RetrainRequest(BaseModel):
    nuevosRegistros: Optional[List[Dict[str, Any]]] = Field(
        default=None,
        description="Lote de nuevos registros de estudiantes"
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
