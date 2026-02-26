# Repository Guidelines

## Project Structure & Module Organization
This repository is a multi-module Gradle backend (`settings.gradle`) for `com.livecommerce`.

- `app/`: Spring Boot entry module (`LiveCommerceApplication`) that wires all feature modules.
- `common/`: shared security, error handling, and response utilities.
- `member/`, `commerce/`, `broadcast/`, `order/`, `payment/`, `settlement/`: domain modules consumed by `app`.
- `app/src/main/resources/`: runtime config and DB migrations (for example `application.yml`, `db/migration/V1__init_schema.sql`).

Keep business/domain logic inside its module and place cross-cutting code in `common`.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root.

- `./gradlew clean build`: compile all modules, run tests, and produce artifacts.
- `./gradlew test`: run all JUnit 5 tests across modules.
- `./gradlew :app:bootRun`: start the application locally on port `8080`.
- `./gradlew :app:dependencies`: inspect dependency graph when troubleshooting.

On Windows, use `.\gradlew.bat` equivalents.

## Coding Style & Naming Conventions
- Languages: Java + Kotlin (JDK 21 toolchain, Kotlin 2.1, Spring Boot 3.4).
- Indentation: 4 spaces; no tabs.
- Packages: lowercase, rooted at `com.livecommerce.<module>`.
- Classes/objects: `PascalCase`; methods/variables: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Prefer constructor injection and small focused classes.
- Keep module boundaries explicit (depend on `common` or declared project modules only).

No formatter/linter plugin is currently enforced in Gradle; keep style consistent with existing sources.

## Testing Guidelines
- Framework: `spring-boot-starter-test` with JUnit Platform (`useJUnitPlatform()` configured globally).
- Test location: `<module>/src/test/java` or `<module>/src/test/kotlin`.
- Naming: unit tests `*Test`, integration tests `*IntegrationTest`.
- Run module-specific tests with `./gradlew :<module>:test` (example: `./gradlew :common:test`).

There is no explicit coverage gate yet; add meaningful tests for new business logic and security-critical paths.

## Commit & Pull Request Guidelines
Recent history uses short, prefixed messages such as `chore: ...`. Follow a Conventional Commit style where possible:

- `feat: add order cancellation endpoint`
- `fix: handle expired JWT parsing`
- `chore: update gradle dependency versions`

PRs should include:
- clear summary and changed modules,
- linked issue/ticket,
- test evidence (command + result),
- config/migration notes when `application.yml` or `db/migration` changes.
