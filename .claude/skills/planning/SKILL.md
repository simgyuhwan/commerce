---
name: planning
description: 개발 티켓 분류 전문가. 설계 기반 feature/domain별 티켓 생성, 우선순위, 구현 가이드
allowed-tools: Read, Write
---

# 개발 계획 스킬

## 입력
- 프로젝트명: `$ARGUMENTS`
- 설계 문서: `.claude/docs/{프로젝트}/`

## 워크플로우

1. **설계 문서 읽기**
2. **.claude/rules 참조**: development.md
3. **티켓 분류**: reference/breakdown.md 참조
4. **티켓 생성**: `.claude/docs/{프로젝트}/tickets/`

## 티켓 구조

```markdown
# TICKET-1: 셀러 신청 API

## 우선순위
P0 (High)

## 예상 소요
3일 (6 Story Points)

## 구현 범위
- POST /api/v1/sellers/apply
- SellerService.create()
- Validation
- 암호화

## 체크리스트
- [ ] API 구현
- [ ] Unit Test
- [ ] Integration Test
- [ ] 문서화

## 구현 가이드
[상세 가이드]

## 검증 기준
- API 응답 201
- DB 암호화 확인
```

## 분류 기준

- **Feature**: 사용자 기능 (회원가입, 주문)
- **Domain**: 도메인 모델 (Aggregate, Entity)
- **Infrastructure**: 공통 (파일 업로드, 인증)

## 우선순위

- P0: Blocker (필수)
- P1: High (핵심)
- P2: Medium (보통)
- P3: Low (추후)

상세: `reference/breakdown.md`