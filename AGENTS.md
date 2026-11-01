# AGENTS.md

## Project overview
This project is a REST API for managing classroom reservations (Gestión de reservas de aulas) in an educational center. 
- **Backend:** Java 21, Spring Boot 4.1.1, Spring Data JPA, MySQL, and Lombok.
- **Frontend:** Located entirely in the `/frontend` directory. It must be strictly built with HTML5, CSS3, and Vanilla JavaScript (No React, Angular, Vue, or any other framework).

## Build and test commands
- **Run local server:** `./mvnw spring-boot:run` (Starts the backend on `http://localhost:8080`).
- **Compile project:** `./mvnw clean compile` (Ensures Lombok annotations and JPA entities are properly processed).
- **Run all tests:** `./mvnw test`.
- **Run specific test:** `./mvnw test -Dtest="<TestClassName>"`.

## Code style guidelines
- **Java/Spring Boot:**
  - Heavily utilize Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@Builder`) to reduce boilerplate code.
  - Entities must map to a relational schema. The `Reserva` entity MUST use Hibernate's `@CreationTimestamp` on the `fechaCreacion` field so the database generates it automatically.
  - Controllers must return exact HTTP status codes using `ResponseEntity`: `200` (OK), `201` (Created), `204` (No Content), `400` (Bad Request), `404` (Not Found), and `409` (Conflict).
- **Frontend:**
  - Use native `fetch` API for all HTTP requests to the backend.
  - Separate concerns: keep logic in `app.js`, structure in `index.html`, and styling in `style.css`.

## Testing instructions
- Ensure the database configuration in `src/main/resources/application.properties` is set correctly for testing environments.
- **Critical business logic to test:** 
  1. Creating a reservation with a date in the past must fail (HTTP 400).
  2. Creating a reservation that overlaps with an existing one for the same classroom, date, and time slot must fail (HTTP 409).
- Fix any Java compilation, JPA query, or type errors until the whole suite is green (`BUILD SUCCESS`).
- Add or update tests for any new endpoints or queries introduced.

## Security considerations
- All database interactions must be done through Spring Data JPA repositories to prevent SQL Injection vulnerabilities.
- Do not expose raw database stack traces to the frontend on errors; always return a clean, structured JSON error message.
- Ensure CORS is properly configured in the Spring Boot backend if the vanilla frontend is served from a different port during local development.

## Extra instructions
- **PR Guidelines:** Title format must follow `[Reservas] <Title>`.
- Always run `./mvnw clean test` and verify frontend `fetch` error handling before committing.
- Do not introduce new third-party dependencies (like UI libraries or JS frameworks) without explicit permission.