# Claude Code - DDD Backend Development Suite

백엔드 개발 전체 프로세스 자동화: 요구사항 검증 → 설계 → 계획 → 리뷰 → 커밋

## 빠른 시작

```bash
# 1. 요구사항 검증 & 개선
/req inputs/new-feature.md

# 2. 아키텍처 설계
/arch {도메인} docs/{도메인}/spec.md

# 3. 개발 티켓 분류
/plan {도메인}

# 4. 개발 (각 티켓별)
# ... 개발 진행 ...

# 5. 코드 리뷰
/review src/main/kotlin/{도메인}

# 6. 커밋
/commit
```

## 디렉토리 구조

```
{프로젝트 루트}/
│
├── inputs/                    # 신규 요구사항 임시 보관 (처리 후 삭제)
│   └── new-feature.md
│
├── docs/                      # 살아있는 설계 문서 (도메인별)
│   ├── {도메인}/
│   │   ├── README.md          # 도메인 진입점 & 핵심 요약
│   │   ├── spec.md            # 확정 스펙 (통합본)
│   │   ├── requirements-v1.md # 원본 요구사항
│   │   ├── requirements-v2.md # 검증·보완된 요구사항
│   │   ├── architecture/      # /arch 출력
│   │   │   ├── 01-domain-analysis.md
│   │   │   ├── 02-bounded-contexts.md
│   │   │   ├── 03-architecture.md
│   │   │   └── 04-api-design.md
│   │   ├── diagrams/          # Mermaid 다이어그램 4종
│   │   ├── tickets/           # /plan 출력
│   │   │   └── TICKET-{N}.md
│   │   └── decisions/         # ADR (Architecture Decision Records)
│   └── _shared/               # 크로스 도메인 공통 설계
│
├── reports/                   # 감사 추적용 스냅샷
│   └── {YYYY-MM-DD}-{도메인}-{type}.md
│
└── .claude/                   # AI 도구 설정 (문서 아님)
    ├── commands/              # 5개 슬래시 커맨드
    ├── skills/                # 5개 전문 스킬
    └── rules/                 # 공통 코딩 규칙
```

## Commands

### /req - 요구사항 검증 & 개선

```bash
/req inputs/new-feature.md
```

- 7가지 관점 검증 (비즈니스, 데이터, API, 보안, 성능, 확장, 운영)
- Critical 이슈 자동 수정
- 역질문 생성 (20개)

**출력**: `reports/{YYYY-MM-DD}-{도메인}-validation.md`

---

### /arch - 아키텍처 설계

```bash
/arch seller docs/seller/spec.md
```

- Event Storming → Bounded Context → Aggregate → API 설계
- Mermaid 다이어그램 4종 자동 생성

**출력**: `docs/{도메인}/architecture/` + `diagrams/`

---

### /plan - 개발 티켓 분류

```bash
/plan seller
```

- Feature/Domain별 분류, 의존성 분석, 우선순위 (P0-P3), Story Points

**출력**: `docs/{도메인}/tickets/TICKET-{N}.md`

---

### /review - 코드 리뷰

```bash
/review                             # 전체
/review src/main/kotlin/seller     # 특정 경로
```

병렬 분석 4가지: Performance / Security / Testing / Requirements

**출력**: `reports/{YYYY-MM-DD}-{도메인}-review.md`

---

### /commit - Git 커밋

```bash
/commit
```

Semantic Commit 자동 생성 (한글 메시지)

---

## 워크플로우

### 신규 기능 개발

```bash
# 1. 요구사항 작성
vim inputs/order-system.md

# 2. 검증 & 보완
/req inputs/order-system.md
# → reports/2026-02-18-order-validation.md 생성

# 3. 설계 (spec.md 확정 후)
/arch order docs/order/spec.md
# → docs/order/architecture/ + diagrams/ 생성

# 4. 티켓 분류
/plan order
# → docs/order/tickets/TICKET-*.md 생성

# 5. 티켓별 개발 + 리뷰 + 커밋
/review src/main/kotlin/order
/commit
```

### 기존 코드 리뷰

```bash
/review
# → reports/2026-02-18-all-review.md 생성
```

## Rules 커스텀

`.claude/rules/` 파일 수정 → 모든 커맨드/스킬에 즉시 반영

```
.claude/rules/
├── development.md   # 코딩 스타일, 트랜잭션, 예외 처리
├── architecture.md  # DDD 원칙, 레이어 구조
├── security.md      # 인증, 암호화, API 보안
├── performance.md   # N+1, 캐싱, 페이징
└── testing.md       # 테스트 전략, 커버리지
```

## 업데이트

### v3.0.0 (2026-02-18)
- 구조 개편: `.claude/docs/` + `.claude/reports/` → 프로젝트 루트 `docs/` + `reports/`
- 도메인 중심 문서 구조 적용 (B+C 혼합)
- 커맨드 출력 경로 업데이트
- inputs/ 역할 명확화 (임시 staging area)

### v2.0.0 (2026-02-15)
- 전체 구조 개편 (토큰 최적화)
- Rules 시스템 추가
- 5개 Commands + 5개 Skills 체계

### v1.0.0 (2026-02-14)
- 초기 버전
