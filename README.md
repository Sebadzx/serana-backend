# Plataforma Web de Detección de Ansiedad Digital con Machine Learning Híbrido

**Proyecto de Tesis para Licenciatura en Ingeniería de Sistemas de Información**  
**Universidad Peruana de Ciencias Aplicadas (UPC)**  
*Autores:* Sebastian Andre Nuñez Alvarez & Darwin Karl Salazar Gutiérrez  
*Asesor:* Dr. José Luis Santisteban Pazos  

---

## 1. Visión General del Sistema

Este repositorio implementa la arquitectura backend y analítica para la detección temprana y seguimiento del tecnoestrés y la ansiedad digital en estudiantes universitarios de Lima Metropolitana. 

El sistema combina:
- **Core Backend Transaccional (Spring Boot 3 / Java 21):** Gestión de identidades seguras (JWT), control de acceso por roles (`ROLE_ESTUDIANTE`, `ROLE_BIENESTAR`, `ROLE_ADMIN`), consentimiento informado y ejercicio de derechos ARCO (Ley N° 29733), persistencia en PostgreSQL 15 y agregación analítica institucional.
- **Microservicio de Machine Learning Híbrido (Python / FastAPI):**
  - **Módulo PLN de Argot Universitario Limeño:** Detección de estresores y normalización léxica («quemado», «jale», «tranca», «ciclarse»).
  - **Random Forest Multimodal:** Clasificación en niveles `BAJO`, `MODERADO` y `ALTO` con métrica objetivo $F1\text{-Score} \ge 85\%$.
  - **Capa de Reglas Clínicas Expertas:** Detección de crisis y prevención de falsos negativos.
  - **Explicabilidad (Feature Importance):** Desglose transparente de los factores causales para retroalimentación clínica y pautas de TCC Digital (iCBT).

---

## 2. Arquitectura de Despliegue

```
                       [ Cliente Web / SPA (Angular) ]
                                      │
                                      ▼ HTTPS (Puerto 8080)
┌────────────────────────────────────────────────────────────────────────────┐
│                    SERANA BACKEND (Spring Boot 3.x)                        │
│                                                                            │
│  [AuthController]  [ConsentimientoController]  [EvaluacionController]     │
│  [ArcoController]  [DashboardBienestarController]                         │
│                                                                            │
│       │ JPA / Hibernate                              │ WebClient (REST)    │
│       ▼                                              ▼ Puerto 8001         │
│  ┌───────────────────────┐             ┌────────────────────────────────┐  │
│  │   PostgreSQL 15       │             │ ML SERVICE (FastAPI / Python)  │  │
│  │ (ACID + JSONB + KMSp) │             │  - NLP Argot Limeño            │  │
│  └───────────────────────┘             │  - Random Forest Classifier    │  │
│                                        │  - Reglas Clínicas Expertas    │  │
│                                        │  - MLOps & Reentrenamiento     │  │
│                                        └────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Estructura de Directorios

```
/
├── backend-spring/              # Core Backend en Spring Boot 3
│   ├── src/main/java/com/upc/serana/
│   │   ├── config/              # WebClient, DataInitializer
│   │   ├── controller/          # Endpoints REST (Auth, Consentimiento, Evaluaciones, Dashboard, ARCO)
│   │   ├── dto/                 # DTOs y contratos JSON
│   │   ├── entity/              # Entidades JPA (Usuario, Consentimiento, Evaluacion, Resultado, etc.)
│   │   ├── repository/          # Interfaces Spring Data JPA
│   │   ├── security/            # Spring Security 6, Filtro JWT, PasswordEncoder
│   │   └── service/             # Logica de negocio e integracion HTTP con ML
│   ├── src/main/resources/      # application.yml
│   ├── Dockerfile               # Compilacion multi-stage Maven + Temurin 21
│   └── pom.xml
│
├── ml_service/                  # Microservicio Analítico en Python
│   ├── app/
│   │   ├── expert_rules.py      # Capa de Reglas Clinicas Expertas e iCBT
│   │   ├── hybrid_model.py      # Orquestador del modelo hibrido y Feature Importance
│   │   ├── main.py              # Endpoints FastAPI (/predict, /health, /retrain)
│   │   ├── nlp_preprocessor.py  # Procesador PLN y diccionario de argot universitario limeño
│   │   └── schemas.py           # Modelos de datos Pydantic
│   ├── Dockerfile               # Contenedor Python 3.11-slim
│   ├── requirements.txt         # Dependencias (FastAPI, Scikit-Learn, Pandas, etc.)
│   ├── test_ml_service.py       # Suite de pruebas unitarias
│   └── train_model.py           # Script de entrenamiento y validacion K-Fold (k=5)
│
├── docker-compose.yml           # Orquestador de PostgreSQL + Backend + ML Service
└── README.md
```

---

## 4. Puesta en Marcha Rápida (Docker Compose)

Para levantar todos los servicios con una sola instrucción:

```bash
docker compose up --build
```

Esto desplegará:
1. **PostgreSQL 15:** `localhost:5432` (Base de datos: `serana_db`, Usuario: `postgres`, Password: `password123`)
2. **ML Service (FastAPI):** `http://localhost:8001` (Documentación Swagger interactiva en `http://localhost:8001/docs`)
3. **Core Backend (Spring Boot):** `http://localhost:8080` (Endpoints bajo `/api/**`)

---

## 5. Ejecución en Entorno Local (Sin Docker)

### Requisitos Previos:
- Java 21 JDK y Maven 3.9+
- Python 3.10+
- PostgreSQL 15 corriendo localmente con la base de datos `serana_db`.

### 5.1. Levantar el Microservicio de ML:
```bash
cd ml_service
pip install -r requirements.txt
python train_model.py
uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload
```

### 5.2. Levantar el Backend Spring Boot:
```bash
cd backend-spring
mvn clean spring-boot:run
```

---

## 6. Usuarios Base Sembrados (DataInitializer)

Al arrancar por primera vez, el backend crea automáticamente:
- **Estudiante de Demostración:**
  - Email: `u20201f737@upc.edu.pe`
  - Password: `Estudiante2026*`
  - Rol: `ROLE_ESTUDIANTE`
- **Profesional de Bienestar Universitario:**
  - Email: `bienestar@upc.edu.pe`
  - Password: `Bienestar2026*`
  - Rol: `ROLE_BIENESTAR`
- **Administrador:**
  - Email: `admin@serana.upc.edu.pe`
  - Password: `AdminSerana2026*`
  - Rol: `ROLE_ADMIN`

---

## 7. Flujo de Endpoints y Ejemplos de Uso

### 1. Iniciar Sesión (`POST /api/auth/login`)
```json
{
  "email": "u20201f737@upc.edu.pe",
  "password": "Estudiante2026*"
}
```
*Respuesta:* Retorna el token JWT que debe incluirse como header `Authorization: Bearer <TOKEN>` en las siguientes solicitudes.

### 2. Aceptar Consentimiento Informado (`POST /api/consentimiento/firmar`)
```json
{
  "aceptado": true,
  "versionPolitica": "v1.0-Ley29733"
}
```

### 3. Iniciar Evaluación (`POST /api/evaluaciones/iniciar`)
Crea una evaluación en estado `EN_PROGRESO` asociada al estudiante autenticado.

### 4. Completar Evaluación con Inferencia (`POST /api/evaluaciones/{id}/completar`)
```json
{
  "ts4usRespuestas": [4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 5, 4, 4],
  "asaidasRespuestas": [4, 5, 4, 3, 4, 4, 3, 4],
  "horasPantallaDia": 10.5,
  "horasRedesSociales": 4.0,
  "horasEstudioVirtual": 6.0,
  "dispositivoPrincipal": "LAPTOP",
  "textoLibre": "Siento que paro quemado todo el día frente a la computadora, ya no doy más con los parciales y tengo pánico de jalar mis cursos."
}
```

*Respuesta:* Retorna el nivel de ansiedad clasificado (`ALTO`), la probabilidad de riesgo, los top factores de influencia (*Feature Importance*), los términos de argot detectados y las pautas psicoterapéuticas iCBT recomendadas.

### 5. Métricas para Bienestar Universitario (`GET /api/bienestar/metricas-globales`)
Disponible para usuarios con rol `ROLE_BIENESTAR` o `ROLE_ADMIN`. Retorna la distribución agregada por nivel de riesgo y la lista de alertas críticas activas.

### 6. Ejercicio de Derechos ARCO (`POST /api/privacidad/solicitud-arco`)
```json
{
  "tipoDerecho": "CANCELACION",
  "motivo": "Solicito la disociación irreversible de mis datos personales y de salud mental conforme al Art. 18 de la Ley N° 29733."
}
```
