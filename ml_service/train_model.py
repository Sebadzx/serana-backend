import os
import json
import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import StratifiedKFold, cross_validate, train_test_split
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    f1_score,
    accuracy_score,
    precision_score,
    recall_score,
)

# Nombres canónicos de las 57 características reales del dataset empírico (1,220 universitarios)
# CERO variables sintéticas y CERO variables musculoesqueléticas.
TMD_FEATURES = [
    "TMD_Q1_LlamadasAtencionUso",
    "TMD_Q2_ExcesoLimiteGasto",
    "TMD_Q3_DiscusionesCosto",
    "TMD_Q4_MasTiempoMensajes",
    "TMD_Q5_MasCincoMensajesDia",
    "TMD_Q6_DormirMenosPorCelular",
    "TMD_Q7_GastoMayorPrevisto",
    "TMD_Q8_UsoPorAburrimiento",
    "TMD_Q9_UsoSituacionesInapropiadas",
    "TMD_Q10_CriticasPorTiempoUso",
    "TMD_Q11_NecesidadUsarTrasPausa",
    "TMD_Q12_IncrementoLlamadas",
    "TMD_Q13_MalestarSiSeAveria",
    "TMD_Q14_NecesidadCrecienteUso",
    "TMD_Q15_MalestarSinCelular",
    "TMD_Q16_NoPoderDejarDeUsarlo",
    "TMD_Q17_IncrementoMensajesWhatsApp",
    "TMD_Q18_RevisarAlDespertar",
    "TMD_Q19_MayorGastoActual",
    "TMD_Q20_IncapazSemanaSinCelular",
    "TMD_Q21_LlamarMensajearPorSoledad",
    "TMD_Q22_ImpulsoUsarAhoraMismo",
]

STAI_TRAIT_FEATURES = [
    "STAI_R21_MalestarEmocional",
    "STAI_R22_NerviosismoInquietud",
    "STAI_R23_InsatisfaccionPersonal",
    "STAI_R24_DeseoFelicidadAjena",
    "STAI_R25_SentimientoFracaso",
    "STAI_R26_FaltaDescanso",
    "STAI_R27_FaltaCalmaSerenidad",
    "STAI_R28_AcumulacionDificultades",
    "STAI_R29_PreocupacionExcesiva",
    "STAI_R30_FaltaFelicidad",
    "STAI_R31_PensamientosPerturbadores",
    "STAI_R32_FaltaAutoconfianza",
    "STAI_R33_InseguridadPersonal",
    "STAI_R34_DificultadDecisiones",
    "STAI_R35_SentimientoIncompetencia",
    "STAI_R36_InsatisfaccionCotidiana",
    "STAI_R37_PensamientosIntrusivos",
    "STAI_R38_RumiacionDecepciones",
    "STAI_R39_InestabilidadEmocional",
    "STAI_R40_TensionPreocupacionesRecientes",
]

HABITOS_SOCIODEMO_FEATURES = [
    "Habito_TipoDispositivo",
    "Habito_TiempoUsoDiario",
    "Habito_LugarUsoFrecuente",
    "Habito_TipoAccesoRed",
    "Habito_ReglasHogarUso",
    "Habito_PercepcionDiscriminacion",
    "Habito_DiscusionesUsoCelular",
    "App_MensajeriaInstantanea",
    "App_Videojuegos",
    "App_RedesSociales",
    "App_Musica",
    "App_VideosStreaming",
    "App_GestionTiempoAcademico",
    "App_Camara",
    "App_CorreoElectronico",
]

FEATURE_NAMES = TMD_FEATURES + STAI_TRAIT_FEATURES + HABITOS_SOCIODEMO_FEATURES

CLASES = ["BAJO", "MODERADO", "ALTO"]
MAPA_CLASES = {"BAJO": 0, "MODERADO": 1, "ALTO": 2}
MAPA_STAI_INGLES = {"Low": "BAJO", "Moderate": "MODERADO", "High": "ALTO"}

ROOT_DIR = os.path.dirname(os.path.dirname(__file__))
EXCEL_PATH = os.path.join(
    ROOT_DIR,
    "Digital Sedentary Behavior, Mobile Device Dependen",
    "Dataset_MobileDependence_MusculoskeletalSymptoms_Depression_Anxiety-zTaMsO.xlsx",
)
CLEAN_CSV_PATH = os.path.join(ROOT_DIR, "mentalhealth_dataset.csv")


def cargar_dataset_real(excel_path: str = EXCEL_PATH):
    """
    Carga directamente el dataset empírico real de 1,220 estudiantes universitarios
    (Hojas: TMD_data, Anxiety_data, SocioDemo_data), excluyendo por completo el
    Cuestionario Nórdico y cualquier variable de postura/molestia musculoesquelética.
    El desenlace independiente (y) es Result_Level_State_Anxiety (STAI-Estado).
    """
    if not os.path.exists(excel_path):
        csv_fallback = CLEAN_CSV_PATH if os.path.exists(CLEAN_CSV_PATH) else os.path.join(BASE_DIR, "mentalhealth_dataset.csv")
        if os.path.exists(csv_fallback):
            df_csv = pd.read_csv(csv_fallback)
            feature_cols = TMD_FEATURES + STAI_TRAIT_FEATURES + HABITOS_SOCIODEMO_FEATURES
            X = df_csv[feature_cols].values.astype(float)
            y = df_csv["STAI_Estado_Nivel"].astype(str).str.strip().map(MAPA_CLASES).values.astype(int)
            return X, y, len(df_csv)
        raise FileNotFoundError(
            f"No se encontró el archivo Excel empírico en {excel_path}."
        )

    xl = pd.ExcelFile(excel_path)
    df_socio = xl.parse("SocioDemo_data")
    df_tmd = xl.parse("TMD_data")
    df_anx = xl.parse("Anxiety_data")

    # Verificar alineación exacta por código de participante (1..1220)
    if not (
        (df_socio["Code"] == df_tmd["Code"]).all()
        and (df_socio["Code"] == df_anx["Code"]).all()
    ):
        raise ValueError("Los códigos de estudiante entre las hojas del Excel no coinciden.")

    # 1. Extraer las 22 respuestas reales de TMD (escala 0..4)
    tmd_raw_cols = [c for c in df_tmd.columns if "Q" in c]
    if len(tmd_raw_cols) != 22:
        raise ValueError(f"Se esperaban 22 columnas en TMD_data, se hallaron {len(tmd_raw_cols)}")
    df_tmd_clean = df_tmd[tmd_raw_cols].copy()
    df_tmd_clean.columns = TMD_FEATURES

    # 2. Extraer las 20 respuestas reales de STAI-Rasgo (item_21..item_40, escala 1..4)
    trait_raw_cols = [
        c
        for c in df_anx.columns
        if c.startswith("Trait_Anxiety")
        or (c.startswith("item_") and int(c.split("_")[1]) >= 21)
    ]
    if len(trait_raw_cols) != 20:
        raise ValueError(f"Se esperaban 20 columnas de STAI-Rasgo, se hallaron {len(trait_raw_cols)}")
    df_trait_clean = df_anx[trait_raw_cols].copy()
    df_trait_clean.columns = STAI_TRAIT_FEATURES

    # 3. Extraer las 15 variables de Hábitos Digitales y Contexto Sociofamiliar (SIN posturas/ergonomía)
    socio_raw_cols = [
        "Type_MD",
        "Average_Time_Use",
        "Areas_Use",
        "Network _Access",
        "Mobile_Use_Rules",
        "Phone_Discrimination",
        "Phone_Related_Arguments",
        "Using_Text Messaging_Apps",
        "Using_Games_Apps",
        "Using_social media_Apps",
        "Using_Music_Apps",
        "Using_Videos_Apps",
        "Using_Ttime-management_Apps",
        "Using_Camara_Apps",
        "Using_Mail_Apps",
    ]
    df_socio_clean = df_socio[socio_raw_cols].copy()
    df_socio_clean.columns = HABITOS_SOCIODEMO_FEATURES

    # Consolidar matriz X de 57 características reales
    X_df = pd.concat([df_tmd_clean, df_trait_clean, df_socio_clean], axis=1)
    X_df = X_df.apply(pd.to_numeric, errors="raise")

    # Desenlace independiente: Result_Level_State_Anxiety (STAI-Estado: 20..80 -> Low/Moderate/High)
    niveles_es = df_anx["Result_Level_State_Anxiety"].astype(str).str.strip().map(MAPA_STAI_INGLES)
    if niveles_es.isna().any():
        raise ValueError("Valores no reconocidos en Result_Level_State_Anxiety.")

    # Guardar versión limpia auditable en CSV (con puntaje y nivel STAI-Estado)
    df_export = X_df.copy()
    df_export.insert(0, "Code", df_socio["Code"])
    df_export["TMD_Puntaje_Total"] = df_tmd["sum"]
    df_export["STAI_Rasgo_Puntaje"] = df_anx["SUM_Trait_Anxiety"]
    df_export["STAI_Estado_Puntaje"] = df_anx["SUM_State_Anxiety"]
    df_export["STAI_Estado_Nivel"] = niveles_es
    df_export.to_csv(CLEAN_CSV_PATH, index=False)

    X = X_df.values.astype(float)
    y = niveles_es.map(MAPA_CLASES).values.astype(int)
    return X, y, len(df_export)


def entrenar_y_guardar():
    X, y, n_rows = cargar_dataset_real(EXCEL_PATH)

    # Partición estratificada 80% Entrenamiento / 20% Prueba Independiente (Hold-Out)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.20, stratify=y, random_state=42
    )

    clf = RandomForestClassifier(
        n_estimators=300,
        max_depth=12,
        min_samples_split=4,
        min_samples_leaf=2,
        class_weight="balanced",
        random_state=42,
        n_jobs=-1,
    )

    # Validación Cruzada Estratificada de 5 Pliegues sobre el conjunto de entrenamiento
    cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    cv_results = cross_validate(
        clf,
        X_train,
        y_train,
        cv=cv,
        scoring=["accuracy", "f1_macro", "precision_macro", "recall_macro"],
    )

    # Ajustar en entrenamiento y evaluar en conjunto Hold-Out (20%)
    clf.fit(X_train, y_train)
    y_pred_test = clf.predict(X_test)

    acc_test = accuracy_score(y_test, y_pred_test)
    f1_test = f1_score(y_test, y_pred_test, average="macro")
    prec_test = precision_score(y_test, y_pred_test, average="macro")
    rec_test = recall_score(y_test, y_pred_test, average="macro")
    cm_test = confusion_matrix(y_test, y_pred_test)

    print("========================================================================")
    print("=== ENTRENAMIENTO EMPÍRICO REAL (1,220 UNIVERSITARIOS) -> STAI-ESTADO ===")
    print(f"Total Muestras Reales: {n_rows} (Train: {len(X_train)}, Hold-Out Test: {len(X_test)})")
    print(f"Total Características: {X.shape[1]} (22 TMD + 20 STAI-Rasgo + 15 Hábitos SocioDemo)")
    print(f"5-Fold CV Accuracy (Train): {cv_results['test_accuracy'].mean():.4f} | F1-Macro: {cv_results['test_f1_macro'].mean():.4f}")
    print(f"Hold-Out Test Accuracy:     {acc_test:.4f} | F1-Macro: {f1_test:.4f}")
    print(f"Hold-Out Precision (Macro): {prec_test:.4f} | Recall:   {rec_test:.4f}")
    print("Matriz de Confusión (Hold-Out Test):", cm_test.tolist())
    print("\nReporte por Clase (Hold-Out Test):")
    print(classification_report(y_test, y_pred_test, target_names=CLASES, digits=4))
    print("========================================================================")

    # Entrenar modelo final con las 1,220 observaciones reales para producción
    clf.fit(X, y)

    model_path = os.path.join(os.path.dirname(__file__), "model.joblib")
    features_path = os.path.join(os.path.dirname(__file__), "feature_names.json")

    joblib.dump(clf, model_path)
    with open(features_path, "w", encoding="utf-8") as f:
        json.dump(FEATURE_NAMES, f, indent=2, ensure_ascii=False)

    print(f"Modelo guardado exitosamente en: {model_path}")
    return clf


if __name__ == "__main__":
    entrenar_y_guardar()
