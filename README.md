# Mini-CTI: Cyber Threat Intelligence Portal

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.4-blue.svg)](https://react.dev/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Mini-CTI is a specialized portal for Cyber Threat Intelligence, designed to help security professionals monitor vulnerabilities and investigate potential threats. This project serves as a comprehensive full-stack application demonstrating my ability to build secure, data-driven security tools with modern web technologies.

---

### 🚀 Key Features

*   **🛡️ Secure Authentication:** Robust user registration and login system using **JWT (JSON Web Tokens)** for stateless session management.
*   **🔍 IP Reputation Lookup:** Integration with the **VirusTotal API** to retrieve real-time threat intelligence and security reports for any IPv4 address.
*   **📊 CISA KEV Integration:** Automated and manual ingestion of the CISA **Known Exploited Vulnerabilities** (KEV) catalog.
*   **⚙️ Administrative Control:** Restricted endpoints for administrators to manually trigger database updates and manage threat data.
*   **📖 Interactive API Documentation:** Fully documented RESTful API using **OpenAPI 3 (Swagger UI)**.
*   **⚡ Modern Frontend:** Responsive, single-page application built with **React 19** and styled with **Tailwind CSS 4**.

---

### 🛠️ Tech Stack

#### Backend
- **Java 21** / **Spring Boot 3.5**
- **Spring Security** — stateless JWT authentication
- **Spring Data JPA** — PostgreSQL persistence
- **MapStruct** — Type-safe DTO mapping
- **Lombok**, **Jakarta Validation**, **Spring Dotenv**
- **OpenAPI / Swagger UI** — interactive API docs

#### Frontend
- **React 19** (Vite)
- **Tailwind CSS 4**
- **React Router 7**
- **TypeScript**

---

### 📥 Requirements

- **JDK 21** or higher
- **Node.js 18+** & **npm**
- **PostgreSQL 15+**
- **VirusTotal API Key** (Free tier available)

---

### 🗄️ Database Setup

Create a PostgreSQL database and user:

```sql
CREATE DATABASE minicti;
CREATE USER cf9 WITH PASSWORD 'Coding571!';
GRANT ALL PRIVILEGES ON DATABASE minicti TO cf9;
```

> Note: The application uses `spring.jpa.hibernate.ddl-auto=validate` in dev, but for the first run you might need to change it to `update` or manually create the schema if not using a migration tool like Flyway.

---

### ⚙️ Configuration

The active profile is `dev` by default. Settings live in `src/main/resources/application-dev.properties`.

| Property | Default (dev) | Override via |
|---|---|---|
| DB host | `localhost` | `DB_HOST` |
| DB port | `5432` | `DB_PORT` |
| DB name | `minicti` | `DB_NAME` |
| DB user | `cf9` | `DB_USER` |
| DB password | `Coding571!` | `DB_PASS` |
| JWT secret | *(dev value)* | `app.security.secret-key` |
| JWT expiration | `10800000` ms (3h) | `app.security.jwt-expiration` |
| VirusTotal API Key | - | `VIRUSTOTAL_API_KEY` |
| VirusTotal Base URL | - | `VIRUSTOTAL_BASE_URL` |
| CISA KEV Base URL | - | `CISAKEV_BASE_URL` |

---

### 🚀 Build & Run

#### 1. Backend Setup
Create a `.env` file in the project root:
```env
DB_URL=jdbc:postgresql://localhost:5432/minicti
DB_USERNAME=cf9
DB_PASSWORD=Coding571!
VIRUSTOTAL_API_KEY=your_vt_api_key
VIRUSTOTAL_BASE_URL=https://www.virustotal.com/api/v3/
CISAKEV_BASE_URL=https://www.cisa.gov/sites/default/files/feeds/known_exploited_vulnerabilities.json
JWT_SECRET=your_secret_key_at_least_32_characters
```

```bash
./gradlew bootRun        # Start the backend
./gradlew test           # Run tests
```

#### 2. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

---

### 🐳 Docker Deployment

The project is fully containerized using **Docker** and **Docker Compose** for easy setup and consistent environments across development and production.

#### 🛠️ Services Overview

-   **`db` (PostgreSQL):** Uses `postgres:18-alpine`. Persistent data is stored in the `postgres_data` volume. It's configured with a health check to ensure the database is ready before the application starts.
-   **`app` (Spring Boot):** The backend service built using `amazoncorretto:21`. It connects to the `db` service and depends on its health.
-   **`frontend` (React + Nginx):** A multi-stage build that compiles the React 19 application and serves the static files using **Nginx 1.29-alpine**. It listens on port `80`.

#### 🚀 Running with Docker Compose

1.  **Ensure you have Docker and Docker Compose installed.**
2.  **Build the backend JAR:**
    ```bash
    ./gradlew clean bootJar
    ```
3.  **Start all services:**
    ```bash
    docker-compose up -d --build
    ```
4.  **Access the applications:**
    -   **Frontend:** `http://localhost`
    -   **Backend API:** `http://localhost:8080`
    -   **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

### 📘 API Overview

Base path: `/api/v1`

#### Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/register` | Public | Register a new user |
| POST | `/users/login` | Public | Log in and receive a JWT |

**Login Request:**
```json
{ "email": "user@example.com", "password": "Password1!" }
```

**Response:**
```json
{ "token": "<jwt_token>" }
```

#### IP Reputation (VirusTotal)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/ip-lookup/{ipAddress}` | Bearer | Get reputation for an IPv4 address |

#### CISA KEV (Vulnerabilities)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/cisa-kev` | Bearer | List vulnerabilities (paginated) |
| POST | `/cisa-kev/update` | ADMIN | Manually trigger database update |

---

### 🔐 Password Policy

Passwords must be at least 8 characters and contain:
- One digit
- One lowercase letter
- One uppercase letter
- One special character (`!@#$%^&*?<>`)

---

### 🚨 Error Responses

All errors return a JSON body with a consistent structure.

| HTTP Status | Cause |
|---|---|
| 400 | Validation error or invalid IP format |
| 401 | Missing or invalid JWT token |
| 403 | Insufficient permissions (e.g. non-ADMIN on `/update`) |
| 404 | Resource not found |
| 409 | User already exists |
| 500 | Internal server error |
| 503 | VirusTotal API or CISA API unavailable |

---

### 📁 Project Structure

```text
mini-cti/
├── src/main/java/com/mini/cti/
│   ├── api/            # REST Controllers
│   ├── authentication/  # JWT & Security Service
│   ├── core/           # Exception handling & Globals
│   ├── dto/            # Records for Data Transfer
│   ├── model/          # JPA Entities
│   ├── mapper/         # Data Mapping (MapStruct & Manual)
│   ├── repository/     # Spring Data Repositories
│   └── service/        # Business Logic
├── frontend/           # React 19 + Tailwind 4 App
└── build.gradle        # Backend dependencies
```

---

### 📖 API Docs (Swagger UI)

Explore the interactive API documentation at:
`http://localhost:8080/swagger-ui/index.html`

---

### 🔮 Future Enhancements

- [x] **Dockerization:** Containerizing the app for easier deployment.
- [ ] **Email Notifications:** Alerting users on new critical CVEs.
- [ ] **Dashboard Visualization:** Adding charts and metrics.
- [ ] **Redis Caching:** Implementing caching for IP lookup requests.

---

### 👤 Author
**Junior Software Engineer**
*Passionate about Backend Development, Cybersecurity, and building tools that make a difference.*

---
License: MIT
