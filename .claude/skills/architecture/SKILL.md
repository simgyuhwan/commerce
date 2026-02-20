---
name: architecture
description: DDD 아키텍처 설계 전문가. Event Storming 기반 설계, Mermaid 다이어그램, 단계별 상세 가이드
allowed-tools: Read, Write, Edit
---

# 아키텍처 설계 스킬

## 입력
- 프로젝트명: `$ARGUMENTS` 첫 번째
- 요구사항 파일: `$ARGUMENTS` 두 번째

## 워크플로우

1. **.claude/rules 읽기**: architecture.md, development.md
2. **요구사항 읽기**
3. **Event Storming**: reference/event-storming.md 참조
4. **설계 문서 생성**: `.claude/docs/{프로젝트}/`
5. **다이어그램 생성**: Mermaid

## 생성 문서 (5개)

```
.claude/docs/{프로젝트}/
├── README.md
├── 01-domain-analysis.md
├── 02-bounded-contexts.md
├── 03-architecture.md
├── 04-api-design.md
└── diagrams/
    ├── domain-overview.mmd
    ├── event-flow.mmd
    ├── context-map.mmd
    └── architecture.mmd
```

## Event Storming 단계

1. Domain Events 도출
2. Commands 식별
3. Aggregates 설계
4. Bounded Context 정의
5. Context Map 작성

상세: `reference/event-storming.md`

## 다이어그램 템플릿

`reference/templates/`에 Mermaid 템플릿

## 품질 기준

- ✅ 비개발자 이해 가능 (한글)
- ✅ 구현 가능한 상세도
- ✅ .claude/rules 준수
- ✅ 다이어그램 독립적 이해