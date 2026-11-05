# Plataforma Web de Detección de Ansiedad Digital con Machine Learning Híbrido

**Proyecto de Tesis - Taller de Proyectos 1 (TP1) - Grupo 4**  
**Universidad Peruana de Ciencias Aplicadas (UPC)**  
*Autores:* Sebastian Andre Nuñez Alvarez & Darwin Karl Salazar Gutiérrez  
*Asesor:* Dr. José Luis Santisteban Pazos  
*Documento de Referencia:* `Memoria_Nuñez_Salazar.docx` / `AvancePaper_Nuñez_Salazar.docx`

---

## 1. Visión General del Sistema

Este repositorio implementa la arquitectura backend y el microservicio analítico de **SERANA** para la detección temprana y seguimiento de la ansiedad digital en estudiantes universitarios.

El sistema combina:
- **Core Backend Transaccional (Spring Boot 3 / Java 21):** Gestión de identidades seguras (JWT), control de acceso por roles (`ROLE_ESTUDIANTE`, `ROLE_BIENESTAR`, `ROLE_ADMIN`), consentimiento informado y ejercicio soberano de derechos ARCO (Ley Nº 29733), persistencia en PostgreSQL 15 y agregación analítica institucional.
- **Microservicio de Machine Learning Híbrido (Python 3.14 / FastAPI):**
  - **Módulo PLN de Argot Universitario Limeño:** Detección de estresores y normalización léxica («quemado», «jale», «tranca», «ciclarse») con tolerancia acentual y control sintáctico de negaciones.
  - **Random Forest (40 Predictores Reales):** Clasificación en niveles `BAJO`, `MODERADO` y `ALTO` sobre una cohorte real de 1.220 estudiantes universitarios (`dataset_tmd_stai.csv`), empleando 22 ítems del Test de Dependencia del Móvil (TMD) y 18 variables sociodemográficas y de hábitos digitales, prediciendo la escala STAI-Estado tras corregir psicométricamente la inversión de sus 10 ítems positivos (Ecuación 1).
  - **Capa de Reglas Clínicas Experimentales (R1, R2 y R3):** Ajuste auditable y transparente en fronteras de incertidumbre y perfiles de hiperconectividad severa sin adulterar las probabilidades probabilísticas del Random Forest.
  - **Métricas Reproducibles en Prueba Independiente (Hold-Out, n = 244):** Exactitud de 54,51 % y F1-Macro de 0,4889 (F1 Bajo: 0,6723; Moderado: 0,2574; Alto: 0,5369), superando al clasificador basal (0,2006) y garantizando ausencia de fuga de datos (TRIPOD+AI).
  - **Explicabilidad (Feature Importance Gini):** Desglose transparente de los factores causales principales (edad, semestre, cohesión familiar, uso compulsivo de mensajería TMD_Q5, uso por aburrimiento TMD_Q8 y alteración de sueño TMD_Q6) para retroalimentación clínica y pautas de TCC Digital (iCBT).

---

## 2. Arquitectura Local e Infraestructura

```
                       [ Pruebas de Infraestructura Local (Postman) ]
                                            │
                                            ▼ HTTP / REST (Puerto 8080)
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          SERANA BACKEND (Spring Boot 3.x)                         │
│                                                                                     │
│  [AuthController]      [ConsentimientoController]      [EvaluacionController]       │
│  [ArcoController]      [DashboardBienestarController]                               │
│                                                                                     │
│       │ JPA / Hibernate                                   │ WebClient (REST)        │
│       ▼                                                   ▼ Puerto 8001             │
│  ┌─────────────────────────────┐             ┌───────────────────────────────────┐  │
│  │ PostgreSQL 15               │             │ ML SERVICE (FastAPI / Python)     │  │
│  │ Base de datos: db_serana    │             │  - NLP Argot Universitario        │  │
│  │ (ACID + JSONB + Ley 29733)  │             │  - Random Forest (40 vars)        │  │
│  └─────────────────────────────┘             │  - Reglas Clínicas (R1, R2, R3)   │  │
│                                              │  - MLOps & Reentrenamiento        │  │
│                                              └───────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Estructura del Proyecto

```text
Desarrollo Tesis - TP1/
├── backend-spring/              # Core Backend en Spring Boot 3
│   ├── src/main/java/com/arquiweb/grupo4/serana/
│   │   ├── config/              # RestClientConfig, SecurityConfig
│   │   ├── controllers/         # Endpoints REST (Auth, Consentimiento, Evaluacion, Dashboard, ARCO)
│   │   ├── dtos/                # DTOs y contratos JSON (RegistroEstudiante, Login, Evaluacion, Inferencia)
│   │   ├── entities/            # Entidades JPA (Usuario, Rol, Consentimiento, Evaluacion, Resultado, etc.)
│   │   ├── repositories/        # Interfaces Spring Data JPA
│   │   ├── security/            # Spring Security 6, Filtro JWT, BCrypt, UserDetailsService
│   │   └── servicesimpl/        # Lógica de negocio e integración HTTP con servicio de ML
│   ├── src/main/resources/      # application.properties
│   └── pom.xml
│
├── ml_service/                  # Microservicio Analítico en Python
│   ├── app/
│   │   ├── expert_rules.py      # Capa de Reglas Clínicas Expertas (R1-R3) e iCBT
│   │   ├── hybrid_model.py      # Orquestador del modelo híbrido y Feature Importance
│   │   ├── main.py              # Endpoints FastAPI (/predict, /predict_hybrid, /health, /retrain)
│   │   ├── nlp_preprocessor.py  # Procesador PLN y diccionario de argot universitario
│   │   └── schemas.py           # Modelos de datos Pydantic validados
│   ├── dataset_tmd_stai.csv     # Dataset real de 1.220 universitarios (TMD + SocioDemo + STAI)
│   ├── requirements.txt         # Dependencias (FastAPI, Scikit-Learn, Pandas, etc.)
│   ├── test_ml_service.py       # Suite de pruebas unitarias (100% Passed)
│   ├── train_model.py           # Pipeline de entrenamiento reproducible con 40 predictores
│   ├── model.joblib             # Pipeline serializado con preprocessor y RandomForest
│   ├── feature_names.json       # Lista canónica de los 40 predictores
│   └── metrics_summary.json     # Metadatos y métricas auditables de validación
│
└── README.md
```

---

## 4. Puesta en Marcha Local

### Requisitos Previos:
- Java 17 / 21 / 25 JDK y Maven 3.9+
- Python 3.10+
- PostgreSQL 15 corriendo localmente en el puerto `5432` con la base de datos `db_serana`.

---

### 4.1. Pasos para Ejecutar el Microservicio de ML (Python / FastAPI):

```bash
cd ml_service
py -m pip install -r requirements.txt
py train_model.py
py -m uvicorn app.main:app --host 0.0.0.0 --port 8001
```
> El servicio iniciará en `http://localhost:8001` (Documentación Swagger interactiva en `http://localhost:8001/docs`).

---

### 4.2. Pasos para Ejecutar el Backend (Spring Boot / Java):

```bash
cd backend-spring
mvn clean spring-boot:run
```
> El backend transaccional iniciará en `http://localhost:8080`.

---

## 5. Pruebas y Validación Técnica

1. **Pruebas Unitarias de ML Service**:
   ```bash
   cd ml_service
   py test_ml_service.py
   ```
2. **Reentrenamiento y Métricas**:
   ```bash
   cd ml_service
   py train_model.py
   ```
