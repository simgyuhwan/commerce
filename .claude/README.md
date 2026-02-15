# Claude Code - DDD Backend Development Suite

백엔드 개발 전체 프로세스 자동화: 요구사항 검증 → 설계 → 계획 → 리뷰 → 커밋

## 🚀 빠른 시작

```bash
# 1. 요구사항 검증 & 개선
/req inputs/seller-system.md

# 2. 아키텍처 설계
/arch seller-system inputs/seller-system.md

# 3. 개발 티켓 분류
/plan seller-system

# 4. 개발 (각 티켓별)
# ... 개발 진행 ...

# 5. 코드 리뷰
/review src/main/java/seller

# 6. 커밋
/commit
```

## 📂 구조

```
.claude/
├── commands/           # 5개 진입점 (심플)
│   ├── req.md         # 요구사항
│   ├── arch.md        # 설계
│   ├── plan.md        # 계획
│   ├── review.md      # 리뷰
│   └── commit.md      # 커밋
│
├── skills/            # 5개 전문 지식 (토큰 최적화)
│   ├── requirements/  # 요구사항 검증
│   ├── architecture/  # DDD 설계
│   ├── planning/      # 티켓 분류
│   ├── code-review/   # 4가지 병렬 리뷰
│   └── git/          # 자동 커밋
│
├── rules/             # 공통 규칙 (커스텀 가능)
│   ├── development.md
│   ├── architecture.md
│   ├── security.md
│   ├── performance.md
│   └── testing.md
│
├── docs/              # 자동 생성 설계 문서
│   └── {프로젝트}/
│       ├── README.md
│       ├── 01-domain-analysis.md
│       ├── 02-bounded-contexts.md
│       ├── 03-architecture.md
│       ├── 04-api-design.md
│       ├── diagrams/
│       └── tickets/
│
└── reports/           # 자동 생성 리포트
    ├── validations/
    └── reviews/
```

## 🎯 Commands

### /req - 요구사항 검증 & 개선

```bash
/req inputs/seller-system.md
```

**실행**:
- 7가지 관점 검증 (비즈니스, 데이터, API, 보안, 성능, 확장, 운영)
- Critical 이슈 자동 수정
- 역질문 생성 (20개)
- 실행 계획 작성
- 예상 반론 대응

**출력**: `.claude/reports/validations/{프로젝트}-validation.md`

### /arch - 아키텍처 설계

```bash
/arch seller-system inputs/seller-system.md
```

**실행**:
- Event Storming
- Bounded Context 정의
- Aggregate 설계
- API 설계
- Mermaid 다이어그램

**출력**: `.claude/docs/{프로젝트}/` (5개 문서 + 4개 다이어그램)

### /plan - 개발 티켓 분류

```bash
/plan seller-system
```

**실행**:
- Feature/Domain별 분류
- 의존성 분석
- 우선순위 (P0-P3)
- Story Points
- 구현 가이드

**출력**: `.claude/docs/{프로젝트}/tickets/TICKET-{N}.md`

### /review - 코드 리뷰

```bash
/review                        # 전체
/review src/main/java/seller  # 특정 경로
```

**병렬 분석 (4가지)**:
- 🚀 Performance (N+1, 캐싱, 인덱스)
- 🔒 Security (SQL Injection, 암호화)
- ✅ Testing (커버리지, 엣지 케이스)
- 📋 Requirements (명세 준수)

**출력**: `.claude/reports/reviews/review-{date}-{target}.md`

### /commit - Git 커밋

```bash
/commit
```

**실행**:
- Semantic Commit 자동 생성
- Type/Scope 추출
- 한글 메시지

**예시**:
```
feat(seller): 셀러 신청 기능 추가

- SellerService 생성
- 암호화 적용

Resolves: #123
```

## ⚙️ Rules (커스텀 가능)

`.claude/rules/` 파일 수정 → 즉시 반영

```markdown
# .claude/rules/development.md

## 코드 스타일
- 메서드 최대 20줄
- 주석 최소화
  ...
```

모든 커맨드/스킬이 자동으로 참조

## 📊 워크플로우

### 시나리오 1: 새 기능 개발

```bash
# Step 1: 요구사항 작성
vim inputs/seller-system.md

# Step 2: 검증 & 개선
/req inputs/seller-system.md
# → Critical 자동 수정
# → 질문 20개 확인
# → 완성도 70% 이상 확인

# Step 3: 설계
/arch seller-system inputs/seller-system.md
# → .claude/docs/seller-system/ 생성

# Step 4: 티켓 분류
/plan seller-system
# → TICKET-1, 2, 3... 생성

# Step 5: 개발 (티켓별)
# TICKET-1 개발...

# Step 6: 리뷰
/review src/main/java/seller
# → Critical 수정

# Step 7: 커밋
/commit
# → feat(seller): ...

# Step 8: 반복 (TICKET-2, 3...)
```

### 시나리오 2: 기존 코드 리뷰

```bash
/review

# 리포트 확인
cat .claude/reports/reviews/review-2025-02-14-all.md

# Critical 수정
# ...

# 재검증
/review
```

## 🎨 Skills

### Progressive Disclosure

```
SKILL.md (500 tokens)
  ↓ 필요시
reference/ (상세 가이드)
```

토큰 절약 + 빠른 실행

### 구조

```
skills/requirements/
├── SKILL.md           # 핵심 워크플로우 (500 tokens)
└── reference/
    ├── checklist.md   # 상세 체크리스트
    └── examples.md    # 실제 예시
```

## 💡 베스트 프랙티스

### 1. Rules 커스텀

```bash
# 팀 규칙 추가
vim .claude/rules/development.md

# DDD 패턴 변경
vim .claude/rules/architecture.md
```

### 2. 단계별 진행

```bash
# ❌ 한 번에 전체
/arch → /plan → 개발 → /review

# ✅ 단계별 검증
/req → 수정 → /arch → 검토 → /plan → 개발 → /review
```

### 3. 티켓 단위 개발

```bash
# ❌ 전체 개발 후 리뷰
# (토큰 초과 위험)

# ✅ 티켓별 개발 + 리뷰
TICKET-1 → /review → 수정 → /commit
TICKET-2 → /review → 수정 → /commit
```

## 📖 예시

### 입력: seller-system-requirements.md

```markdown
# 셀러 시스템

## 개요
회원이 판매자로 전환...

## 데이터 모델
- 셀러명
- 사업자번호 (암호화)
  ...
```

### 출력: 자동 생성

```
.claude/
├── reports/
│   └── validations/
│       └── seller-system-validation.md  # 검증 리포트
├── docs/
│   └── seller-system/
│       ├── README.md
│       ├── 01-domain-analysis.md
│       ├── 02-bounded-contexts.md
│       ├── 03-architecture.md
│       ├── 04-api-design.md
│       ├── diagrams/
│       │   ├── domain-overview.mmd
│       │   ├── event-flow.mmd
│       │   ├── context-map.mmd
│       │   └── architecture.mmd
│       └── tickets/
│           ├── TICKET-1.md  # 파일 업로드
│           ├── TICKET-2.md  # 암호화
│           ├── TICKET-3.md  # Seller Aggregate
│           └── ...
└── reports/
    └── reviews/
        └── review-2025-02-14-seller.md  # 리뷰 리포트
```

## 🔧 설정

### 환경 변수 (선택)

```bash
# Git 커밋 언어
export GIT_COMMIT_LANG="ko"  # 기본: ko

# 자동 스테이징
export GIT_AUTO_STAGE="false"  # 기본: false
```

## 📚 참고

### Skill 구조
- [Anthropic Skills Guide](https://github.com/anthropics/skills)
- [Best Practices](https://platform.claude.com/docs/en/agents-and-tools/agent-skills/best-practices)

### DDD
- Event Storming
- Bounded Context
- Aggregate Pattern

### Semantic Commit
- [Conventional Commits](https://www.conventionalcommits.org/)

## 🔄 업데이트

### v2.0.0 (2025-02-15)
- ✅ 전체 구조 개편 (토큰 최적화)
- ✅ Agent 제거 (Commands + Skills 통합)
- ✅ Rules 시스템 추가 (커스텀 가능)
- ✅ Progressive Disclosure (SKILL.md + reference)
- ✅ 5개 Commands (심플)
- ✅ 5개 Skills (전문화)
- ✅ 코드 리뷰 4가지 병렬 분석

### v1.0.0 (2025-02-14)
- ✅ 초기 버전

## 📝 라이선스

MIT

---

**심플하고 강력한 백엔드 개발 자동화**