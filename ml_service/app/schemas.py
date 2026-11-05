from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field, field_validator


class HabitosSocioDemo(BaseModel):
    """
    Variables sociodemográficas y de hábitos digitales (18 variables correspondientes
    al modelo de 40 predictores validados de SERANA).
    """
    age: int = Field(default=20, ge=15, le=60, description="Edad del estudiante universitario")
    semester: int = Field(default=5, ge=1, le=14, description="Semestre académico en curso")
    sex: str = Field(default="Female", description="Sexo biológico del estudiante ('Female' o 'Male')")

    familyExpression: int = Field(default=3, ge=1, le=4, description="Expresión y comunicación familiar (1=Nunca .. 4=Siempre)")
    familyActivities: int = Field(default=2, ge=1, le=4, description="Actividades compartidas en familia (1=Nunca .. 4=Siempre)")
    reglasHogarUso: int = Field(default=2, ge=1, le=4, description="Reglas familiares de uso del móvil (1=Nunca .. 4=Siempre)")
    percepcionDiscriminacion: int = Field(default=1, ge=1, le=4, description="Discriminación por teléfono (1=Nunca .. 4=Siempre)")
    discusionesUsoCelular: int = Field(default=2, ge=1, le=4, description="Discusiones por uso del móvil (1=Nunca .. 4=Siempre)")

    tipoDispositivo: int = Field(default=2, ge=1, le=4, description="1=Celular basico, 2=Smartphone, 3=Tablet, 4=Laptop")
    tiempoUsoDiario: int = Field(default=3, ge=1, le=4, description="1=<1h, 2=1-3h, 3=3-6h, 4=>6h")
    lugarUsoFrecuente: int = Field(default=2, ge=1, le=4, description="1=Hogar, 2=Universidad, 3=Centros comerciales, 4=Todos")
    tipoAccesoRed: int = Field(default=3, ge=1, le=3, description="1=Plan de datos, 2=Wi-Fi, 3=Ambos")

    # 8 Tipos de aplicaciones utilizadas (1=No, 2=Sí)
    appMensajeria: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (WhatsApp / Mensajería instantánea)")
    appVideojuegos: int = Field(default=1, ge=1, le=2, description="1=No, 2=Si (Videojuegos)")
    appRedesSociales: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Redes sociales)")
    appMusica: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Música)")
    appVideos: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Videos / Streaming)")
    appGestionTiempo: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Calendario, alarmas, recordatorios)")
    appCamara: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Cámara)")
    appCorreo: int = Field(default=2, ge=1, le=2, description="1=No, 2=Si (Correo electrónico / académico)")


class EscalasPsicometricas(BaseModel):
    tmdRespuestas: List[int] = Field(
        ...,
        min_length=22,
        max_length=22,
        description="22 ítems reales del Test of Mobile Dependence - TMD (escala 0 a 4)"
    )
    staiRasgoRespuestas: Optional[List[int]] = Field(
        default=None,
        description="Opcional: 20 ítems de STAI-Rasgo (no utilizados como predictores en el modelo de 40 variables para evitar fuga/redundancia)"
    )

    @field_validator("tmdRespuestas")
    @classmethod
    def validar_rango_tmd(cls, respuestas: List[int]) -> List[int]:
        for idx, val in enumerate(respuestas, start=1):
            if not isinstance(val, int) or isinstance(val, bool) or val < 0 or val > 4:
                raise ValueError(f"El ítem TMD #{idx} tiene valor {val}; cada respuesta debe estar en el rango entero 0 a 4.")
        return respuestas


class InferenciaRequest(BaseModel):
    evaluacionId: int = Field(default=1, description="ID de la evaluación generado por el backend de Spring Boot")
    habitosSocioDemo: Optional[HabitosSocioDemo] = Field(default_factory=HabitosSocioDemo)
    escalas: Optional[EscalasPsicometricas] = None

    # Compatibilidad plana directa con clientes que envían las listas directamente
    tmdRespuestas: Optional[List[int]] = None
    staiRasgoRespuestas: Optional[List[int]] = None
    tipoDispositivo: Optional[int] = None
    tiempoUsoDiario: Optional[int] = None
    lugarUsoFrecuente: Optional[int] = None
    tipoAccesoRed: Optional[int] = None
    reglasHogarUso: Optional[int] = None
    percepcionDiscriminacion: Optional[int] = None
    discusionesUsoCelular: Optional[int] = None
    appMensajeria: Optional[int] = None
    appVideojuegos: Optional[int] = None
    appRedesSociales: Optional[int] = None
    appMusica: Optional[int] = None
    appVideos: Optional[int] = None
    appGestionTiempo: Optional[int] = None
    appCamara: Optional[int] = None
    appCorreo: Optional[int] = None

    textoLibre: Optional[str] = Field(
        default="",
        description="Discurso libre del estudiante sobre su sentir emocional y experiencia con el uso de dispositivos"
    )


class IndicadoresPLN(BaseModel):
    polaridadSentimiento: float = Field(..., description="Puntaje de polaridad entre -1.0 y 1.0")
    conteoTerminosArgot: int = Field(..., description="Cantidad de términos de estrés detectados en el diccionario")
    terminosDetectados: List[str] = Field(default_factory=list, description="Lista de palabras de estrés encontradas")
    densidadEstres: float = Field(..., description="Proporción de palabras de estrés respecto al total de palabras")
    longitudPalabras: int = Field(..., description="Cantidad total de palabras del texto analizado")


class FeatureImportanceItem(BaseModel):
    factor: str = Field(..., description="Nombre descriptivo de la variable predictora")
    peso: float = Field(..., description="Importancia relativa normalizada (0.0 - 1.0)")


class InferenciaResponse(BaseModel):
    evaluacionId: int
    nivelAnsiedad: str = Field(..., description="Nivel final de orientación: BAJO, MODERADO o ALTO")
    nivelAnsiedadPredicho: str = Field(..., description="Alias para retrocompatibilidad con Spring Boot")
    prediccionRandomForest: str = Field(..., description="Clase predicha exclusivamente por el modelo Random Forest (40 predictores)")
    probabilidadesRandomForest: Dict[str, float] = Field(..., description="Probabilidades por clase del Random Forest (BAJO, MODERADO, ALTO)")
    probabilidadPrediccionRF: float = Field(..., description="Probabilidad asignada por el Random Forest a su propia clase predicha")
    scoreProbabilidad: float = Field(..., description="Alias para retrocompatibilidad")
    modificadoPorRegla: bool = Field(default=False, description="True si una regla clínica modificó la categoría del Random Forest")
    reglaClinicaDisparada: Optional[str] = Field(default=None, description="Identificador de la regla clínica aplicada (R1, R2, R3)")
    alertaCritica: bool = Field(default=False, description="True si amerita orientación psicopedagógica prioritaria")
    esCasoCritico: bool = Field(default=False, description="Alias para retrocompatibilidad")
    probabilidadRiesgo: float = Field(..., description="Probabilidad de la clase predicha por el Random Forest")
    justificacionCli: str = Field(default="", description="Justificación clínica y metodológica")
    justificacionExclusion: str = Field(default="", description="Alias para retrocompatibilidad")
    indicadoresPLN: IndicadoresPLN
    estresoresDetectados: List[str] = Field(default_factory=list, description="Lista de palabras de estrés encontradas")
    polaridadSentimiento: float = Field(default=0.0, description="Polaridad del discurso libre")
    featureImportance: List[FeatureImportanceItem]
    recomendacionesICBT: List[str] = Field(default_factory=list, description="Pautas de orientación preventiva TCC Digital")
    tiempoInferenciaMs: float


class RetrainRequest(BaseModel):
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
    numeroPredictores: int
