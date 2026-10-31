import os
import json
from typing import Tuple, Dict, Any
import numpy as np
import pandas as pd
import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import StratifiedKFold, cross_validate, train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix

MODEL_FILE = os.path.join(os.path.dirname(__file__), "model.joblib")
FEATURE_NAMES_FILE = os.path.join(os.path.dirname(__file__), "feature_names.json")
DATASET_FILE = os.path.join(os.path.dirname(__file__), "..", "mentalhealth_dataset.csv")

FEATURE_NAMES = [
    # 13 items TS4US (Escala de Tecnoestrés en Universitarios, rango 1-5)
    "TS4US_1_SobrecargaInfo", "TS4US_2_InvasionVidaPrivada", "TS4US_3_ComplejidadSistemas",
    "TS4US_4_InseguridadDigital", "TS4US_5_IncertidumbreTecnologica", "TS4US_6_FatigaPantalla",
    "TS4US_7_PresionConectividad", "TS4US_8_MultitareaVirtual", "TS4US_9_ObsolescenciaHabilidades",
    "TS4US_10_DependenciaPlataformas", "TS4US_11_SaturacionTareas", "TS4US_12_MiedoDesconexion",
    "TS4US_13_FaltaControlDigital",
    # 8 items ASAIDAS (Escala de Ansiedad y Dependencia a la IA / Académico, rango 1-5)
    "ASAIDAS_1_UsoCompulsivoIA", "ASAIDAS_2_AnsiedadEvaluaciones", "ASAIDAS_3_DelegacionCognitiva",
    "ASAIDAS_4_MiedoFalloAlgoritmico", "ASAIDAS_5_PerdidaAutonomia", "ASAIDAS_6_DudaCapacidadPropia",
    "ASAIDAS_7_SaturacionNotificaciones", "ASAIDAS_8_ComparacionDigital",
    # 5 variables de hábitos y contexto digital (incluyendo horas de sueño)
    "Habito_HorasPantallaDia", "Habito_HorasRedesSociales", "Habito_HorasEstudioVirtual",
    "Habito_SuenoHoras", "Habito_DispositivoPrincipal",
    # 3 características continuas de PLN
    "PLN_PolaridadSentimiento", "PLN_ScorePonderadoEstres", "PLN_DensidadEstres"
]

LABEL_MAP = {"BAJO": 0, "MODERADO": 1, "ALTO": 2}
TARGET_COL = "STAI_Estado_Nivel"


def cargar_dataset() -> Tuple[np.ndarray, np.ndarray]:
    """
    Carga las 29 variables predictoras compatibles con el formulario SERANA y el desenlace
    de referencia independiente STAI-Estado (STAI_Estado_Nivel) directamente desde el dataset.
    No genera variables sintéticas a partir de la etiqueta a predecir.
    """
    if not os.path.exists(DATASET_FILE):
        raise FileNotFoundError(
            f"No se encontró el archivo de datos de entrenamiento en {DATASET_FILE}. "
            "Se requiere un dataset compatible con las 29 características y STAI_Estado_Nivel."
        )

    df = pd.read_csv(DATASET_FILE)
    columnas_faltantes = [c for c in FEATURE_NAMES + [TARGET_COL] if c not in df.columns]
    if columnas_faltantes:
        raise ValueError(
            f"El dataset no contiene las columnas requeridas para el entrenamiento: {columnas_faltantes}"
        )

    X = df[FEATURE_NAMES].to_numpy(dtype=float)
    y_raw = df[TARGET_COL].astype(str).str.upper().str.strip()
    if not set(y_raw.unique()).issubset(set(LABEL_MAP.keys())):
        raise ValueError(f"Valores inesperados en {TARGET_COL}: {y_raw.unique()}")

    y = y_raw.map(LABEL_MAP).to_numpy(dtype=int)
    return X, y


def entrenar_y_evaluar_modelo() -> Dict[str, Any]:
    """
    Entrena el clasificador Random Forest sobre el conjunto de entrenamiento (80%)
    con validación cruzada estratificada (5-Fold) y evalúa sobre un conjunto Hold-Out
    independiente (20%) tomando STAI-Estado como desenlace de referencia.
    """
    X, y = cargar_dataset()

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.20, stratify=y, random_state=42
    )

    rf = RandomForestClassifier(
        n_estimators=120,
        max_depth=10,
        min_samples_split=6,
        min_samples_leaf=3,
        class_weight="balanced",
        random_state=42,
        n_jobs=-1
    )

    skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    scoring = ["accuracy", "precision_macro", "recall_macro", "f1_macro"]
    cv_results = cross_validate(rf, X_train, y_train, cv=skf, scoring=scoring, n_jobs=-1)

    # Ajustar en el set de entrenamiento y evaluar en el set Hold-Out independiente (20%)
    rf.fit(X_train, y_train)
    y_pred_test = rf.predict(X_test)

    holdout_acc = float(accuracy_score(y_test, y_pred_test))
    holdout_prec = float(precision_score(y_test, y_pred_test, average="macro", zero_division=0))
    holdout_rec = float(recall_score(y_test, y_pred_test, average="macro", zero_division=0))
    holdout_f1 = float(f1_score(y_test, y_pred_test, average="macro", zero_division=0))
    conf_mat = confusion_matrix(y_test, y_pred_test).tolist()

    # Guardar el modelo entrenado y el mapeo de las 29 variables
    joblib.dump(rf, MODEL_FILE)
    with open(FEATURE_NAMES_FILE, "w", encoding="utf-8") as f:
        json.dump(FEATURE_NAMES, f, ensure_ascii=False, indent=2)

    metrics = {
        "desenlaceReferencia": "STAI-Estado (Puntos de corte: 20-37 BAJO, 38-44 MODERADO, 45-80 ALTO)",
        "accuracy": round(holdout_acc, 4),
        "precisionMacro": round(holdout_prec, 4),
        "recallMacro": round(holdout_rec, 4),
        "f1ScoreMacro": round(holdout_f1, 4),
        "cv5FoldF1Macro": round(float(np.mean(cv_results["test_f1_macro"])), 4),
        "cv5FoldAccuracy": round(float(np.mean(cv_results["test_accuracy"])), 4),
        "confusionMatrixHoldout": conf_mat,
        "totalMuestras": int(len(y)),
        "muestrasTrain": int(len(y_train)),
        "muestrasHoldoutTest": int(len(y_test)),
        "featureCount": int(len(FEATURE_NAMES))
    }

    print("=" * 72)
    print("=== ENTRENAMIENTO Y EVALUACION CON DESENLACE STAI-ESTADO ===")
    print(f"Total Muestras: {metrics['totalMuestras']} (Train: {metrics['muestrasTrain']}, Hold-Out Test: {metrics['muestrasHoldoutTest']})")
    print(f"5-Fold CV F1-Score (Train): {metrics['cv5FoldF1Macro']:.4f} | Accuracy: {metrics['cv5FoldAccuracy']:.4f}")
    print(f"Hold-Out Test F1-Score:     {metrics['f1ScoreMacro']:.4f} | Accuracy: {metrics['accuracy']:.4f}")
    print(f"Hold-Out Precision (Macro): {metrics['precisionMacro']:.4f} | Recall:   {metrics['recallMacro']:.4f}")
    print(f"Matriz de Confusion (Hold-Out Test): {conf_mat}")
    print("=" * 72)

    return metrics


if __name__ == "__main__":
    entrenar_y_evaluar_modelo()
