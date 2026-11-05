import sys
import os
from pydantic import ValidationError

sys.path.insert(0, os.path.dirname(__file__))

from app.nlp_preprocessor import NLPPreprocessor
from app.expert_rules import ExpertRulesEngine
from app.schemas import InferenciaRequest, HabitosSocioDemo, EscalasPsicometricas
from app.hybrid_model import HybridInferenceEngine


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
    print("   [OK] Todos los casos de tildes, negaciones y diccionario pasaron.\n")


def test_validacion_rango_cuestionario():
    print(">>> 2. Probando Validación de Rango (22 TMD [0..4])...")
    # TMD fuera de rango (5 no es valido en escala 0..4)
    try:
        EscalasPsicometricas(
            tmdRespuestas=[2] * 21 + [5]
        )
        assert False, "Debió rechazar valor 5 fuera de rango 0..4 en TMD"
    except ValidationError:
        pass

    escalas_ok = EscalasPsicometricas(
        tmdRespuestas=[0, 1, 2, 3, 4] * 4 + [2, 3]
    )
    assert len(escalas_ok.tmdRespuestas) == 22
    print("   [OK] Validación de rango TMD (0 a 4) completada.\n")


def test_expert_rules_y_modelo_hibrido():
    print(">>> 3. Probando Modelo Híbrido SERANA (40 predictores -> STAI-Estado corregido)...")
    motor = HybridInferenceEngine()
    assert len(motor.feature_names) == 40, f"Se esperaban 40 características, se encontraron {len(motor.feature_names)}"

    tmd_prueba = [2] * 22  # suma = 44
    req = InferenciaRequest(
        evaluacionId=100,
        habitosSocioDemo=HabitosSocioDemo(
            tipoDispositivo=2,
            tiempoUsoDiario=3,
            appRedesSociales=2
        ),
        escalas=EscalasPsicometricas(
            tmdRespuestas=tmd_prueba,
        ),
        textoLibre="Hoy avancé mis trabajos de tesis con calma y sin angustia.",
    )
    res = motor.predecir(req)
    assert res.nivelAnsiedad in ["BAJO", "MODERADO", "ALTO"]
    assert sum(res.probabilidadesRandomForest.values()) > 0.99
    assert len(res.featureImportance) == 5
    print(f"   [OK] Inferencia completada: RF={res.prediccionRandomForest}, Final={res.nivelAnsiedad}, Regla={res.reglaClinicaDisparada}\n")


if __name__ == "__main__":
    print("==================================================")
    print("SUITE DE PRUEBAS UNITARIAS - ML SERVICE (40 VARS)")
    print("==================================================")
    test_nlp_tildes_y_negaciones()
    test_validacion_rango_cuestionario()
    test_expert_rules_y_modelo_hibrido()
    print("Todas las pruebas unitarias pasaron exitosamente.")
