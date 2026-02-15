# Security 검증

참조: `.claude/rules/security.md`

## 자동 검증

### SQL Injection
```bash
grep -rn "\"SELECT.*+.*\"" --include="*.java"
grep -rn "\"INSERT.*+.*\"" --include="*.java"
```

### 하드코딩 시크릿
```bash
grep -rn "password.*=.*\"" --include="*.yml" --include="*.properties"
grep -rn "api.*key.*=.*\"" --include="*.yml"
```

### 취약한 암호화
```bash
grep -rn "MD5\|SHA1" --include="*.java"
```

### 인증 누락
```bash
# @PreAuthorize 없는 API
grep -rn "@GetMapping\|@PostMapping" --include="*Controller.java" | grep -v "@PreAuthorize\|@Secured"
```

## 체크리스트

### 인증/인가
- [ ] JWT 설정
- [ ] @PreAuthorize 적용
- [ ] 권한 검증

### 입력 검증
- [ ] @Valid 사용
- [ ] XSS 필터
- [ ] SQL Injection 방어

### 민감정보
- [ ] AES-256 암호화
- [ ] bcrypt 해싱
- [ ] 로그 마스킹

### API
- [ ] HTTPS
- [ ] Rate Limiting
- [ ] CORS 설정

## 리포트 형식

```markdown
## 🔒 Security (N건)

### [S-1] SQL Injection 취약점
**위치**: `UserService.java:45`
**심각도**: 🔴 Critical
**문제**:
```java
String sql = "SELECT * FROM users WHERE name = '" + name + "'";
```
**위험**: 임의 SQL 실행 가능
**해결**:
```java
String sql = "SELECT * FROM users WHERE name = ?";
jdbcTemplate.query(sql, name);
```
```