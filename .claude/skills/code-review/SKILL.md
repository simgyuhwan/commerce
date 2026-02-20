---
name: code-review
description: 코드 리뷰 전문가. Performance, Security, Testing, Requirements 4가지 관점 병렬 분석하여 종합 리포트 생성
allowed-tools: Read, Grep, Glob, Bash
---

# 코드 리뷰 스킬

## 워크플로우

1. **컨텍스트**: 변경 파일, 티켓 정보, .claude/rules
2. **병렬 분석** (4가지):
    - 🚀 Performance: reference/performance.md
    - 🔒 Security: reference/security.md
    - ✅ Testing: reference/testing.md
    - 📋 Requirements: reference/requirements.md
3. **종합 리포트**: `.claude/reports/reviews/review-{date}-{target}.md`

## 대상
- `$ARGUMENTS` 없음: 전체 프로젝트
- `$ARGUMENTS` 경로: 특정 경로만
- `$ARGUMENTS` --recent: 최근 변경만

## 출력 형식

```markdown
# 코드 리뷰

## 📊 요약
🔴 2 | 🟡 5 | 🟢 8

## 🚀 Performance (3건)
[P-1] N+1 쿼리
...

## 🔒 Security (2건)
[S-1] SQL Injection
...

## ✅ Testing (5건)
[T-1] 커버리지 부족
...

## 📋 Requirements (3건)
[R-1] 명세 미준수
...

## 🎯 우선순위
1. [S-1] Critical
2. [P-1] Major
...
```

## 검증 패턴

```bash
# Performance
grep -rn "@OneToMany" | grep -v "JOIN FETCH"

# Security
grep -rn "\"SELECT.*+.*\""

# Testing
find . -name "*Test.java" | wc -l
```

상세: `reference/`