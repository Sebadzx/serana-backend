import re
import unicodedata
from typing import Dict, List, Set

# Diccionario de terminos de estres, agotamiento y argot universitario limeño (formas canonicas sin tilde)
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

PALABRAS_NEGACION: Set[str] = {
    "no", "nunca", "jamas", "ni", "tampoco", "cero", "sin", "ningun", "ninguna", "nada"
}


def remover_tildes(texto: str) -> str:
    """Convierte caracteres acentuados en ASCII para empate léxico consistente (conservando ñ)."""
    if not texto:
        return ""
    resultado = []
    for c in texto:
        if c in ("ñ", "Ñ"):
            resultado.append(c.lower())
        else:
            decomp = unicodedata.normalize("NFD", c)
            sin_marca = "".join(ch for ch in decomp if unicodedata.category(ch) != "Mn")
            resultado.append(sin_marca)
    return "".join(resultado)


def normalizar_texto(texto: str) -> str:
    """Normaliza texto: minúsculas, remoción de tildes y limpieza de espacios."""
    if not texto:
        return ""
    texto = remover_tildes(texto.lower())
    texto = re.sub(r"[^\w\s]", " ", texto)
    texto = re.sub(r"\s+", " ", texto).strip()
    return texto


def dividir_clausulas(texto: str) -> List[str]:
    """Divide el texto en cláusulas por signos de puntuación para acotar el alcance de las negaciones."""
    if not texto:
        return []
    texto_sin_tildes = remover_tildes(texto.lower())
    partes = re.split(r"[.,;:!?¡¿\n\r]+", texto_sin_tildes)
    clausulas = []
    for p in partes:
        limpia = re.sub(r"[^\w\s]", " ", p)
        limpia = re.sub(r"\s+", " ", limpia).strip()
        if limpia:
            clausulas.append(limpia)
    return clausulas


class NLPPreprocessor:
    """
    Módulo de Procesamiento de Lenguaje Natural adaptado al contexto
    sociocultural de estudiantes universitarios de Lima Metropolitana.
    Corrige normalización de tildes y alcance de negaciones (ventana de hasta 3 tokens previos por cláusula).
    """

    def __init__(self):
        self.lexico_estres: Dict[str, float] = {
            remover_tildes(k.lower()): v for k, v in ARGOT_ESTRES_LIMENO.items()
        }
        self.lexico_positivo: Dict[str, float] = {
            remover_tildes(k.lower()): v for k, v in TERMINOS_POSITIVOS.items()
        }
        # Ordenar términos de mayor a menor longitud de palabras para priorizar frases ("no doy mas") sobre unigramas
        self.terminos_estres_ordenados = sorted(
            self.lexico_estres.items(), key=lambda x: (-len(x[0].split()), -len(x[0]))
        )
        self.terminos_positivos_ordenados = sorted(
            self.lexico_positivo.items(), key=lambda x: (-len(x[0].split()), -len(x[0]))
        )

    @staticmethod
    def _esta_negado(tokens_clausula: List[str], idx_inicio: int, termino: str) -> bool:
        """
        Determina si una aparición en tokens_clausula[idx_inicio] está precedida por una negación
        en una ventana de hasta 3 palabras previas dentro de la misma cláusula.
        Si el propio término comienza con una palabra de negación (ej. 'no doy mas'),
        solo se considera negado si hay otra negación antes de su propio inicio.
        """
        ventana_inicio = max(0, idx_inicio - 3)
        tokens_previos = tokens_clausula[ventana_inicio:idx_inicio]
        return any(tok in PALABRAS_NEGACION for tok in tokens_previos)

    def analizar_discurso(self, texto: str) -> Dict[str, any]:
        """
        Analiza el texto libre emocional del estudiante y extrae indicadores
        cuantificables de polaridad, argot y densidad respetando tildes y negaciones.
        """
        texto_norm = normalizar_texto(texto)
        palabras_totales = texto_norm.split()
        total_palabras = len(palabras_totales)

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
        terminos_encontrados: List[str] = []

        clausulas = dividir_clausulas(texto)

        for clausula in clausulas:
            tokens = clausula.split()
            n_tok = len(tokens)
            ocupados = [False] * n_tok

            # 1. Evaluar términos de estrés (frases primero, luego palabras simples)
            for termino, peso in self.terminos_estres_ordenados:
                term_tokens = termino.split()
                m = len(term_tokens)
                for i in range(n_tok - m + 1):
                    if any(ocupados[i : i + m]):
                        continue
                    if tokens[i : i + m] == term_tokens:
                        for k in range(i, i + m):
                            ocupados[k] = True
                        if self._esta_negado(tokens, i, termino):
                            # Ejemplo: "no estoy estresado", "sin panico" -> no es estresor activo
                            score_positivo += peso * 0.5
                        else:
                            score_negativo += peso
                            if termino not in terminos_encontrados:
                                terminos_encontrados.append(termino)

            # 2. Evaluar términos positivos en los tokens restantes
            for termino, peso in self.terminos_positivos_ordenados:
                term_tokens = termino.split()
                m = len(term_tokens)
                for i in range(n_tok - m + 1):
                    if any(ocupados[i : i + m]):
                        continue
                    if tokens[i : i + m] == term_tokens:
                        for k in range(i, i + m):
                            ocupados[k] = True
                        if self._esta_negado(tokens, i, termino):
                            # Ejemplo: "no estoy tranquilo", "no me siento bien"
                            score_negativo += peso * 0.8
                        else:
                            score_positivo += peso

        # Normalizar polaridad entre -1.0 y 1.0
        denominador = score_negativo + score_positivo + 1.0
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
        Retorna las 3 características continuas densas para concatenar al Random Forest:
        [polaridadSentimiento, scorePonderadoEstresNormalizado, densidadEstres]
        """
        res = self.analizar_discurso(texto)
        score_norm = min(res["scorePonderadoEstres"] / 10.0, 1.0)
        return [
            res["polaridadSentimiento"],
            score_norm,
            min(res["densidadEstres"] * 5.0, 1.0)
        ]
