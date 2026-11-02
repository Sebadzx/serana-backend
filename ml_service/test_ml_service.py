import sys
import os
from pydantic import ValidationError

sys.path.insert(0, os.path.dirname(__file__))

from app.nlp_preprocessor import NLPPreprocessor
from app.expert_rules import ExpertRulesEngine
from app.schemas import InferenciaRequest, HabitosSocioDemo, EscalasPsicometricas
from app.hybrid_model import HybridTriajeModel


def test_nlp_tildes_y_negaciones():
    print(">>> 1. Probando Módulo de PLN (Tildes, Negaciones y Argot Universitario)...")
    nlp = NLPPreprocessor()

    r_panico_sin = nlp.analizar_discurso("Tengo panico")
    r_panico_con = nlp.analizar_discurso("Tengo pánico")
    assert "panico" in r_panico_sin["terminosDetectados"], "Debe detectar 'panico' sin tilde"
    assert "panico" in r_panico_con["terminosDetectados"], "Debe detectar 'pánico' con tilde"
    assert r_panico_sin["polaridadSentimiento"] == r_panico_con["polaridadSentimiento"]

    r_nodoy_sin = nlp.analizar_discurso("No doy mas")
    r_nodoy_con = nlp.analizar_discurso("No doy más")
    assert "no doy mas" in r_nodoy_sin["terminosDetectados"], "Debe detectar 'No doy mas'"
    assert "no doy mas" in r_nodoy_con["terminosDetectados"], "Debe detectar 'No doy más' con tilde"

    r_estresado = nlp.analizar_discurso("Estoy estresado")
    r_no_estresado = nlp.analizar_discurso("No estoy estresado")
    assert r_estresado["polaridadSentimiento"] < 0
    assert r_no_estresado["polaridadSentimiento"] > 0
    assert "estresado" not in r_no_estresado["terminosDetectados"]

    r_sin_panico = nlp.analizar_discurso("Sin panico")
    assert "panico" not in r_sin_panico["terminosDetectados"]
    assert r_sin_panico["polaridadSentimiento"] > 0

    r_neutro = nlp.analizar_discurso("Hoy asistí a la clase de matemáticas por la mañana.")
    assert r_neutro["terminosDetectados"] == []
    print("   [OK] Todos los casos de tildes, negaciones y diccionario vacío pasaron.\n")


def test_validacion_rango_cuestionario():
    print(">>> 2. Probando Validación Estricta de Rango (22 TMD [0..4] + 20 STAI-Rasgo [1..4])...")
    # TMD fuera de rango (5 no es valido en escala 0..4)
    try:
        EscalasPsicometricas(
            tmdRespuestas=[2] * 21 + [5],
            staiRasgoRespuestas=[2] * 20,
        )
        assert False, "Debió rechazar valor 5 fuera de rango 0..4 en TMD"
    except ValidationError:
        pass

    # STAI-Rasgo fuera de rango (0 no es valido en escala 1..4)
    try:
        EscalasPsicometricas(
            tmdRespuestas=[2] * 22,
            staiRasgoRespuestas=[0] + [2] * 19,
        )
        assert False, "Debió rechazar valor 0 fuera de rango 1..4 en STAI-Rasgo"
    except ValidationError:
        pass

    escalas_ok = EscalasPsicometricas(
        tmdRespuestas=[0, 1, 2, 3, 4] * 4 + [2, 3],
        staiRasgoRespuestas=[1, 2, 3, 4] * 5,
    )
    assert len(escalas_ok.tmdRespuestas) == 22
    assert len(escalas_ok.staiRasgoRespuestas) == 20
    print("   [OK] Validación de rango y estructura completada.\n")


def test_expert_rules_y_modelo_hibrido():
    print(">>> 3. Probando Separación RF vs. Regla Clínica y Modelo Híbrido (57 variables -> STAI-Estado)...")
    rules = ExpertRulesEngine()
    nlp = NLPPreprocessor()

    # Mismas respuestas con TMD = 44 (Dependencia Media >= 39) y STAI-Rasgo = 36 (Bajo), RF fijo en BAJO
    tmd_prueba = [2] * 22  # suma = 44
    stai_bajo = [1] * 4 + [2] * 16  # suma = 36

    ind_con_panico = nlp.analizar_discurso("Tengo pánico")
    nivel_1, mod_1, alerta_1, regla_1, _ = rules.evaluar_reglas_clinicas(
        tmd_respuestas=tmd_prueba,
        stai_rasgo_respuestas=stai_bajo,
        tiempo_uso_diario=2,
        indicadores_pln=ind_con_panico,
        prediccion_rf="BAJO",
    )
    assert nivel_1 == "ALTO" and alerta_1 is True and mod_1 is True

    ind_sin_panico = nlp.analizar_discurso("Sin panico")
    nivel_2, mod_2, alerta_2, regla_2, _ = rules.evaluar_reglas_clinicas(
        tmd_respuestas=tmd_prueba,
        stai_rasgo_respuestas=stai_bajo,
        tiempo_uso_diario=2,
        indicadores_pln=ind_sin_panico,
        prediccion_rf="BAJO",
    )
    assert nivel_2 == "BAJO" and alerta_2 is False and mod_2 is False

    # Prueba integral con HybridTriajeModel (57 caracteristicas reales)
    motor = HybridTriajeModel()
    req = InferenciaRequest(
        evaluacionId=100,
        habitosSocioDemo=HabitosSocioDemo(),
        escalas=EscalasPsicometricas(
            tmdRespuestas=tmd_prueba,
            staiRasgoRespuestas=stai_bajo,
        ),
        textoLibre="Hoy avancé mis trabajos sin panico y tranquilo.",
    )
    res = motor.predecir(req)
    assert res.indicadoresPLN.terminosDetectados == []
    assert sum(res.probabilidadesRandomForest.values()) > 0.99
    print("   [OK] Separación entre predicción RF, regla clínica y modelo de 57 variables verificada.\n")


if __name__ == "__main__":
    print("==================================================")
    print("SUITE DE PRUEBAS UNITARIAS - ML SERVICE (57 VARS)")
    print("==================================================")
    test_nlp_tildes_y_negaciones()
    test_validacion_rango_cuestionario()
    test_expert_rules_y_modelo_hibrido()
    print("Todas las pruebas unitarias pasaron exitosamente.")
