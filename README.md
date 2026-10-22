# CalcOps 🧮 🚀

A production-ready, containerized RESTful Calculator API built with **Spring Boot 4**, **Java 21**, **Docker**, **Nginx**, and **Jenkins CI/CD**.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-blue.svg)](https://www.docker.com/)
[![Nginx](https://img.shields.io/badge/Nginx-Reverse%20Proxy-green.svg)](https://nginx.org/)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-red.svg)](https://www.jenkins.io/)

---

## 📌 Overview

**CalcOps** provides arithmetic computation endpoints exposed as a REST API. It is engineered with modern Software Production Engineering (DevOps) principles:
- **Clean Architecture**: Controller-Service-DTO pattern with immutable Java records.
- **Robust Exception Handling**: Global exception handler returning standardized JSON error payloads.
- **Containerization**: Multi-stage Docker build optimizing layer caching and image footprint.
- **Least Privilege Security**: Container runs under a dedicated, unprivileged `spring` system user.
- **Orchestration & Reverse Proxy**: Docker Compose setup orchestrating the CalcOps backend with an **Nginx** reverse proxy and automated health checks.
- **Continuous Integration**: Declarative Jenkins pipeline automating compilation, testing, and JUnit report aggregation.

---

## 🏗️ Architecture

```mermaid
flowchart LR
    Client([Client / Browser / curl]) -->|Port 8081| Nginx[Nginx Reverse Proxy]
    subgraph Docker Network [calcops_default]
        Nginx -->|Proxy Pass :8080| CalcOps[CalcOps Spring Boot App]
        CalcOps --> Actuator[/actuator/health]
    end
    Jenkins[Jenkins CI/CD] -.->|Build & Test| CalcOps
```

---

## 📁 Project Structure

```
calcops/
├── .dockerignore                 # Docker build exclusions
├── .gitignore                    # Git exclusions
├── Dockerfile                    # Multi-stage production Dockerfile
├── Jenkinsfile                   # Declarative CI/CD pipeline
├── compose.yaml                  # Docker Compose orchestration (CalcOps + Nginx)
├── mvnw / mvnw.cmd               # Maven wrappers
├── pom.xml                       # Maven dependencies & build configuration
├── nginx/
│   └── default.conf              # Nginx reverse proxy configuration
└── src/
    ├── main/
    │   ├── java/CalcOps/
    │   │   ├── CalcopsApplication.java        # Spring Boot main entry point
    │   │   ├── controller/
    │   │   │   └── CalculatorController.java  # REST API endpoints (/api/v1/*)
    │   │   ├── service/
    │   │   │   └── CalculatorService.java     # Business logic & arithmetic operations
    │   │   ├── dto/
    │   │   │   ├── CalculatorResponse.java    # Success response record
    │   │   │   └── ErrorResponse.java         # Error response record
    │   │   └── exception/
    │   │       └── GlobalExceptionHandler.java # Centralized @RestControllerAdvice
    │   └── resources/
    │       └── application.properties         # Application configuration
    └── test/
        └── java/CalcOps/
            ├── CalcopsApplicationTests.java   # Context load test
            ├── controller/
            │   └── CalculatorControllerTest.java # MockMvc endpoint tests
            └── service/
                └── CalculatorServiceTest.java # Unit tests for arithmetic logic
```

---

## 🚀 API Documentation

All calculation endpoints are exposed under `/api/v1` and accept query parameters `a` and `b` (floating-point numbers).

### 1. Arithmetic Endpoints

| Method | Endpoint | Query Parameters | Description |
|---|---|---|---|
| `GET` | `/api/v1/add` | `a` (double), `b` (double) | Adds `a` and `b` |
| `GET` | `/api/v1/subtract` | `a` (double), `b` (double) | Subtracts `b` from `a` |
| `GET` | `/api/v1/multiply` | `a` (double), `b` (double) | Multiplies `a` by `b` |
| `GET` | `/api/v1/divide` | `a` (double), `b` (double) | Divides `a` by `b` |

#### Successful Response Format (`HTTP 200 OK`):
```json
{
  "operation": "add",
  "a": 10.0,
  "b": 25.0,
  "result": 35.0
}
```

### 2. Error Responses (`HTTP 400 Bad Request`)

CalcOps returns consistent error payloads via `GlobalExceptionHandler`:

* **Division by Zero:**
  ```bash
  GET /api/v1/divide?a=10&b=0
  ```
  ```json
  {
    "error": "DIVISION_BY_ZERO",
    "message": "Cannot divide by zero"
  }
  ```

* **Missing Required Parameter:**
  ```bash
  GET /api/v1/add?a=10
  ```
  ```json
  {
    "error": "MISSING_PARAMETER",
    "message": "Required parameter 'b' is missing"
  }
  ```

* **Invalid Parameter Type:**
  ```bash
  GET /api/v1/add?a=hello&b=5
  ```
  ```json
  {
    "error": "INVALID_PARAMETER",
    "message": "Parameter 'a' must contain a valid number"
  }
  ```

### 3. Actuator & Health Check

* **Health Endpoint**: `GET /actuator/health`
  ```json
  {
    "status": "UP",
    "groups": ["liveness", "readiness"]
  }
  ```

---

## 🛠️ Quick Start & Running Locally

### Prerequisites
- **JDK 21** or later
- **Docker** & **Docker Compose**
- **Git**

### 1. Run with Maven (Local Development)

Clone the repository and run the application locally:
```bash
git clone https://github.com/sidnovates/CalcOps.git
cd CalcOps

# Make wrapper executable
chmod +x mvnw

# Run application on default port 8080
./mvnw spring-boot:run
```

Access the API directly at:
```bash
curl "http://localhost:8080/api/v1/add?a=15&b=30"
```

### 2. Execute Tests

Execute the full unit and mock MVC integration test suite:
```bash
./mvnw clean test
```

Surefire test reports are saved in `target/surefire-reports/`.

---

## 🐳 Docker & Docker Compose Deployment

CalcOps is designed to run in a microservices environment orchestrated with Docker Compose.

### Build and Run with Docker Compose

To build the image and bring up the complete stack (CalcOps + Nginx):
```bash
docker compose up -d --build
```

### Check Service Status & Health
```bash
docker compose ps
```

Expected output:
```text
NAME                IMAGE            COMMAND                  SERVICE   CREATED         STATUS                   PORTS
calcops-calcops-1   calcops:local    "java -jar app.jar"      calcops   Up (healthy)    8080/tcp
calcops-nginx-1     nginx:alpine     "/docker-entrypoint.…"   nginx     Up              0.0.0.0:8081->80/tcp
```

### Test via Nginx Reverse Proxy (Port 8081)

```bash
# Add
curl "http://localhost:8081/api/v1/add?a=42&b=58"

# Subtract
curl "http://localhost:8081/api/v1/subtract?a=100&b=25"

# Multiply
curl "http://localhost:8081/api/v1/multiply?a=6&b=7"

# Divide
curl "http://localhost:8081/api/v1/divide?a=100&b=4"

# Health Check
curl "http://localhost:8081/actuator/health"
```

### Stop Services
```bash
docker compose down
```

---

## 🔄 CI/CD Pipeline (Jenkins)

The project includes a declarative `Jenkinsfile` for automated build verification:

```groovy
pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean compile'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw -B test'
            }

            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}
```

### Pipeline Stages:
1. **Checkout**: Checks out source code from Git repository.
2. **Build**: Compiles source code with `./mvnw -B clean compile`.
3. **Test**: Executes test suite with `./mvnw -B test`.
4. **Post Actions**: Collects and archives JUnit test reports (`target/surefire-reports/*.xml`).

---

## 🔒 Security Best Practices Implemented

- **Non-Root Execution**: Docker container creates and executes under unprivileged system user `spring:spring`.
- **Minimal Runtime Footprint**: Multi-stage build leverages `eclipse-temurin:21-jre-jammy` to exclude build tooling from the runtime image.
- **Layer Caching**: Dependency downloads (`mvn dependency:go-offline`) are isolated from application code changes.
- **Port Segregation**: Backend container is kept internal to the Docker network; host ingress traffic is mediated strictly by Nginx on port 8081.
