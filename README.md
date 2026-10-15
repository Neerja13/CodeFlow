# CodeFlow

GitHub Issue Management and Development Workflow — a Java 21 / Spring Boot REST application that stores issues in MySQL and will later integrate with the GitHub REST API.

**Milestone 1** is the backend foundation only: layered REST API, Issue CRUD, validation, tests, and configuration via environment variables. There is no dashboard and no GitHub client yet.

## Architecture

Requests travel in one direction:

`HTTP request → Controller → Service → Repository (JPA) → MySQL`

| Class / package | Purpose |
| --- | --- |
| `CodeFlowApplication` | Spring Boot entry point |
| `controller` | HTTP mapping, JSON, status codes — no SQL |
| `service.IssueService` | Create/update rules (trim title, default `OPEN`) |
| `repository.IssueRepository` | Spring Data JPA access to table `issues` |
| `entity.Issue` | JPA mapping for one stored issue |
| `entity.IssueStatus` | `OPEN`, `IN_PROGRESS`, `COMPLETED` |
| `dto.IssueRequest` / `IssueResponse` | API JSON so the database schema is not exposed |
| `exception.GlobalExceptionHandler` | `400` validation errors and `404` missing issues |
| `config.WebConfig` | Minimal CORS for a later dashboard |

## Dependencies

- **spring-boot-starter-web** — REST controllers and embedded Tomcat
- **spring-boot-starter-data-jpa** — Hibernate and Spring Data repositories
- **spring-boot-starter-validation** — `@Valid`, `@NotBlank`, `@Size`
- **spring-boot-starter-actuator** — `/actuator/health`
- **mysql-connector-j** — JDBC driver used when you **run** the app
- **spring-boot-starter-test** — JUnit 5, MockMvc, AssertJ
- **h2** (test scope) — in-memory database so tests never use your MySQL instance

## Prerequisites

- JDK 21 (this project will not compile on JDK 17)
- MySQL 8+ to **run** the application (not required for `mvnw test`)
- Maven Wrapper is included (`mvnw.cmd` on Windows). A separate Maven install is optional.

If `java -version` still shows 17, point the current PowerShell session at JDK 21:

```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
java -version
```

## Database setup (Windows)

1. Start MySQL (Services app, or MySQL Installer).
2. Open MySQL Shell, MySQL Workbench, or `mysql` in PowerShell:

```sql
CREATE DATABASE codeflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Hibernate will create/update tables on startup (`spring.jpa.hibernate.ddl-auto=update`). You do not need to write the `issues` table by hand for Milestone 1.

## Environment variables

Credentials are **not** stored in source files. `application.properties` reads:

| Variable | Purpose | Example (not real secrets) |
| --- | --- | --- |
| `DB_URL` | JDBC URL | `jdbc:mysql://localhost:3306/codeflow` |
| `DB_USERNAME` | MySQL user | `root` |
| `DB_PASSWORD` | MySQL password | set to **your** password; leave unset only if the user has no password |

**Current PowerShell session** (values disappear when you close the window):

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/codeflow"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
```

**User-level variables** (persist for new terminals; still not committed to Git):

```powershell
[System.Environment]::SetEnvironmentVariable("DB_URL", "jdbc:mysql://localhost:3306/codeflow", "User")
[System.Environment]::SetEnvironmentVariable("DB_USERNAME", "root", "User")
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD", "your-password", "User")
```

Do not put real passwords in `application.properties`, `.env` files that you commit, or the README. `.gitignore` already excludes `.env` and `application-local.properties`.

## Build and test

From the project root, with JDK 21 on `PATH`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd -q -DskipTests package
```

Tests use the `test` Spring profile and H2. They do **not** read or write your `codeflow` MySQL database.

## Run the application

```powershell
.\mvnw.cmd spring-boot:run
```

The API listens on `http://localhost:8080`. Startup will fail if MySQL is down or `DB_PASSWORD` does not match your local user.

## Verify endpoints

Health:

```powershell
curl http://localhost:8080/api/health
```

Create:

```powershell
curl -X POST http://localhost:8080/api/issues `
  -H "Content-Type: application/json" `
  -d "{\"title\":\"First issue\",\"description\":\"Stored in MySQL\",\"repositoryName\":\"owner/codeflow\"}"
```

List / get / update / delete (replace `1` with the returned id):

```powershell
curl http://localhost:8080/api/issues
curl http://localhost:8080/api/issues/1
curl -X PUT http://localhost:8080/api/issues/1 -H "Content-Type: application/json" -d "{\"title\":\"Updated title\",\"status\":\"IN_PROGRESS\"}"
curl -X DELETE http://localhost:8080/api/issues/1
```

Expected: blank title → `400`; unknown id → `404` with JSON `{ "status": 404, "message": "Issue not found: ..." }`; delete → `204`.

## REST API (Milestone 1)

| Method | Path | Result |
| --- | --- | --- |
| GET | `/api/health` | `{ "status": "UP" }` |
| POST | `/api/issues` | `201 Created` |
| GET | `/api/issues` | `200` list |
| GET | `/api/issues/{id}` | `200` or `404` |
| PUT | `/api/issues/{id}` | `200` or `404` |
| DELETE | `/api/issues/{id}` | `204` or `404` |

`POST` / `PUT` require a non-blank `title` (max 255). Optional fields: `description`, `repositoryName`, `githubIssueNumber`, `status`. `githubIssueNumber` stays null until a later real GitHub import.

## What is not in Milestone 1

GitHub REST API, implementation plans, tasks, Git, pull requests, and the HTML dashboard.
