# 아키텍처 규칙 (Architecture Rules)

## DDD 원칙

### 1. Bounded Context
- 명확한 경계 설정
- 컨텍스트 간 독립성
- Ubiquitous Language 유지

### 2. Aggregate
- Root를 통한 접근만
- 트랜잭션 경계 = Aggregate 경계
- 다른 Aggregate는 ID로 참조

### 3. Entity vs Value Object
- Entity: 식별자 + 변경 가능
- Value Object: 불변 + 동등성

### 4. Repository
- Aggregate Root만 Repository
- 인터페이스는 Domain
- 구현은 Infrastructure

### 5. Domain Event
- 과거형 네이밍 (OrderCreated)
- 불변 객체
- 비동기 처리

## 레이어 구조

```
Presentation (Controller)
    ↓
Application (UseCase, Command/Query Handler)
    ↓
Domain (Entity, VO, Repository Interface)
    ↓
Infrastructure (Repository Impl, External API)
```

### 의존성 방향
- 항상 안쪽으로 (Domain ← Application ← Presentation)
- Domain은 독립적

## 패턴

### CQRS (선택)
- Command: 쓰기 (트랜잭션)
- Query: 읽기 (최적화)

### Event Sourcing (선택)
- 이벤트 저장
- 상태 재구성

## 금지 사항

❌ Domain이 Infrastructure 의존
❌ Aggregate 간 직접 참조
❌ Anemic Domain Model
❌ Transaction Script