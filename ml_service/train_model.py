import os
import json
import joblib
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split, StratifiedKFold
from sklearn.preprocessing import OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestClassifier
from sklearn.dummy import DummyClassifier
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    f1_score,
    accuracy_score,
    precision_score,
    recall_score,
)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_FILE = os.path.join(BASE_DIR, "model.joblib")
FEATURE_NAMES_FILE = os.path.join(BASE_DIR, "feature_names.json")
METRICS_FILE = os.path.join(BASE_DIR, "metrics_summary.json")

# Lista canónica de los 40 predictores validados (22 TMD + 18 SocioDemo / Uso Digital)
TMD_COLS = [f"TMD_Q{i}" for i in range(1, 23)]
SOCIO_18_COLS = [
    "Age", "Semester", "Sex",
    "Family_Expression", "Family_Activities", "Mobile_Use_Rules",
    "Phone_Discrimination", "Phone_Related_Arguments",
    "Type_MD", "Average_Time_Use",
    "Using_Text Messaging_Apps", "Using_Games_Apps", "Using_social media_Apps",
    "Using_Music_Apps", "Using_Videos_Apps", "Using_Ttime-management_Apps",
    "Using_Camara_Apps", "Using_Mail_Apps"
]
FEATURE_COLS = TMD_COLS + SOCIO_18_COLS  # 40 predictores

NOMINAL_COLS = ["Sex", "Type_MD"]
NUMERIC_ORDINAL_COLS = [c for c in FEATURE_COLS if c not in NOMINAL_COLS]

CLASES = ["BAJO", "MODERADO", "ALTO"]
MAPA_ETIQUETAS = {0: "BAJO", 1: "MODERADO", 2: "ALTO"}

def aplicar_capa_reglas_experimentales(X_df: pd.DataFrame, y_pred_rf: np.ndarray, proba_rf: np.ndarray):
    """
    Capa de Reglas Clínicas Experimentales (R1, R2, R3) de SERANA:
    Conserva la predicción del Random Forest y registra de manera auditable
    cualquier modificación con su justificación clínica correspondiente.
    """
    y_hibrido = np.array(y_pred_rf, copy=True)
    reglas_activadas = []

    tmd_sum      = X_df[TMD_COLS].sum(axis=1).values
    q3_conflicto = X_df["TMD_Q3"].values
    q16_control  = X_df["TMD_Q16"].values
    fam_exp      = X_df["Family_Expression"].values
    fam_act      = X_df["Family_Activities"].values
    avg_time     = X_df["Average_Time_Use"].values
    soc_apps     = X_df["Using_social media_Apps"].values

    for idx in range(len(X_df)):
        rf_c = int(y_pred_rf[idx])
        p_bajo, p_mod, p_alto = proba_rf[idx]
        p_max = proba_rf[idx, rf_c]
        regla = "NINGUNA"

        # Regla 1: Sensibilización de Clase Moderada (1) en frontera de incertidumbre con alta exposición (>=3h/día)
        if rf_c != 1 and p_mod >= 0.32 and (p_max - p_mod) <= 0.045 and avg_time[idx] >= 3 and fam_act[idx] <= 3:
            y_hibrido[idx] = 1
            regla = "R1_TRANSICION_MODERADA_ALTA_EXPOSICION"

        # Regla 2: Escalamiento a Clase Alta (2) por hiperconectividad, pérdida de control y baja cohesión familiar
        elif rf_c == 1 and p_alto >= 0.37 and avg_time[idx] >= 3 and soc_apps[idx] == 2 and (q3_conflicto[idx] >= 2 or q16_control[idx] >= 3) and fam_act[idx] <= 2:
            y_hibrido[idx] = 2
            regla = "R2_ESCALAMIENTO_ALTO_HIPERCONECTIVIDAD"

        # Regla 3: Atenuación a Clase Baja (0) por ausencia de dependencia TMD (<=29) y entorno familiar protector
        elif rf_c == 1 and p_bajo >= 0.37 and fam_exp[idx] >= 3 and fam_act[idx] >= 3 and tmd_sum[idx] <= 29:
            y_hibrido[idx] = 0
            regla = "R3_FACTOR_PROTECTOR_FAMILIAR_BAJA_DEPENDENCIA"

        reglas_activadas.append(regla)

    return y_hibrido, reglas_activadas


def cargar_datos(csv_path: str = None) -> tuple[pd.DataFrame, pd.Series]:
    """Carga y preprocesa el dataset real de 1,220 estudiantes universitarios."""
    if csv_path is None:
        posibles_rutas = [
            os.path.join(BASE_DIR, "dataset_tmd_stai.csv"),
            os.path.join(BASE_DIR, "..", "..", "..", "dataset_tmd_stai.csv"),
            r"D:\COSASDARWIN\20262\Taller de Proyectos 1\dataset_tmd_stai.csv",
        ]
        for p in posibles_rutas:
            if os.path.exists(p):
                csv_path = p
                break

    if not csv_path or not os.path.exists(csv_path):
        raise FileNotFoundError(f"No se encontró el dataset en ninguna de las rutas esperadas: {posibles_rutas}")

    df = pd.read_csv(csv_path)

    # 1. Inversión psicométrica de los 10 ítems positivos de STAI-Estado (Spielberger)
    col_item1 = [c for c in df.columns if "State_Anxiety" in c and "item_1" in c][0]
    state_raw_cols = [col_item1] + [f"item_{i}" for i in range(2, 21)]
    df_stai_state = df[state_raw_cols].copy()
    df_stai_state.columns = [f"STAI_S_{i}" for i in range(1, 21)]

    items_positivos = [1, 2, 5, 8, 10, 11, 15, 16, 19, 20]
    for i in items_positivos:
        df_stai_state[f"STAI_S_{i}"] = 5 - df_stai_state[f"STAI_S_{i}"]

    df["STAI_Estado_Puntaje_Corregido"] = df_stai_state.sum(axis=1)
    df["risk_level"] = pd.cut(df["STAI_Estado_Puntaje_Corregido"], bins=[19, 37, 44, 80], labels=[0, 1, 2]).astype(int)

    # 2. 22 ítems TMD (0 a 4)
    col_q1 = [c for c in df.columns if "Q1" in c and "Q10" not in c and "Q11" not in c and "Q12" not in c and "Q13" not in c and "Q14" not in c and "Q15" not in c and "Q16" not in c and "Q17" not in c and "Q18" not in c and "Q19" not in c][0]
    tmd_raw_cols = [col_q1] + [f"Q{i}" for i in range(2, 23)]
    df_tmd_22 = df[tmd_raw_cols].copy()
    df_tmd_22.columns = TMD_COLS

    X = pd.concat([df_tmd_22, df[SOCIO_18_COLS].copy()], axis=1)
    y = df["risk_level"].copy()

    return X, y


def construir_pipeline() -> Pipeline:
    preprocessor = ColumnTransformer(
        transformers=[
            ("nominal", OneHotEncoder(handle_unknown="ignore", sparse_output=False), NOMINAL_COLS),
            ("num_ord", "passthrough", NUMERIC_ORDINAL_COLS)
        ]
    )

    rf_pipe = Pipeline([
        ("preprocessor", preprocessor),
        ("classifier", RandomForestClassifier(
            n_estimators=120,
            max_depth=12,
            min_samples_split=4,
            min_samples_leaf=2,
            class_weight="balanced",
            random_state=42,
            n_jobs=-1
        ))
    ])
    return rf_pipe


def entrenar_y_guardar(csv_path: str = None) -> Pipeline:
    print("=== Entrenando Modelo Híbrido SERANA (40 predictores reales, 1,220 estudiantes) ===")
    X, y = cargar_datos(csv_path)

    X_dev, X_test, y_dev, y_test = train_test_split(X, y, test_size=0.20, random_state=42, stratify=y)
    rf_pipe = construir_pipeline()

    # 1. 5-Fold Cross Validation en Desarrollo (n=976)
    skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    cv_records = {"Dummy": [], "RF": [], "Hybrid": []}

    dummy_pipe = Pipeline([
        ("preprocessor", rf_pipe.named_steps["preprocessor"]),
        ("classifier", DummyClassifier(strategy="most_frequent"))
    ])

    for tr_idx, va_idx in skf.split(X_dev, y_dev):
        X_tr, y_tr = X_dev.iloc[tr_idx], y_dev.iloc[tr_idx]
        X_va, y_va = X_dev.iloc[va_idx], y_dev.iloc[va_idx]

        dummy_pipe.fit(X_tr, y_tr)
        p_dum = dummy_pipe.predict(X_va)

        rf_pipe.fit(X_tr, y_tr)
        p_rf = rf_pipe.predict(X_va)
        prob_rf = rf_pipe.predict_proba(X_va)
        p_hyb, _ = aplicar_capa_reglas_experimentales(X_va, p_rf, prob_rf)

        for name, p in [("Dummy", p_dum), ("RF", p_rf), ("Hybrid", p_hyb)]:
            cv_records[name].append({
                "accuracy": accuracy_score(y_va, p),
                "f1_macro": f1_score(y_va, p, average="macro"),
                "precision_macro": precision_score(y_va, p, average="macro", zero_division=0),
                "recall_macro": recall_score(y_va, p, average="macro", zero_division=0)
            })

    # 2. Ajuste Final en Desarrollo y Evaluación en Hold-Out Test (n=244)
    rf_pipe.fit(X_dev, y_dev)
    test_pred_rf = rf_pipe.predict(X_test)
    test_proba_rf = rf_pipe.predict_proba(X_test)
    test_pred_hyb, test_reglas = aplicar_capa_reglas_experimentales(X_test, test_pred_rf, test_proba_rf)

    # Métricas Test
    acc_rf = accuracy_score(y_test, test_pred_rf)
    f1_rf = f1_score(y_test, test_pred_rf, average="macro")
    prec_rf = precision_score(y_test, test_pred_rf, average="macro", zero_division=0)
    rec_rf = recall_score(y_test, test_pred_rf, average="macro", zero_division=0)

    acc_hyb = accuracy_score(y_test, test_pred_hyb)
    f1_hyb = f1_score(y_test, test_pred_hyb, average="macro")
    prec_hyb = precision_score(y_test, test_pred_hyb, average="macro", zero_division=0)
    rec_hyb = recall_score(y_test, test_pred_hyb, average="macro", zero_division=0)

    cm_rf = confusion_matrix(y_test, test_pred_rf).tolist()
    cm_hyb = confusion_matrix(y_test, test_pred_hyb).tolist()

    # Bootstrap Delta F1 (B=2000)
    y_test_np = y_test.values
    rng = np.random.default_rng(42)
    boot_deltas = [
        f1_score(y_test_np[b], test_pred_hyb[b], average="macro") - f1_score(y_test_np[b], test_pred_rf[b], average="macro")
        for b in (rng.integers(0, len(y_test_np), size=len(y_test_np)) for _ in range(2000))
    ]
    delta_obs = f1_hyb - f1_rf
    ci_lower = float(np.percentile(boot_deltas, 2.5))
    ci_upper = float(np.percentile(boot_deltas, 97.5))

    mod_test = (test_pred_rf != test_pred_hyb)
    corr_test = mod_test & (test_pred_rf != y_test_np) & (test_pred_hyb == y_test_np)
    err_test  = mod_test & (test_pred_rf == y_test_np) & (test_pred_hyb != y_test_np)

    # Feature Importance Gini
    ohe_names = list(rf_pipe.named_steps["preprocessor"].named_transformers_["nominal"].get_feature_names_out(NOMINAL_COLS))
    encoded_feature_names = ohe_names + NUMERIC_ORDINAL_COLS
    importances = rf_pipe.named_steps["classifier"].feature_importances_
    feat_df = pd.DataFrame({"feature": encoded_feature_names, "gini": importances}).sort_values("gini", ascending=False)
    top_15_features = feat_df.head(15).to_dict(orient="records")

    # Guardar modelo completo (pipeline) y metadatos
    joblib.dump(rf_pipe, MODEL_FILE)
    with open(FEATURE_NAMES_FILE, "w", encoding="utf-8") as f:
        json.dump(FEATURE_COLS, f, indent=2, ensure_ascii=False)

    summary = {
        "dataset_size": len(X),
        "n_dev": len(X_dev),
        "n_test": len(X_test),
        "num_features": len(FEATURE_COLS),
        "cv_results": {
            k: {
                "accuracy_mean": float(pd.DataFrame(v)["accuracy"].mean()),
                "accuracy_std": float(pd.DataFrame(v)["accuracy"].std()),
                "f1_macro_mean": float(pd.DataFrame(v)["f1_macro"].mean()),
                "f1_macro_std": float(pd.DataFrame(v)["f1_macro"].std()),
                "precision_macro": float(pd.DataFrame(v)["precision_macro"].mean()),
                "recall_macro": float(pd.DataFrame(v)["recall_macro"].mean())
            }
            for k, v in cv_records.items()
        },
        "test_results": {
            "random_forest": {
                "accuracy": round(acc_rf, 4),
                "f1_macro": round(f1_rf, 4),
                "precision_macro": round(prec_rf, 4),
                "recall_macro": round(rec_rf, 4),
                "confusion_matrix": cm_rf
            },
            "hybrid": {
                "accuracy": round(acc_hyb, 4),
                "f1_macro": round(f1_hyb, 4),
                "precision_macro": round(prec_hyb, 4),
                "recall_macro": round(rec_hyb, 4),
                "confusion_matrix": cm_hyb
            },
            "delta_f1_macro": round(delta_obs, 4),
            "ci_95": [round(ci_lower, 4), round(ci_upper, 4)],
            "rules_audit": {
                "modified": int(mod_test.sum()),
                "corrected": int(corr_test.sum()),
                "errors_introduced": int(err_test.sum())
            }
        },
        "top_15_features": top_15_features
    }

    with open(METRICS_FILE, "w", encoding="utf-8") as f:
        json.dump(summary, f, indent=2, ensure_ascii=False)

    print(f"Modelo guardado en: {MODEL_FILE}")
    print(f"Nombres de características (40) guardados en: {FEATURE_NAMES_FILE}")
    print(f"Resumen de métricas guardado en: {METRICS_FILE}")
    print(f"Hold-Out Test -> RF Acc: {acc_rf:.4f}, F1: {f1_rf:.4f} | Híbrido Acc: {acc_hyb:.4f}, F1: {f1_hyb:.4f}")
    return rf_pipe


if __name__ == "__main__":
    entrenar_y_guardar()
