import sys
import os
from pydantic import ValidationError

# Asegurar path de importacion
sys.path.insert(0, os.path.dirname(__file__))

from app.nlp_preprocessor import NLPPreprocessor
from app.expert_rules import ExpertRulesEngine
from app.schemas import InferenciaRequest, PerfilEstudiante, EscalasPsicometricas

def test_nlp_tildes_y_negaciones():
    print(">>> 1. Probando Módulo de PLN (Tildes, Negaciones y Argot Universitario)...")
    nlp = NLPPreprocessor()
    
    # Casos de auditoría técnica (Punto 3):
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
    assert r_estresado["polaridadSentimiento"] < 0, "'Estoy estresado' debe tener polaridad negativa"
    assert r_no_estresado["polaridadSentimiento"] > 0, "'No estoy estresado' debe invertir polaridad a positiva"
    assert "estresado" not in r_no_estresado["terminosDetectados"], "'No estoy estresado' no debe listar 'estresado' como estresor activo"

    r_sin_panico = nlp.analizar_discurso("Sin panico")
    assert "panico" not in r_sin_panico["terminosDetectados"], "'Sin panico' NO debe activar 'panico' en terminosDetectados"
    assert r_sin_panico["polaridadSentimiento"] > 0, "'Sin panico' debe tener polaridad positiva"

    # Texto vacio o sin terminos del diccionario (Punto 6):
    r_neutro = nlp.analizar_discurso("Hoy asistí a la clase de matemáticas por la mañana.")
    assert r_neutro["terminosDetectados"] == [], "No debe inventar estresores si no hay términos del diccionario"
    print("   [OK] Todos los casos de tildes, negaciones y diccionario vacío pasaron.\n")


def test_validacion_rango_cuestionario():
    print(">>> 2. Probando Validación Estricta de Rango (1..5) y Longitud (13 TS4US + 8 ASAIDAS)...")
    # Rango invalido (0 o 6) debe lanzar ValidationError
    try:
        EscalasPsicometricas(
            ts4usRespuestas=[3]*12 + [6],
            asaidasRespuestas=[3]*8
        )
        assert False, "Debió rechazar valor 6 fuera de rango 1..5"
    except ValidationError:
        pass

    try:
        EscalasPsicometricas(
            ts4usRespuestas=[0] + [3]*12,
            asaidasRespuestas=[3]*8
        )
        assert False, "Debió rechazar valor 0 fuera de rango 1..5"
    except ValidationError:
        pass

    # Longitud valida y rango 1..5
    escalas_ok = EscalasPsicometricas(
        ts4usRespuestas=[1, 2, 3, 4, 5, 3, 2, 4, 3, 5, 2, 3, 4],
        asaidasRespuestas=[2, 3, 4, 2, 3, 5, 2, 3]
    )
    assert len(escalas_ok.ts4usRespuestas) == 13
    assert len(escalas_ok.asaidasRespuestas) == 8
    print("   [OK] Validación de rango y estructura completada.\n")


def test_expert_rules_separacion_rf():
    print(">>> 3. Probando Separación entre Probabilidad RF y Regla Clínica Experta (Punto 4)...")
    rules = ExpertRulesEngine()
    
    # Mismas respuestas con 8 ítems >= 4 (promedio 3.23 < 4.2) y salida fija RF = BAJO,
    # tal como el experimento controlado de la auditoría:
    ts4us_prueba = [4]*8 + [2]*5
    asaidas_bajo = [2]*8

    # Caso 1: "Tengo pánico" (con tilde) con RF fijo en BAJO -> Regla 1 eleva a ALTO con alerta, modificadoPorRegla=True
    nlp = NLPPreprocessor()
    ind_con_panico = nlp.analizar_discurso("Tengo pánico")
    nivel_1, mod_1, alerta_1, regla_1, _ = rules.evaluar_reglas_clinicas(
        ts4us_respuestas=ts4us_prueba,
        asaidas_respuestas=asaidas_bajo,
        horas_pantalla=6.0,
        habito_sueno_horas=7.0,
        dispositivo="LAPTOP",
        indicadores_pln=ind_con_panico,
        prediccion_rf="BAJO"
    )
    assert nivel_1 == "ALTO" and alerta_1 is True and mod_1 is True

    # Caso 2: "Sin panico" con RF fijo en BAJO -> Termina en BAJO sin alerta y sin modificacion por regla
    ind_sin_panico = nlp.analizar_discurso("Sin panico")
    nivel_2, mod_2, alerta_2, regla_2, _ = rules.evaluar_reglas_clinicas(
        ts4us_respuestas=ts4us_prueba,
        asaidas_respuestas=asaidas_bajo,
        horas_pantalla=6.0,
        habito_sueno_horas=7.0,
        dispositivo="LAPTOP",
        indicadores_pln=ind_sin_panico,
        prediccion_rf="BAJO"
    )
    assert nivel_2 == "BAJO" and alerta_2 is False and mod_2 is False
    print("   [OK] Separación entre predicción RF, regla clínica y alerta verificada.\n")


if __name__ == "__main__":
    print("==================================================")
    print("SUITE DE PRUEBAS UNITARIAS - ML SERVICE (AUDITORÍA)")
    print("==================================================")
    test_nlp_tildes_y_negaciones()
    test_validacion_rango_cuestionario()
    test_expert_rules_separacion_rf()
    print("Todas las pruebas unitarias pasaron exitosamente.")
