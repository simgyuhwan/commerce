---
description: 코드 리뷰. Performance, Security, Testing, Requirements 4가지 관점 병렬 분석
---

# 코드 리뷰

code-review 스킬로 종합 분석:

**사용**:
```bash
/review                          # 전체
/review src/main/java/seller    # 특정 경로
/review --recent                 # 최근 변경
```

**병렬 분석 (4가지)**:
- 🚀 Performance (N+1, 캐싱, 인덱스)
- 🔒 Security (SQL Injection, 암호화, 인증)
- ✅ Testing (커버리지, 엣지 케이스)
- 📋 Requirements (명세 준수)

**출력**: `reports/{YYYY-MM-DD}-{도메인}-review.md`

**리포트 구성**:
- 요약 (🔴 Critical, 🟡 Major, 🟢 Minor)
- 4가지 관점별 이슈
- 우선순위 액션 아이템

**참조**: .claude/rules/

대상: $ARGUMENTS