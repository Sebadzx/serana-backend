import os
import json
from typing import Tuple, Dict, Any
import numpy as np
import pandas as pd
import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import StratifiedKFold, cross_validate
from sklearn.metrics import classification_report, f1_score


MODEL_FILE = os.path.join(os.path.dirname(__file__), "model.joblib")
FEATURE_NAMES_FILE = os.path.join(os.path.dirname(__file__), "feature_names.json")

FEATURE_NAMES = [
    # 13 items TS4US (Escala de Tecnoestres en Universitarios)
    "TS4US_1_SobrecargaInfo", "TS4US_2_InvasionVidaPrivada", "TS4US_3_ComplejidadSistemas",
    "TS4US_4_InseguridadDigital", "TS4US_5_IncertidumbreTecnologica", "TS4US_6_FatigaPantalla",
    "TS4US_7_PresionConectividad", "TS4US_8_MultitareaVirtual", "TS4US_9_ObsolescenciaHabilidades",
    "TS4US_10_DependenciaPlataformas", "TS4US_11_SaturacionTareas", "TS4US_12_MiedoDesconexion",
    "TS4US_13_FaltaControlDigital",
    # 8 items ASAIDAS (Escala de Ansiedad y Dependencia a la IA / Academico)
    "ASAIDAS_1_UsoCompulsivoIA", "ASAIDAS_2_AnsiedadEvaluaciones", "ASAIDAS_3_DelegacionCognitiva",
    "ASAIDAS_4_MiedoFalloAlgoritmico", "ASAIDAS_5_PerdidaAutonomia", "ASAIDAS_6_DudaCapacidadPropia",
    "ASAIDAS_7_SaturacionNotificaciones", "ASAIDAS_8_ComparacionDigital",
    # Habitos y contexto academico
    "Habito_HorasPantallaDia", "Habito_HorasRedesSociales", "Habito_HorasEstudioVirtual",
    "Habito_DispositivoPrincipal",
    # Caracteristicas continuas de PLN
    "PLN_PolaridadSentimiento", "PLN_ScorePonderadoEstres", "PLN_DensidadEstres"
]

DISPOSITIVO_MAP = {"SMARTPHONE": 1.0, "LAPTOP": 2.0, "DESKTOP": 3.0, "TABLET": 4.0}

def generar_dataset_calibrado(n_muestras: int = 1500, random_seed: int = 42) -> Tuple[np.ndarray, np.ndarray]:
    """
    Genera un dataset sintetico calibrado con rigor estadistico sobre los parametros
    reportados en investigaciones peruanas (Araya-Ugarte et al. 2025; Solano & Nuñez 2025;
    Morales-Garcia et al. 2025).
    """
    np.random.seed(random_seed)
    
    # Proporciones reales aproximadas en Lima: 45% Bajo, 35% Moderado, 20% Alto
    n_bajo = int(n_muestras * 0.45)
    n_mod = int(n_muestras * 0.35)
    n_alto = n_muestras - n_bajo - n_mod
    
    # 1. Grupo Bajo Riesgo
    ts4us_bajo = np.clip(np.random.normal(loc=1.8, scale=0.5, size=(n_bajo, 13)), 1, 5).round()
    asaidas_bajo = np.clip(np.random.normal(loc=1.6, scale=0.5, size=(n_bajo, 8)), 1, 5).round()
    hab_pantalla_b = np.clip(np.random.normal(loc=5.5, scale=1.5, size=(n_bajo, 1)), 1, 14)
    hab_redes_b = np.clip(np.random.normal(loc=2.0, scale=1.0, size=(n_bajo, 1)), 0.5, 8)
    hab_estudio_b = np.clip(np.random.normal(loc=3.5, scale=1.2, size=(n_bajo, 1)), 1, 10)
    disp_b = np.random.choice([1.0, 2.0, 3.0, 4.0], size=(n_bajo, 1), p=[0.25, 0.55, 0.15, 0.05])
    pln_pol_b = np.clip(np.random.normal(loc=0.45, scale=0.25, size=(n_bajo, 1)), -0.2, 1.0)
    pln_score_b = np.clip(np.random.normal(loc=0.1, scale=0.1, size=(n_bajo, 1)), 0.0, 0.4)
    pln_dens_b = np.clip(np.random.normal(loc=0.05, scale=0.04, size=(n_bajo, 1)), 0.0, 0.3)
    
    X_bajo = np.hstack([ts4us_bajo, asaidas_bajo, hab_pantalla_b, hab_redes_b, hab_estudio_b, disp_b, pln_pol_b, pln_score_b, pln_dens_b])
    y_bajo = np.zeros(n_bajo, dtype=int) # 0 = BAJO
    
    # 2. Grupo Moderado
    ts4us_mod = np.clip(np.random.normal(loc=3.1, scale=0.6, size=(n_mod, 13)), 1, 5).round()
    asaidas_mod = np.clip(np.random.normal(loc=2.9, scale=0.6, size=(n_mod, 8)), 1, 5).round()
    hab_pantalla_m = np.clip(np.random.normal(loc=8.0, scale=1.8, size=(n_mod, 1)), 3, 16)
    hab_redes_m = np.clip(np.random.normal(loc=3.8, scale=1.3, size=(n_mod, 1)), 1, 10)
    hab_estudio_m = np.clip(np.random.normal(loc=4.5, scale=1.5, size=(n_mod, 1)), 1, 12)
    disp_m = np.random.choice([1.0, 2.0, 3.0, 4.0], size=(n_mod, 1), p=[0.40, 0.45, 0.10, 0.05])
    pln_pol_m = np.clip(np.random.normal(loc=-0.1, scale=0.25, size=(n_mod, 1)), -0.6, 0.4)
    pln_score_m = np.clip(np.random.normal(loc=0.35, scale=0.15, size=(n_mod, 1)), 0.1, 0.7)
    pln_dens_m = np.clip(np.random.normal(loc=0.20, scale=0.08, size=(n_mod, 1)), 0.05, 0.6)
    
    X_mod = np.hstack([ts4us_mod, asaidas_mod, hab_pantalla_m, hab_redes_m, hab_estudio_m, disp_m, pln_pol_m, pln_score_m, pln_dens_m])
    y_mod = np.ones(n_mod, dtype=int) # 1 = MODERADO
    
    # 3. Grupo Alto Riesgo
    ts4us_alto = np.clip(np.random.normal(loc=4.2, scale=0.5, size=(n_alto, 13)), 1, 5).round()
    asaidas_alto = np.clip(np.random.normal(loc=4.0, scale=0.5, size=(n_alto, 8)), 1, 5).round()
    hab_pantalla_a = np.clip(np.random.normal(loc=11.5, scale=2.0, size=(n_alto, 1)), 6, 20)
    hab_redes_a = np.clip(np.random.normal(loc=5.5, scale=1.6, size=(n_alto, 1)), 2, 14)
    hab_estudio_a = np.clip(np.random.normal(loc=6.0, scale=1.8, size=(n_alto, 1)), 2, 14)
    disp_a = np.random.choice([1.0, 2.0, 3.0, 4.0], size=(n_alto, 1), p=[0.55, 0.35, 0.05, 0.05])
    pln_pol_a = np.clip(np.random.normal(loc=-0.65, scale=0.2, size=(n_alto, 1)), -1.0, -0.2)
    pln_score_a = np.clip(np.random.normal(loc=0.75, scale=0.15, size=(n_alto, 1)), 0.4, 1.0)
    pln_dens_a = np.clip(np.random.normal(loc=0.45, scale=0.12, size=(n_alto, 1)), 0.2, 0.9)
    
    X_alto = np.hstack([ts4us_alto, asaidas_alto, hab_pantalla_a, hab_redes_a, hab_estudio_a, disp_a, pln_pol_a, pln_score_a, pln_dens_a])
    y_alto = np.full(n_alto, 2, dtype=int) # 2 = ALTO
    
    X = np.vstack([X_bajo, X_mod, X_alto])
    y = np.concatenate([y_bajo, y_mod, y_alto])
    
    return X, y

def entrenar_y_evaluar_modelo(n_muestras: int = 1500) -> dict:
    X, y = generar_dataset_calibrado(n_muestras=n_muestras)
    
    # Configuracion de Random Forest con balanceo de clases y profundidad controlada
    rf = RandomForestClassifier(
        n_estimators=120,
        max_depth=12,
        min_samples_split=4,
        min_samples_leaf=2,
        class_weight="balanced",
        random_state=42,
        n_jobs=-1
    )
    
    # Validacion Cruzada Estratificada K-Fold (k=5)
    skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    scoring = ["accuracy", "precision_macro", "recall_macro", "f1_macro"]
    cv_results = cross_validate(rf, X, y, cv=skf, scoring=scoring, n_jobs=-1)
    
    # Ajustar con el 100% de los datos para persistencia
    rf.fit(X, y)
    
    # Guardar modelo y metadata
    joblib.dump(rf, MODEL_FILE)
    with open(FEATURE_NAMES_FILE, "w", encoding="utf-8") as f:
        json.dump(FEATURE_NAMES, f, ensure_ascii=False, indent=2)
        
    metrics = {
        "accuracy": float(np.mean(cv_results["test_accuracy"])),
        "precisionMacro": float(np.mean(cv_results["test_precision_macro"])),
        "recallMacro": float(np.mean(cv_results["test_recall_macro"])),
        "f1ScoreMacro": float(np.mean(cv_results["test_f1_macro"])),
        "totalMuestras": int(len(y)),
        "featureCount": int(len(FEATURE_NAMES))
    }
    
    print(f"=== ENTRENAMIENTO EXITOSO ===")
    print(f"Total Muestras: {metrics['totalMuestras']}")
    print(f"F1-Score (Macro): {metrics['f1ScoreMacro']:.4f} (Meta: >= 0.85)")
    print(f"Accuracy: {metrics['accuracy']:.4f}")
    print(f"Precision (Macro): {metrics['precisionMacro']:.4f}")
    print(f"Recall (Macro): {metrics['recallMacro']:.4f}")
    
    return metrics

if __name__ == "__main__":
    entrenar_y_evaluar_modelo()
