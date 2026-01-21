# Copilot / AI agent instructions for Shope

Purpose: give an AI coding agent the minimal, actionable context to be immediately productive in this Spring Boot web app.

- **Project type:** Spring Boot 3 application with Thymeleaf UI + small REST surface. Key packages: `com.srz.shope.config`, `controller`, `model`, `repository`, `service`, `web`.
- **Build & run:** use the Maven wrapper: `./mvnw spring-boot:run` for dev; `./mvnw package` then `java -jar target/srz-shope.jar` for production. Docker compose is available: run `docker-compose up --build` (see Dockerfile and `docker-compose.yml`). See [pom.xml](pom.xml) and [docker-compose.yml](docker-compose.yml).

- **Database & environment:** Postgres is the production DB. Connection is driven by environment variables that override `application.properties`. Common env vars:
  - `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/shope`)
  - `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`
  - `ADMIN_PASSWORD` (used by the in-memory `admin` user)
  See [src/main/resources/application.properties](src/main/resources/application.properties).

- **Data seeding:** `com.srz.shope.config.DataInitializer` seeds `Product` rows only when the repository is empty (it checks `repository.count()`). It will try to load image bytes from `src/main/resources/static/images/*` and populate `imageData` and `imageContentType`. Do NOT assume seeds run if products already exist. See [src/main/java/com/srz/shope/config/DataInitializer.java](src/main/java/com/srz/shope/config/DataInitializer.java).

- **Security model:** Spring Security is configured in `SecurityConfig`:
  - An in-memory `admin` user is created using `ADMIN_PASSWORD` (default `admin`).
  - `CompositeUserDetailsService` (provided elsewhere in `service`) is wired as the application user store; it is used for real user accounts.
  - Public endpoints include static assets, `/`, `/home`, `/login`, `/signup`, and `/api/products`.
  - `/admin/**` requires role `ADMIN`. Login page: `/login` and logout redirect `/home?logout`.
  See [src/main/java/com/srz/shope/config/SecurityConfig.java](src/main/java/com/srz/shope/config/SecurityConfig.java).

- **Common code patterns to follow (do not invent new conventions):**
  - Use Spring Data JPA repositories in `repository` (interfaces extend `JpaRepository`).
  - Domain models live in `model` and map to DB via JPA annotations.
  - Service layer lives in `service` (business logic) and is used by controllers in `controller` (MVC) and `web` (REST/DTO endpoints).
  - Server-rendered views use Thymeleaf templates in `src/main/resources/templates` and static assets in `src/main/resources/static` (css/js/images).

- **Where to make changes for typical tasks:**
  - Add REST endpoints or return binary images from `controller`/`web` packages.
  - Add DB mappings in `model` and repository queries in `repository`.
  - Add cross-cutting configuration in `config` (security, data seeding, initializer beans).

- **Run & debug tips:**
  - Startup logs include a DB connection check in `ShopeApplication` that logs the JDBC URL; useful to verify DB connectivity. See [src/main/java/com/srz/shope/ShopeApplication.java](src/main/java/com/srz/shope/ShopeApplication.java).
  - To reproduce CI/test locally: `./mvnw test`. Test reports are under `target/surefire-reports`.

- **Files to inspect first for feature work or bug fixes:**
  - [src/main/java/com/srz/shope/ShopeApplication.java](src/main/java/com/srz/shope/ShopeApplication.java)
  - [src/main/resources/application.properties](src/main/resources/application.properties)
  - [src/main/java/com/srz/shope/config/SecurityConfig.java](src/main/java/com/srz/shope/config/SecurityConfig.java)
  - [src/main/java/com/srz/shope/config/DataInitializer.java](src/main/java/com/srz/shope/config/DataInitializer.java)
  - [pom.xml](pom.xml)

- **What an AI agent should not assume:**
  - There is no embedded H2 auto-configured for dev—Postgres is expected by default; always check `application.properties` and use env vars if running locally without Postgres.
  - Do not modify seed data to overwrite existing rows (initializer explicitly avoids overwriting).

If anything in this guidance looks incomplete or you want added examples (e.g., a minimal contributor flow for adding a Product REST endpoint), tell me which area to expand.
