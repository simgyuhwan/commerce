---
description: DDD 아키텍처 설계. Event Storming, Bounded Context, Mermaid 다이어그램, 단계별 상세 가이드
---

# 아키텍처 설계

architecture 스킬로 DDD 설계 자동화:

**사용**:
```bash
/arch seller docs/seller/spec.md
```

**실행**:
1. Event Storming
2. Bounded Context 정의
3. Aggregate 설계
4. API 설계
5. Mermaid 다이어그램 생성

**출력**: `docs/{도메인}/`
- README.md
- architecture/01-domain-analysis.md
- architecture/02-bounded-contexts.md
- architecture/03-architecture.md
- architecture/04-api-design.md
- diagrams/ (4개)

**참조**: .claude/rules/architecture.md

대상: $ARGUMENTS