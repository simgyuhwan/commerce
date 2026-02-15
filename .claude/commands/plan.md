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

**출력**: `.claude/docs/{프로젝트}/tickets/`
- TICKET-1.md (파일 업로드)
- TICKET-2.md (암호화)
- TICKET-3.md (Seller Aggregate)
- ...

**티켓 구성**:
- 우선순위 (P0-P3)
- 예상 소요 (일, SP)
- 구현 범위
- 검증 기준 (DoD)

**참조**: .claude/rules/development.md

대상: $ARGUMENTS