# Gym Membership Management

A small Spring Boot application to manage gym members, membership plans, payments, and member documents. It provides REST endpoints for CRUD operations and integrates with PostgreSQL for persistent storage and Cloudinary for document/media uploads.

This README provides a short product-style overview, quickstart instructions, architecture notes, environment configuration, and pointers for contributors.

## What it does

- Manages member records (personal details, status, gender, etc.).
- Supports membership creation and lifecycle (status, start/end dates, payments).
- Stores and serves member documents (uploads via Cloudinary integration).
- Exposes REST APIs and OpenAPI (Swagger) UI for exploration.

## Who it's for / Why it helps

This project is suitable for small gyms, fitness studios, or as a learning project for building CRUD-backed SaaS services. It centralizes member information and payments, simplifies document management (IDs, waivers), and provides a starting point to extend features like recurring billing, notifications, or a frontend app.

## Key features

- Member management (create/read/update/delete).
- Membership plans and membership records tied to members.
- Payment records and basic payment status tracking.
- Member document upload/management using Cloudinary.
- OpenAPI documentation (Swagger UI) for the REST API.

## Tech stack

- Java (Spring Boot 3.x)
- Spring Data JPA (PostgreSQL)
- Spring Web (REST controllers)
- Cloudinary Java SDK for uploads
- SpringDoc OpenAPI (Swagger UI)
- Maven (with included Maven wrapper)
- Docker + docker-compose for containerized runtime

## Quickstart (development)

Requirements:

- JDK matching the project's `java.version` (see `pom.xml`). The project currently sets <java.version>25</java.version> in `pom.xml`. Use a compatible JDK or update the property if necessary.
- Docker & Docker Compose (optional, for running with containers).

On Windows (PowerShell) you can build and run locally:

```powershell
# 1) Build the application (skip tests for a faster build):
.\mvnw.cmd clean package -DskipTests

# 2) Run the Spring Boot app from the generated jar:
java -jar target\gym-management-zdi.jar
```

Or use Docker Compose to build and run the app image together with required services:

```powershell
# Copy the environment key template and edit `.env` with your values:
copy .env.keys .env; notepad .env

# Build & start services (Compose will build the app image with Dockerfile):
docker-compose up --build
```

## Environment variables

The `docker-compose.yml` and the application use environment variables for sensitive configuration. The repository includes an `.env.keys` template listing the required keys (do not commit actual secrets).

Common variables you'll need to configure:

- POSTGRES_HOST, POSTGRES_PORT, POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
- CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET
- SPRING_PROFILES_ACTIVE (optional)

Configure them in `.env` or your shell/IDE run configuration.

## API and documentation

The project exposes REST controllers under `com.jatana.gymmembershipmanagemt.controller`.

- MemberController - endpoints for member CRUD.
- MembershipController - endpoints for memberships.
- PlanController - membership plans.
- MemberDocumentController - upload and manage documents.
- Membership and Payment endpoints for payment recording.

SpringDoc OpenAPI UI is included; when the application is running, visit:

- http://localhost:8080/swagger-ui.html or /swagger-ui/index.html (depending on SpringDoc version)

Use the Swagger UI to explore available endpoints and model schemas.

## Architecture notes

- Layered Spring Boot app: controllers -> services -> repositories (Spring Data JPA). Models live in `model` and DTOs in `model.dto`.
- Persistence: PostgreSQL (runtime scope). Configure via standard Spring properties (spring.datasource.\*).
- File uploads: the app uses Cloudinary (see `config/CloudinaryConfig.java`) to store and serve media.

## Build and CI notes

- The repository uses Maven and includes the Maven wrapper (`mvnw` / `mvnw.cmd`) for reproducible builds.
- Final artifact name is `gym-management-zdi.jar` (see `pom.xml` finalName).

Consider adding a GitHub Actions workflow to run `mvn -B -DskipTests clean package` on PRs to catch build regressions early.

## Contributing

If you'd like to contribute:

1. Fork the repository and create a feature branch.
2. Run and extend unit tests in `src/test` and keep build green.
3. Open pull requests against the `main` branch with a clear description of changes.

Development tips:

- Use IntelliJ IDEA or VS Code with the Java extension for a good developer experience.
- Load the `application.properties` under `src/main/resources` and set a local H2 or PostgreSQL for quick testing.

## Next steps / improvements

- Add example `.env` with non-secret demo values for new developers (keep secrets out).
- Add an integration test that boots the Spring context with an embedded PostgreSQL or Testcontainers.
- Add CI (GitHub Actions) to automate builds and tests.

## License & authors

See `pom.xml` for placeholder license and author entries. Add a license file (`LICENSE`) and update `pom.xml` developer and license information if this will be distributed.

---
