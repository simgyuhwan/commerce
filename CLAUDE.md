# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소의 코드를 작업할 때 가이드를 제공합니다.

## 프로젝트 개요

Flow Core는 Spring Boot 3.5.9와 Java 17로 구축된 실시간 스트리밍 라이브 커머스 플랫폼입니다. 회원 관리, 판매자 온보딩, 상품 관리를 담당하며 MariaDB를 영속성 계층으로, Redis를 캐싱으로 사용합니다.

## 자주 사용하는 명령어

### 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 클린 빌드
./gradlew clean build
```

### 테스트
```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "com.flowcommerce.core.domain.member.service.MemberServiceTest"

# 특정 테스트 메서드 실행
./gradlew test --tests "com.flowcommerce.core.domain.member.service.MemberServiceTest.회원가입이_가능하다"

# 특정 프로파일로 테스트 실행
./gradlew test -Dspring.profiles.active=test
```

### 코드 품질
```bash
# 의존성 확인
./gradlew dependencies

# 의존성 트리 보기
./gradlew dependencies --configuration runtimeClasspath
```

## 아키텍처

### 계층형 아키텍처

코드베이스는 명확한 관심사 분리를 가진 클린 계층형 아키텍처를 따릅니다:

```
api/          - REST 컨트롤러 및 API 구성
  ├─ config/  - Web, Security, Swagger 설정
  └─ v1/      - 버전별 API 엔드포인트 (member, product)
    ├─ request/   - 요청 DTO
    └─ response/  - 응답 DTO

domain/       - 핵심 비즈니스 로직 (프레임워크 독립적)
  ├─ member/  - 회원 및 판매자 도메인 모델, 서비스, 검증기
  └─ product/ - 상품 도메인 모델 및 로직

storage/      - JPA 엔티티 및 리포지토리 (인프라스트럭처)
  ├─ member/  - MemberEntity, SellerEntity, 리포지토리
  └─ product/ - 상품 영속성 계층

support/      - 공통 관심사
  ├─ auth/    - 인증 및 패스워드 암호화
  ├─ error/   - 예외 처리 (CoreException, ErrorType)
  ├─ response/- 표준 API 응답 래퍼
  └─ utils/   - 유틸리티 클래스
```

### 주요 아키텍처 패턴

**도메인 주도 설계(DDD):**
- `domain/`의 **도메인 모델**은 JPA 어노테이션이 없는 순수 Java 객체입니다
- `storage/`의 **스토리지 엔티티**는 JPA로 영속성을 처리합니다
- 도메인 모델은 정적 팩토리 메서드를 사용합니다 (예: `Member.create()`)
- 엔티티는 정적 팩토리 메서드를 사용합니다 (예: `MemberEntity.forLocalSignUp()`)

**도메인과 영속성의 분리:**
- 도메인 계층(`domain/`)은 비즈니스 로직을 포함하며 프레임워크에 독립적입니다
- 스토리지 계층(`storage/`)은 JPA로 데이터베이스 상호작용을 처리합니다
- `domain/*/service/`의 서비스는 비즈니스 작업을 조율합니다
- 도메인 계층의 검증기는 비즈니스 규칙을 강제합니다 (예: `MemberPolicyValidator`, `SellerApplyValidator`)

**소프트 삭제 패턴:**
- 모든 엔티티는 `EntityStatus`(ACTIVE/DELETED)를 포함하는 `BaseEntity`를 상속합니다
- 리포지토리 삭제 대신 `entity.delete()`를 사용합니다
- 상태 확인을 위해 `entity.isActive()`와 `entity.isDeleted()`를 사용합니다

**예외 처리:**
- 구조화된 에러 처리를 위한 `ErrorType` enum을 가진 커스텀 `CoreException`
- `ApiControllerAdvice`에서 글로벌 예외 처리
- 모든 API 응답에 표준 `ApiResponse<T>` 래퍼 사용

**테스트 전략:**
- 통합 테스트는 `IntegrationTestSupport`를 상속합니다 (@SpringBootTest with @Transactional)
- 테스트에는 H2 인메모리 데이터베이스를 사용합니다 (application-test.yml에 설정)
- 테스트 데이터를 위한 Fixture 클래스 사용 (예: `MemberFixture`)
- 테스트 메서드명은 테스트 동작을 설명하는 한글로 작성합니다

### 설정

**환경별 설정:**
- `application.yml` - 메인 설정 (MariaDB, Redis, 파일 업로드)
- `application-test.yml` - 테스트 설정 (H2 데이터베이스)
- 환경 변수: `DB_PASSWORD`, `UPLOAD_DIR`

**주요 의존성:**
- Spring Boot 3.5.9 (Web, Data JPA, Actuator, Data Redis)
- MariaDB 드라이버 (runtime: org.mariadb.jdbc.mariadb-java-client)
- H2 데이터베이스 (테스트 전용)
- Lombok (보일러플레이트 감소)
- Jasypt (암호화)
- SpringDoc OpenAPI (API 문서화)
- Micrometer Prometheus (메트릭)
- Fixture Monkey (테스트 데이터 생성)

**포트 및 엔드포인트:**
- 애플리케이션: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API 문서: `http://localhost:8080/v3/api-docs`
- Actuator 엔드포인트: `/actuator/health`, `/actuator/info`, `/actuator/prometheus`

## 개발 워크플로우

### 새로운 기능 추가

1. **도메인 모델**: `domain/`에 JPA 어노테이션 없이 도메인 객체 생성
2. **스토리지 계층**: `storage/`에 `BaseEntity`를 상속하는 JPA 엔티티 생성
3. **리포지토리**: `storage/`에 Spring Data JPA 리포지토리 인터페이스 생성
4. **서비스**: `domain/*/service/`에 비즈니스 로직 구현
5. **검증기**: 전용 검증기 클래스에 검증 로직 추가
6. **컨트롤러**: `api/v1/`에 REST 엔드포인트 생성
7. **테스트**: `IntegrationTestSupport`를 상속하는 통합 테스트 작성

### 테스트 가이드라인

- 통합 테스트는 H2 데이터베이스와 함께 실제 Spring 컨텍스트를 사용합니다
- 테스트는 트랜잭션으로 실행되며 자동으로 롤백됩니다
- 테스트 시나리오는 설명적인 한글 메서드명을 사용합니다
- 일관된 테스트 데이터를 위해 Fixture 클래스를 활용합니다
- 단언문(assertion)에는 AssertJ를 사용합니다

### 데이터베이스 마이그레이션

- 개발 환경의 JPA DDL 모드는 `update`입니다 (application.yml:19)
- 테스트 데이터베이스는 `create-drop` 모드를 사용합니다 (application-test.yml:11)
- 프로덕션 데이터베이스는 MariaDB이며, 테스트에는 MariaDB 호환 모드의 H2를 사용합니다
