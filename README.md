# FitNova AI

FitNova is an adaptive personal-fitness application with a cinematic React/vinext frontend and a Java 21 Spring Boot API. Authenticated fitness records are owned by Spring Boot and persisted in MySQL; the browser never connects to the database or receives backend secrets.

## Architecture

```text
React + TypeScript (vinext)
        │ REST / HTTPS + JWT
Spring Boot controllers
        │
Service layer (auth, profile, readiness, workouts, activity)
        │
Spring Data JPA / Hibernate
        │
MySQL database: fitnova
```

Flyway owns the schema. H2 is used only by isolated automated tests. Readiness and progress calculations are deterministic Java code. Camera access is real, but form scoring stays disabled until a validated pose-estimation model and deterministic exercise rules are installed. Gemini-backed features stay unavailable unless the backend is given a valid key; no browser code calls Gemini.

## Requirements

- Node.js 22.13+
- Java 21+
- MySQL 8+

## Database

Create a dedicated database and account:

```sql
create database fitnova character set utf8mb4 collate utf8mb4_unicode_ci;
create user 'fitnova'@'localhost' identified by 'choose-a-strong-password';
grant all privileges on fitnova.* to 'fitnova'@'localhost';
```

Flyway applies `backend/src/main/resources/db/migration` automatically. Hibernate uses `ddl-auto=validate` and does not create production tables.

## Environment

Copy `.env.example` to a local, ignored environment file or configure the variables in your shell/deployment platform.

Frontend:

- `NEXT_PUBLIC_API_BASE_URL` — normally `http://localhost:8080/api`

Backend-only:

- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `JWT_SECRET` — at least 32 random characters; mandatory outside the local MySQL profile
- `CORS_ORIGIN` — comma-separated allowed frontend origins
- `GEMINI_API_KEY` — optional until AI Kitchen/Coach is enabled

Never prefix backend secrets with `NEXT_PUBLIC_`.

## Run locally

Copy `.env.example` to `.env.local` and fill in the local database settings. Then start the frontend and backend together from the repository root:

```powershell
npm install
npm run dev
```

The startup command launches the Spring Boot API on `http://localhost:8080` and the FitNova site on `http://localhost:3000`. To run them separately instead:

```powershell
npm run dev:backend
npm run dev:frontend
```

Open `http://localhost:3000`, create an account, complete onboarding, submit readiness, and generate a workout.

## Production deployment

FitNova is deployed on Railway as one public container: Nginx routes the website to the Vinext server and `/api` requests to Spring Boot. A managed Railway MySQL service stores application data.

- Production site: `https://fitnova-production-c221.up.railway.app`
- Health check: `https://fitnova-production-c221.up.railway.app/actuator/health`
- Container definition: `Dockerfile`
- Railway configuration: `railway.toml`

The production service requires `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, and `SPRING_PROFILES_ACTIVE=mysql`. Railway supplies the database values through references to its MySQL service. Add `GEMINI_API_KEY` as a sealed Railway variable only when AI Kitchen and Ask FitNova should be enabled.

## Validation

```powershell
npm run lint
npm run build
cd backend
.\mvnw.cmd test
```

Backend tests run Flyway against an isolated H2 database in MySQL compatibility mode. Production must use managed MySQL, HTTPS, a strong `JWT_SECRET`, and exact CORS origins.

## Current honest capability boundaries

- Form Coach requests camera permission and renders the real camera feed. It does not invent pose landmarks, reps, or scores while a pose model is absent.
- AI Kitchen and Ask FitNova do not simulate Gemini responses while the server integration/key is unavailable.
- HRV and resting heart rate appear only when manually supplied; FitNova never claims to measure them.
- Dashboard and progress screens show loading, empty, error, or real persisted values—never authenticated demo records.
