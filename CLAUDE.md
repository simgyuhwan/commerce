# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

All commands run from the `backend/` directory.

```bash
# Build
./gradlew build

# Run application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.flowcommerce.core.domain.product.ProductServiceTest"

# Run a single test method
./gradlew test --tests "com.flowcommerce.core.domain.product.ProductServiceTest.주문 생성 성공"

# Compile only (fast check)
./gradlew compileKotlin
```

## Architecture

**Stack**: Kotlin + Spring Boot 3.5.9 + Spring Data JPA + MariaDB (H2 for tests)

The project uses a **4-layer architecture** under `src/main/kotlin/com/flowcommerce/core/`:

| Layer | Package | Responsibility |
|-------|---------|---------------|
| Presentation | `api/` | Controllers, request/response DTOs |
| Application | `domain/*/service/` | Use cases, orchestration |
| Domain | `domain/` | Entities, Value Objects, business logic, validators |
| Infrastructure | `storage/` | JPA entities, Spring Data repositories |

Cross-cutting concerns live in `support/`: error handling (`CoreException`, `ErrorType`), auth, `ApiResponse`.

### Bounded Contexts

- **member** – registration, authentication, commission, document verification
- **product** – product catalog, stock management, stock history
- **seller** – seller application, document upload, approval workflow

### Key Patterns

**Domain vs Storage separation**: Domain entities (`domain/*/Entity.kt`) hold business logic. JPA entities (`storage/*/Entity.kt`) handle persistence. They are separate classes — domain entities are plain Kotlin classes, not `@Entity`.

**BaseEntity** (`storage/BaseEntity.kt`): All JPA entities extend this. Provides `id`, `status` (ACTIVE/DELETED), `createdAt`, `updatedAt`. Soft delete via `delete()` method.

**Error handling**: Throw `CoreException(ErrorType.XXX)` everywhere. `ApiControllerAdvice` handles all exceptions and returns `ApiResponse.error(...)`. Never catch and swallow exceptions.

**API responses**: Always return `ApiResponse.success(data)` or `ApiResponse.success()`. The wrapper contains `resultType`, `data`, and `errorMessage`.

**Commands**: Write operations use explicit command objects (e.g., `ProductCreate`, `StockAdjust`) rather than passing raw request objects into the domain.

### Test Infrastructure

- `IntegrationTestSupport` – base class for `@SpringBootTest` + `@Transactional` + `@ActiveProfiles("test")` tests. Extend this instead of re-declaring these annotations.
- `RestDocsTestSupport` – base class for REST Docs API documentation tests.
- `TestFixtureConfig` – configures Fixture Monkey for test data generation.
- Test profile uses H2 in MariaDB compatibility mode (`MODE=MariaDB`), so SQL syntax matches production.

## Documentation

Architecture design docs live in `backend/docs/{domain}/`. Each domain has:
- `architecture/` – 4 markdown files (domain analysis, bounded contexts, architecture, API design)
- `diagrams/` – Mermaid diagrams
- `tickets/` – development tickets
- `requirements-v*.md` – versioned requirements

Snapshot reports are saved to `backend/reports/`. Temporary requirement inputs go in `backend/inputs/`.
