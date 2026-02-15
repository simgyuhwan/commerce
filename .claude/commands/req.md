---
description: 요구사항 검증 및 개선. 7가지 관점 분석, Critical 자동 수정, 역질문, 실행 계획, 예상 반론
---

# 요구사항 검증 & 개선

requirements 스킬로 제1원칙 기반 검증:

**사용**:
```bash
/req inputs/seller-system.md
```

**실행**:
1. 7가지 관점 검증
2. Critical 자동 수정
3. 역질문 생성 (20개)
4. 실행 계획 작성
5. 예상 반론 대응

**출력**: `.claude/reports/validations/{프로젝트}-validation.md`

**검증 항목**:
- 비즈니스 로직
- 데이터 모델
- API 명세
- 보안
- 성능
- 확장성
- 운영

대상: $ARGUMENTS