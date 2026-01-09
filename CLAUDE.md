# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Flow Core is a Live Commerce Platform with Real-time Streaming built using Spring Boot 3.5.9 and Java 17. It handles member management, seller onboarding, and product management with MariaDB for persistence and Redis for caching.

## Common Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Clean build
./gradlew clean build
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.flowcommerce.core.domain.member.service.MemberServiceTest"

# Run specific test method
./gradlew test --tests "com.flowcommerce.core.domain.member.service.MemberServiceTest.회원가입이_가능하다"

# Run tests with specific profile
./gradlew test -Dspring.profiles.active=test
```

### Code Quality
```bash
# Check dependencies
./gradlew dependencies

# View dependency tree
./gradlew dependencies --configuration runtimeClasspath
```

## Architecture

### Layered Architecture

The codebase follows a clean layered architecture with clear separation of concerns:

```
api/          - REST controllers and API configuration
  ├─ config/  - Web, Security, Swagger configuration
  └─ v1/      - Versioned API endpoints (member, product)
    ├─ request/   - Request DTOs
    └─ response/  - Response DTOs

domain/       - Core business logic (framework-agnostic)
  ├─ member/  - Member and Seller domain models, services, validators
  └─ product/ - Product domain models and logic

storage/      - JPA entities and repositories (infrastructure)
  ├─ member/  - MemberEntity, SellerEntity, repositories
  └─ product/ - Product persistence layer

support/      - Cross-cutting concerns
  ├─ auth/    - Authentication and password encoding
  ├─ error/   - Exception handling (CoreException, ErrorType)
  ├─ response/- Standard API response wrappers
  └─ utils/   - Utility classes
```

### Key Architectural Patterns

**Domain-Driven Design:**
- **Domain models** in `domain/` are pure Java objects without JPA annotations
- **Storage entities** in `storage/` handle persistence with JPA
- Domain models use static factory methods (e.g., `Member.create()`)
- Entities use static factory methods (e.g., `MemberEntity.forLocalSignUp()`)

**Separation of Domain and Persistence:**
- Domain layer (`domain/`) contains business logic and is framework-agnostic
- Storage layer (`storage/`) handles database interactions with JPA
- Services in `domain/*/service/` orchestrate business operations
- Validators in domain layer enforce business rules (e.g., `MemberPolicyValidator`, `SellerApplyValidator`)

**Soft Delete Pattern:**
- All entities extend `BaseEntity` which includes `EntityStatus` (ACTIVE/DELETED)
- Use `entity.delete()` instead of repository deletion
- Check `entity.isActive()` and `entity.isDeleted()` for status

**Exception Handling:**
- Custom `CoreException` with `ErrorType` enum for structured error handling
- Global exception handling in `ApiControllerAdvice`
- Standard `ApiResponse<T>` wrapper for all API responses

**Testing Strategy:**
- Integration tests extend `IntegrationTestSupport` (@SpringBootTest with @Transactional)
- Use H2 in-memory database for tests (configured in application-test.yml)
- Fixture classes for test data (e.g., `MemberFixture`)
- Test method names in Korean describing the behavior being tested

### Configuration

**Environment-specific config:**
- `application.yml` - Main configuration (MariaDB, Redis, file upload)
- `application-test.yml` - Test configuration (H2 database)
- Environment variables: `DB_PASSWORD`, `UPLOAD_DIR`

**Key dependencies:**
- Spring Boot 3.5.9 (Web, Data JPA, Actuator, Data Redis)
- MariaDB driver (runtime: org.mariadb.jdbc.mariadb-java-client)
- H2 database (test only)
- Lombok for boilerplate reduction
- Jasypt for encryption
- SpringDoc OpenAPI for API documentation
- Micrometer Prometheus for metrics
- Fixture Monkey for test data generation

**Ports and endpoints:**
- Application: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API docs: `http://localhost:8080/v3/api-docs`
- Actuator endpoints: `/actuator/health`, `/actuator/info`, `/actuator/prometheus`

## Development Workflow

### Adding New Features

1. **Domain Model**: Create domain objects in `domain/` without JPA annotations
2. **Storage Layer**: Create corresponding JPA entities in `storage/` extending `BaseEntity`
3. **Repository**: Create Spring Data JPA repository interface in `storage/`
4. **Service**: Implement business logic in `domain/*/service/`
5. **Validator**: Add validation logic in dedicated validator classes
6. **Controller**: Create REST endpoint in `api/v1/`
7. **Tests**: Write integration tests extending `IntegrationTestSupport`

### Testing Guidelines

- Integration tests use real Spring context with H2 database
- Tests are transactional and roll back automatically
- Use descriptive Korean method names for test scenarios
- Leverage Fixture classes for consistent test data
- AssertJ is used for assertions

### Database Migration

- JPA DDL mode is `update` in development (application.yml:19)
- Test database uses `create-drop` mode (application-test.yml:11)
- MariaDB is the production database, H2 for testing with MariaDB compatibility mode
