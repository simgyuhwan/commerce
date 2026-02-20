---
name: requirements
description: 요구사항 검증 및 개선. 제1원칙 기반 7가지 관점 분석, 자동 수정, 실행 계획, 예상 반론 생성
allowed-tools: Read, Write, Edit
---

# 요구사항 스킬

## 실행 로직

```bash
# 1. 디렉토리 생성
mkdir -p .claude/reports/validations

# 2. 입력 파일 읽기
INPUT_FILE="$ARGUMENTS"
cat "$INPUT_FILE"

# 3. Rules 읽기
cat .claude/rules/architecture.md
cat .claude/rules/security.md
cat .claude/rules/performance.md

# 4. 검증 수행 (reference/checklist.md 참조)
# 5. 리포트 생성
```

## 워크플로우

1. **파일 읽기**: `$ARGUMENTS` 요구사항 파일
2. **.claude/rules 참조**: architecture.md, security.md, performance.md
3. **7가지 검증**: reference/checklist.md 참조
4. **자동 수정**: Critical 이슈
5. **리포트 생성**: `.claude/reports/validations/{프로젝트}-validation.md`

## 7가지 관점

상세는 `reference/checklist.md` 참조:
1. 비즈니스 로직 (예외, 상태, 동시성)
2. 데이터 모델 (타입, 제약, 관계)
3. API 명세
4. 보안
5. 성능
6. 확장성
7. 운영

## 출력 형식

```markdown
# {프로젝트} 요구사항 검증

## 완성도
- 비즈니스: 60% 🟡
- 보안: 20% 🔴
  ...

## 🔴 Critical (자동 수정)
[C-1] 문제
→ 수정안 제시
→ 파일 업데이트

## 🟡 Major
[M-1] 문제
→ 개선 방향

## ❓ 역질문 (20개)
기획자 확인 필요

## 📋 실행 계획
1단계: ...
2단계: ...

## 💬 예상 반론
Q: "시간 부족한데?"
A: Critical만 먼저
```

상세: `reference/` 참조