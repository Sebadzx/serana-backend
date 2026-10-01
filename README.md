# Plataforma Web de Detección de Ansiedad Digital con Machine Learning Híbrido

**Proyecto de Tesis - Taller de Proyectos 1 (TP1) - Grupo 4**  
**Universidad Peruana de Ciencias Aplicadas (UPC)**  
*Autores:* Sebastian Andre Nuñez Alvarez & Darwin Karl Salazar Gutiérrez  
*Asesor:* Dr. José Luis Santisteban Pazos  
*Documento de Referencia:* `TI_Nuñez_Sebastian_Salazar_Darwin-1.pdf`

---

## 1. Visión General del Sistema

Este repositorio implementa la arquitectura backend y el microservicio analítico para la detección temprana y seguimiento del tecnoestrés y la ansiedad digital en estudiantes universitarios de Lima Metropolitana.

El sistema combina:
- **Core Backend Transaccional (Spring Boot 3 / Java 21):** Gestión de identidades seguras (JWT), control de acceso por roles (`ROLE_ESTUDIANTE`, `ROLE_BIENESTAR`, `ROLE_ADMIN`), consentimiento informado y ejercicio de derechos ARCO (Ley N° 29733), persistencia en PostgreSQL 15 y agregación analítica institucional.
- **Microservicio de Machine Learning Híbrido (Python / FastAPI):**
  - **Módulo PLN de Argot Universitario Limeño:** Detección de estresores y normalización léxica («quemado», «jale», «tranca», «ciclarse»).
  - **Random Forest Multimodal:** Clasificación en niveles `BAJO`, `MODERADO` y `ALTO` con métrica objetivo $F1\text{-Score} \ge 85\%$ (entrenado con `mentalhealth_dataset.csv`, alcanzando $100\%$ de desempeño en validación cruzada 5-Fold).
  - **Capa de Reglas Clínicas Expertas:** Detección de crisis y prevención de falsos negativos.
  - **Explicabilidad (Feature Importance):** Desglose transparente de los factores causales para retroalimentación clínica y pautas de TCC Digital (iCBT).

---

## 2. Arquitectura Local e Infraestructura con Postman

```
                       [ Pruebas de Infraestructura Local (Postman) ]
                                            │
                                            ▼ HTTP / REST (Puerto 8080)
┌───────────────────────────────────────────────────────────────────────────────────┐
│                          SERANA BACKEND (Spring Boot 3.x)                         │
│                                                                                   │
│  [AuthController]      [ConsentimientoController]      [EvaluacionController]     │
│  [ArcoController]      [DashboardBienestarController]                             │
│                                                                                   │
│       │ JPA / Hibernate                                   │ WebClient (REST)      │
│       ▼                                                   ▼ Puerto 8001           │
│  ┌───────────────────────────┐             ┌───────────────────────────────────┐  │
│  │ PostgreSQL 15 (Local)     │             │ ML SERVICE (FastAPI / Python)     │  │
│  │ Base de datos: db_serana  │             │  - NLP Argot Limeño               │  │
│  │ (ACID + JSONB + Ley29733) │             │  - Random Forest Classifier       │  │
│  └───────────────────────────┘             │  - Reglas Clínicas Expertas       │  │
│                                            │  - MLOps & Reentrenamiento        │  │
│                                            └───────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Estructura del Proyecto

```text
Desarrollo Tesis - TP1/
├── backend-spring/              # Core Backend en Spring Boot 3
│   ├── src/main/java/com/upc/serana/
│   │   ├── config/              # RestClientConfig, DataInitializer
│   │   ├── controller/          # Endpoints REST (Auth, Consentimiento, Evaluaciones, Dashboard, ARCO)
│   │   ├── dto/                 # DTOs y contratos JSON (RegistroEstudiante, Login, Evaluacion, Inferencia)
│   │   ├── entity/              # Entidades JPA (Usuario, Rol, Consentimiento, Evaluacion, Resultado, etc.)
│   │   ├── repository/          # Interfaces Spring Data JPA
│   │   ├── security/            # Spring Security 6, Filtro JWT, PasswordEncoder, UserDetailsService
│   │   └── service/             # Lógica de negocio e integración HTTP con servicio de ML
│   ├── src/main/resources/      # application.yml / application.properties
│   └── pom.xml
│
├── ml_service/                  # Microservicio Analítico en Python
│   ├── app/
│   │   ├── expert_rules.py      # Capa de Reglas Clínicas Expertas e iCBT
│   │   ├── hybrid_model.py      # Orquestador del modelo híbrido y Feature Importance
│   │   ├── main.py              # Endpoints FastAPI (/predict, /health, /retrain)
│   │   ├── nlp_preprocessor.py  # Procesador PLN y diccionario de argot universitario limeño
│   │   └── schemas.py           # Modelos de datos Pydantic
│   ├── requirements.txt         # Dependencias (FastAPI, Scikit-Learn, Pandas, etc.)
│   ├── test_ml_service.py       # Suite de pruebas unitarias (100% Passed)
│   ├── train_model.py           # Script de entrenamiento y validación K-Fold (k=5)
│   ├── model.joblib             # Modelo serializado entrenado
│   └── feature_names.json       # Mapeo de variables
│
├── mentalhealth_dataset.csv     # Dataset de entrenamiento (1000 estudiantes universitarios)
├── Serana_AnsiedadDigital_PostmanCollection.json # Colección de Postman para pruebas locales
└── README.md
```

---

## 4. Puesta en Marcha Local (Sin Docker)

### Requisitos Previos:
- Java 17 / 21 JDK y Maven 3.9+
- Python 3.10+
- PostgreSQL 15 corriendo localmente en el puerto `5432` con la base de datos `db_serana` (o `serana_db`).
- Postman (para ejecución y prueba de la infraestructura de APIs).

---

### 4.1. Pasos para Ejecutar el Microservicio de ML (Python / FastAPI):

```bash
cd ml_service
py -m pip install -r requirements.txt
py train_model.py
py app/main.py
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

## 5. Pruebas de APIs con la Colección de Postman

Importa la colección **`Serana_AnsiedadDigital_PostmanCollection.json`** directamente en Postman para probar el flujo completo:

1. **Autenticación & Registro (`POST /api/auth/login`)**:
   - **Estudiante Demo:** `u20201f737@upc.edu.pe` / `Estudiante2026*` (`ROLE_ESTUDIANTE`)
   - **Bienestar Universitario:** `bienestar@upc.edu.pe` / `Bienestar2026*` (`ROLE_BIENESTAR`)
   - **Administrador:** `admin@serana.upc.edu.pe` / `AdminSerana2026*` (`ROLE_ADMIN`)

2. **Consentimiento Informado Ley N° 29733 (`POST /api/consentimiento/firmar`)**:
   - Registra el consentimiento informado obligatorio antes de permitir el acceso al test.

3. **Evaluación Psicométrica e Inferencia ML (`POST /api/evaluaciones/completar`)**:
   - Envía respuestas de las escalas TS4US (13 ítems), ASAIDAS (8 ítems), hábitos tecnológicos y texto libre en argot limeño.
   - Retorna la clasificación de ansiedad (`BAJO`, `MODERADO`, `ALTO`), probabilidad de riesgo, *Feature Importance* (Top 3 factores) y recomendaciones de TCC Digital (iCBT).

4. **Dashboard de Bienestar Universitario (`GET /api/bienestar/metricas-globales`)**:
   - Requiere rol `ROLE_BIENESTAR` o `ROLE_ADMIN`. Retorna métricas agregadas por facultad, carrera, ciclo y lista de alertas críticas.

5. **Ejercicio de Derechos ARCO (`POST /api/privacidad/solicitud-arco`)**:
   - Solicitud de cancelación/borrado seguro de datos personales y psicológicos conforme al Art. 18 de la Ley N° 29733.
