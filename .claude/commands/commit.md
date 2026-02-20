---
description: Git 자동 커밋. Semantic Commit 규칙, 한글 메시지, 타입/스코프 자동 분류
---

# Git 커밋

git 스킬로 자동 커밋:

**사용**:
```bash
/commit                    # 자동
/commit --interactive      # 확인 후
```

**실행**:
1. 변경사항 분석
2. Type 결정 (feat/fix/refactor...)
3. Scope 추출 (파일 경로 → 도메인)
4. Subject 생성 (50자 이내, 한글)
5. Body 작성 (3개 이상 변경 시)
6. 커밋

**Semantic Commit**:
```
feat(seller): 셀러 신청 기능 추가

- SellerService 생성
- 암호화 적용
- 검증 로직

Resolves: #123
```

**참조**: .claude/rules/development.md

대상: $ARGUMENTS