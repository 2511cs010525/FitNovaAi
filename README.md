# FitNova AI

FitNova AI is a fitness web app with account signup, onboarding, readiness tracking, workouts, meals, progress, and optional Gemini-powered nutrition and coaching features.

**Live demo:** https://fitnova-ai-snowy.vercel.app/

## Technology

- React, TypeScript, and Vinext frontend
- Java 21 and Spring Boot REST API
- JWT authentication, Spring Data JPA, and Flyway migrations
- PostgreSQL for the Vercel deployment; MySQL for local development
- Gemini API for optional AI features

The browser calls the Spring Boot API; database credentials and the Gemini key stay on the server. The production deployment runs both services in one Docker container behind Nginx.

## Run on a Windows computer

Install Node.js 22.13 or newer, Java 21 or newer, and MySQL 8 or newer. Create an empty MySQL database named `fitnova` and a user with access to it. From the project folder:

1. Copy `.env.example` to `.env.local`.
2. Edit `.env.local`: set `DB_USER`, `DB_PASSWORD`, and a random `JWT_SECRET` of at least 32 characters. The example uses `SPRING_PROFILES_ACTIVE=mysql` and `DB_URL` for the local database. Add your own `GEMINI_API_KEY` only if you want the AI features.
3. In PowerShell, run `npm install` followed by `npm run dev`.
4. Open http://localhost:3000/ and sign up.

The API runs on http://localhost:8080/. Flyway creates the tables automatically. Never upload `.env.local` or any real key or password.

## Tests

Run `npm run lint` and `npm run build` from the project folder. For backend tests, run `cd backend` and then `.\mvnw.cmd test` in PowerShell.

## Cloud deployment

The included `Dockerfile` and `vercel.json` deploy the frontend and backend together on Vercel. Production uses a Neon PostgreSQL database and the Spring `postgres` profile. Configure `SPRING_PROFILES_ACTIVE=postgres`, `JWT_SECRET`, and `GEMINI_API_KEY` as Vercel environment variables. Connect Neon to the Vercel project so it supplies `PGHOST_UNPOOLED`, `PGDATABASE`, `PGUSER`, and `PGPASSWORD`. The PostgreSQL migrations live in `backend/src/main/resources/db/postgresql/`. The database starts empty; local MySQL records are not transferred by deployment.

## Submit to the MRU CSE second-year repository

Target repository: https://github.com/cse-department-projects/MRU-CSE-2nd-Year-Projects

1. Extract this ZIP on your computer. GitHub does not unpack a ZIP that you upload as a file.
2. Open the target repository on GitHub and choose **Fork** while signed in to your GitHub account.
3. In your fork, create a folder such as `FitNova-AI/`. Upload the extracted project files into that folder, keeping `backend/`, `app/`, `public/`, `deploy/`, `package.json`, and this README together. GitHub's web upload may be slow for this project; Git on your computer is another option.
4. Check that `.env.local`, API keys, passwords, `node_modules/`, `backend/target/`, and `.vercel/` are absent.
5. Commit the files in your fork, then choose **Contribute → Open pull request** to propose the addition to the department repository's `main` branch. Add your team members and project summary to the pull request description.

The department repository currently lists no additional submission format in its README. Follow any instructions your instructor provides.

## Capability notes

- Gemini features require a valid server-side API key.
- Form Coach uses the camera, but automated pose scoring is not enabled without a validated model and exercise rules.
- Readiness and progress use saved records; new accounts begin with empty data.
