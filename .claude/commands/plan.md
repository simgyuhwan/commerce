---
description: 개발 티켓 분류. Feature/Domain별 분류, 우선순위, 구현 가이드, Story Points
---

# 개발 계획

planning 스킬로 티켓 분류:

**사용**:
```bash
/plan seller-system
```

**실행**:
1. 설계 문서 분석
2. Feature/Domain 분류
3. 의존성 분석
4. 우선순위 설정
5. 티켓 생성

**출력**: `docs/{도메인}/tickets/`
- TICKET-01.md
- TICKET-02.md
- ...

**티켓 구성**:
- 우선순위 (P0-P3)
- 예상 소요 (일, SP)
- 구현 범위
- 검증 기준 (DoD)

**참조**: .claude/rules/development.md

대상: $ARGUMENTS