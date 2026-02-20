# 티켓 분류 가이드

## 분류 원칙

### 1. 크기
- 1 티켓 = 1-3일 작업
- 너무 크면 분할
- 독립적으로 배포 가능

### 2. 의존성
- 순서: Infrastructure → Domain → API
- 병렬 가능: 독립 도메인

### 3. 우선순위
- P0: 없으면 진행 불가
- P1: 핵심 기능
- P2: 부가 기능
- P3: Nice-to-have

## 분류 예시

### 셀러 시스템
```
Infrastructure (P0)
├─ TICKET-1: 파일 업로드 (S3)
└─ TICKET-2: 암호화 유틸

Domain (P0)
├─ TICKET-3: Seller Aggregate
└─ TICKET-4: Document VO

Application (P1)
├─ TICKET-5: 셀러 신청 API
├─ TICKET-6: 관리자 승인 API
└─ TICKET-7: 계좌 실명 조회

Testing (P1)
├─ TICKET-8: Unit Tests
└─ TICKET-9: Integration Tests

Nice-to-have (P3)
└─ TICKET-10: 알림 발송
```

## 티켓 템플릿

```markdown
# TICKET-{번호}: {제목}

## 우선순위
P0 | P1 | P2 | P3

## 예상 소요
{일수}일 ({Story Points} SP)

## 의존성
- TICKET-X 완료 후 시작
- 또는 "없음"

## 구현 범위
- [ ] API 엔드포인트
- [ ] Service 로직
- [ ] Repository
- [ ] DTO
- [ ] Validation

## 기술 스택
- Spring Boot
- JPA
- Redis
- ...

## 구현 가이드

### Entity
```java
@Entity
class Seller {
    @Id @GeneratedValue
    private Long id;
    
    // .claude/rules/development.md 참조
}
```

### Service
```java
@Service
class SellerService {
    @Transactional
    public Seller create(SellerRequest request) {
        // .claude/rules/architecture.md 참조
    }
}
```

### Test
```java
@Test
void create_success() {
    // .claude/rules/testing.md 참조
}
```

## 검증 기준 (DoD)
- [ ] 코드 작성
- [ ] Unit Test 작성
- [ ] Integration Test 작성
- [ ] /review 통과
- [ ] 문서 업데이트

## 참고
- 설계 문서: .claude/docs/{프로젝트}/03-architecture.md
- API 명세: .claude/docs/{프로젝트}/04-api-design.md
```

## Story Point 기준

- 1 SP: 0.5일 (간단한 CRUD)
- 2 SP: 1일 (표준 기능)
- 3 SP: 1.5일 (복잡한 로직)
- 5 SP: 2-3일 (통합 필요)
- 8 SP: 분할 필요

## 예시: 티켓 생성

### TICKET-1: 파일 업로드
```
우선순위: P0
소요: 1일 (2 SP)
의존성: 없음

구현:
- S3 설정
- FileController
- 5MB 제한
- UUID 파일명

검증:
- 업로드 성공
- URL 반환
- 파일 크기 검증
```

### TICKET-5: 셀러 신청 API
```
우선순위: P1
소요: 3일 (6 SP)
의존성: TICKET-1, 2, 3, 4

구현:
- POST /api/v1/sellers/apply
- SellerService
- Validation (형식, 중복)
- 암호화 (사업자번호, 계좌)
- 계좌 실명 조회 (Mock)

검증:
- API 201 Created
- DB 암호화 확인
- Validation 동작
- Test 커버리지 80%
```