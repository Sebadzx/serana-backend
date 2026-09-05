import re
import unicodedata
from typing import Dict, List, Tuple

# Diccionario de terminos de estres, agotamiento y argot universitario limeño
ARGOT_ESTRES_LIMENO = {
    # Agotamiento extremo / Burnout
    "quemado": 1.5, "quemada": 1.5, "queme": 1.5,
    "colapso": 2.0, "colapsado": 2.0, "colapsada": 2.0, "colapsando": 2.0,
    "muerto": 1.0, "muerta": 1.0, "morir": 1.0,
    "asfixiado": 1.5, "asfixiada": 1.5, "ahogado": 1.5,
    "frito": 1.2, "frita": 1.2,
    "no doy mas": 2.5, "tirar la toalla": 2.0, "ya fue": 1.0,

    # Presion academica / Desaprobacion
    "jalar": 1.5, "jale": 1.5, "jalado": 1.5, "jalada": 1.5, "jalare": 1.8,
    "tranca": 1.2, "recontra tranca": 1.8,
    "ciclarse": 1.8, "cicle": 1.8, "ciclo": 0.5,
    "bica": 2.0, "trica": 2.5,
    "bajon": 1.5, "desespero": 2.0,

    # Sobrecarga digital y privacion de sueño
    "trasnochar": 1.2, "trasnochando": 1.3, "amanecida": 1.5,
    "desvelo": 1.3, "insomnio": 1.8, "ojeras": 1.0,
    "notificaciones": 0.8, "bombardeo": 1.2, "saturado": 1.5, "saturada": 1.5,
    "hiperconectado": 1.4, "adiccion": 1.5, "pantalla": 0.5,

    # Estados emocionales de angustia clinica
    "ansiedad": 2.0, "ansioso": 1.8, "ansiosa": 1.8,
    "panico": 2.5, "angustia": 2.2, "angustiado": 2.0, "angustiada": 2.0,
    "estres": 1.5, "estresado": 1.5, "estresada": 1.5,
    "llorar": 1.8, "llorando": 1.8, "llanto": 1.8,
    "impotencia": 1.8, "frustracion": 1.5, "frustrado": 1.5, "frustrada": 1.5,
    "desesperado": 2.0, "desesperada": 2.0, "bloqueado": 1.5, "bloqueada": 1.5
}

TERMINOS_POSITIVOS = {
    "tranquilo": 1.5, "tranquila": 1.5, "calma": 1.5, "paz": 1.5,
    "bien": 1.0, "super": 1.0, "motivado": 1.5, "motivada": 1.5,
    "relajado": 1.5, "relajada": 1.5, "organizado": 1.2, "organizada": 1.2,
    "controlado": 1.5, "manejable": 1.5, "alegre": 1.5, "feliz": 1.5,
    "exito": 1.5, "apoyo": 1.2, "descanso": 1.2, "duermo bien": 1.8,
    "confianza": 1.5, "autoeficacia": 1.5, "satisfaccion": 1.5
}

PALABRAS_NEGACION = {"no", "nunca", "jamas", "ni", "tampoco", "cero", "sin"}

def normalizar_texto(texto: str) -> str:
    """Normaliza texto: minusculas, conversion de tildes para busqueda robusta."""
    if not texto:
        return ""
    texto = texto.lower()
    # Eliminar puntuacion compleja pero preservar espacios
    texto = re.sub(r'[^\w\s]', ' ', texto)
    # Reemplazar multiples espacios por uno solo
    texto = re.sub(r'\s+', ' ', texto).strip()
    return texto

def remover_tildes(texto: str) -> str:
    """Convierte caracteres acentuados en ASCII para empate léxico."""
    return ''.join(
        c for c in unicodedata.normalize('NFD', texto)
        if unicodedata.category(c) != 'Mn'
    )

class NLPPreprocessor:
    """
    Modulo de Procesamiento de Lenguaje Natural adaptado al contexto
    sociocultural de estudiantes universitarios de Lima Metropolitana.
    """

    def __init__(self):
        # Generar versiones sin tilde de los diccionarios
        self.lexico_estres = {}
        for k, v in ARGOT_ESTRES_LIMENO.items():
            self.lexico_estres[k] = v
            sin_tilde = remover_tildes(k)
            if sin_tilde != k:
                self.lexico_estres[sin_tilde] = v

        self.lexico_positivo = {}
        for k, v in TERMINOS_POSITIVOS.items():
            self.lexico_positivo[k] = v
            sin_tilde = remover_tildes(k)
            if sin_tilde != k:
                self.lexico_positivo[sin_tilde] = v

    def analizar_discurso(self, texto: str) -> Dict[str, any]:
        """
        Analiza el texto libre emocional del estudiante (50-500 palabras)
        y extrae indicadores cuantificables de polaridad, argot y densidad.
        """
        texto_norm = normalizar_texto(texto)
        palabras = texto_norm.split()
        total_palabras = len(palabras)

        if total_palabras == 0:
            return {
                "polaridadSentimiento": 0.0,
                "conteoTerminosArgot": 0,
                "terminosDetectados": [],
                "densidadEstres": 0.0,
                "longitudPalabras": 0,
                "scorePonderadoEstres": 0.0
            }

        score_negativo = 0.0
        score_positivo = 0.0
        terminos_encontrados = []

        # Analisis de n-gramas (bigramas y unigramas)
        texto_espaciado = f" {texto_norm} "

        for termino, peso in self.lexico_estres.items():
            patron = r'\b' + re.escape(termino) + r'\b'
            coincidencias = len(re.findall(patron, texto_espaciado))
            if coincidencias > 0:
                score_negativo += peso * coincidencias
                if termino not in terminos_encontrados:
                    terminos_encontrados.append(termino)

        for termino, peso in self.lexico_positivo.items():
            patron = r'\b' + re.escape(termino) + r'\b'
            coincidencias = len(re.findall(patron, texto_espaciado))
            if coincidencias > 0:
                score_positivo += peso * coincidencias

        # Verificacion basica de contexto de negacion ("no estoy estresado")
        for i, palabra in enumerate(palabras):
            if palabra in PALABRAS_NEGACION and i + 1 < len(palabras):
                sig = palabras[i + 1]
                if sig in self.lexico_estres:
                    score_negativo = max(0.0, score_negativo - self.lexico_estres[sig] * 1.5)
                    score_positivo += 0.8
                elif sig in self.lexico_positivo:
                    score_positivo = max(0.0, score_positivo - self.lexico_positivo[sig] * 1.5)
                    score_negativo += 1.0

        # Normalizar polaridad entre -1.0 y 1.0
        denominador = (score_negativo + score_positivo + 1.0)
        polaridad = (score_positivo - score_negativo) / denominador
        polaridad = max(-1.0, min(1.0, round(polaridad, 3)))

        densidad = len(terminos_encontrados) / max(total_palabras, 1)

        return {
            "polaridadSentimiento": polaridad,
            "conteoTerminosArgot": len(terminos_encontrados),
            "terminosDetectados": sorted(terminos_encontrados),
            "densidadEstres": round(densidad, 4),
            "longitudPalabras": total_palabras,
            "scorePonderadoEstres": round(score_negativo, 3)
        }

    def extraer_vector_pln(self, texto: str) -> List[float]:
        """
        Retorna las 3 caracteristicas continuas densas para concatenar al Random Forest:
        [polaridadSentimiento, scorePonderadoEstresNormalizado, densidadEstres]
        """
        res = self.analizar_discurso(texto)
        score_norm = min(res["scorePonderadoEstres"] / 10.0, 1.0)
        return [
            res["polaridadSentimiento"],
            score_norm,
            min(res["densidadEstres"] * 5.0, 1.0)
        ]
