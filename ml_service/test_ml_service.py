import sys
import os

# Asegurar path de importacion
sys.path.insert(0, os.path.dirname(__file__))

from app.nlp_preprocessor import NLPPreprocessor
from app.expert_rules import ExpertRulesEngine
from app.schemas import InferenciaRequest, PerfilEstudiante, EscalasPsicometricas

def test_nlp():
    print(">>> 1. Probando Módulo de PLN (Argot Universitario Limeño)...")
    nlp = NLPPreprocessor()
    
    # Texto con estresores y jerga tipica limeña
    texto_estres = "La verdad siento que ya no doy mas, paro recontra quemado con las entregas virtuales, no duermo por el desvelo y tengo miedo de jalar el ciclo."
    res = nlp.analizar_discurso(texto_estres)
    print("   Resultado PLN (Estres):", res)
    assert res["polaridadSentimiento"] < 0, "La polaridad debe ser negativa"
    assert res["conteoTerminosArgot"] >= 3, "Debe detectar al menos 3 terminos de argot/estres"
    assert "quemado" in res["terminosDetectados"], "Debe detectar 'quemado'"
    assert "jalar" in res["terminosDetectados"], "Debe detectar 'jalar'"

    # Texto optimista / adaptado
    texto_calma = "Me siento tranquilo y motivado con mis cursos virtuales, el horario es super manejable y organizado."
    res_calma = nlp.analizar_discurso(texto_calma)
    print("   Resultado PLN (Calma):", res_calma)
    assert res_calma["polaridadSentimiento"] > 0, "La polaridad debe ser positiva"
    print("   [OK] NLP Preprocessor funcionando correctamente.\n")

def test_expert_rules():
    print(">>> 2. Probando Capa de Reglas Clinicas Expertas...")
    rules = ExpertRulesEngine()
    
    # Caso critico: promedio alto y palabras de panico
    ts4us_alto = [5, 5, 4, 5, 4, 5, 5, 4, 5, 4, 5, 4, 5]
    asaidas_alto = [4, 4, 5, 4, 4, 5, 4, 4]
    indicadores_pln = {
        "polaridadSentimiento": -0.8,
        "conteoTerminosArgot": 3,
        "terminosDetectados": ["panico", "colapso", "no doy mas"]
    }
    
    nivel, prob, alerta, regla, pautas = rules.evaluar_reglas_clinicas(
        ts4us_respuestas=ts4us_alto,
        asaidas_respuestas=asaidas_alto,
        horas_pantalla=12.0,
        dispositivo="SMARTPHONE",
        indicadores_pln=indicadores_pln,
        prediccion_rf="MODERADO", # Simular que RF fallo por ruido
        probabilidad_rf=0.55
    )
    
    print(f"   Resultado Regla Experta: Nivel={nivel}, AlertaCritica={alerta}, Regla={regla}")
    assert nivel == "ALTO", "La regla de crisis debe elevar a ALTO"
    assert alerta is True, "Debe activar alerta critica"
    assert regla == "REGLA_1_SATURACION_EXTREMA_Y_CRISIS_DISCURSIVA"
    assert len(pautas) >= 3, "Debe sugerir recomendaciones iCBT y alerta urgente"
    print("   [OK] Reglas Expertas funcionando correctamente.\n")

if __name__ == "__main__":
    print("==================================================")
    print("SUITE DE PRUEBAS UNITARIAS - ML SERVICE")
    print("==================================================")
    test_nlp()
    test_expert_rules()
    print("Todas las pruebas unitarias pasaron exitosamente.")
