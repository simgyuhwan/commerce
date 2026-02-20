# 보안 규칙 (Security Rules)

## 인증/인가

### JWT
- Access Token: 1시간
- Refresh Token: 14일
- HttpOnly Cookie 저장

### 권한
- `@PreAuthorize` 사용
- 메서드 레벨 검증
- Role 기반

## 입력 검증

### 필수
```java
@Valid @RequestBody CreateOrderRequest request
```

### XSS 방어
- HTML 태그 필터링
- 출력 시 인코딩

### SQL Injection 방어
```java
// ✅ Good
jdbcTemplate.query("SELECT * FROM users WHERE id = ?", userId);

// ❌ Bad
jdbcTemplate.query("SELECT * FROM users WHERE id = '" + userId + "'");
```

## 민감정보

### 암호화
- PII: AES-256-GCM
- 비밀번호: bcrypt (cost=12)
- API Key: 환경 변수

### 마스킹
```java
// 로그
log.info("User: {}", maskPhone(phone)); // 010-****-5678
```

### 저장 금지
❌ 비밀번호 평문
❌ 카드 번호
❌ 주민번호

## API 보안

### Rate Limiting
- 로그인: 5회/분
- API: 100회/분
- 파일 업로드: 10회/시간

### CORS
```java
@CrossOrigin(origins = "https://example.com")
```

### HTTPS
- 프로덕션 필수
- HSTS 헤더

## 금지 사항

❌ 평문 저장
❌ 하드코딩 시크릿
❌ SQL 문자열 결합
❌ MD5, SHA1 해싱
❌ 에러에 민감정보 노출