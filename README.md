Gym-Management-Software

## Docker / Environment variables

This repository's `docker-compose.yml` now uses environment variable substitution so you can provide configuration (ports, database credentials, API keys) at runtime instead of hardcoding them.

Quick steps:

1. Create `.env` from the template that only contains keys (use this to ensure required vars are present), edit it, then build the application and start the services with compose (Compose will build the image for you).

```powershell
# 1) Copy the keys-only template to .env
copy .env.keys .env

# 2) Edit `.env` and replace placeholder values with real ones (POSTGRES_PASSWORD, CLOUDINARY_API_SECRET, etc.)
notepad .env

# 3) Build the Java application using the included Maven wrapper (Windows):
.\mvnw.cmd clean package -DskipTests

# 4) Start the services with docker-compose (it will build the app image using the repo's Dockerfile):
docker-compose up --build
```

Notes:

- You can also export/override individual variables in your shell before running `docker-compose`.
- The `.env.keys` file contains placeholder values equal to the variable names so you can see which keys are required. Don't commit `.env` if it contains secrets.
- To test missing-variable behavior, remove or rename `.env` and run `docker-compose config` — Compose will error with a helpful message about which variable is missing.
