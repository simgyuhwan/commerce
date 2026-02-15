# Semantic Commit Types

## 타입별 설명

### feat (기능)
새로운 기능 추가
```
feat(auth): 소셜 로그인 추가
feat(order): 주문 취소 기능
```

### fix (수정)
버그 수정
```
fix(payment): NPE 수정
fix(seller): 암호화 누락 수정
```

### refactor (리팩토링)
기능 변경 없는 코드 개선
```
refactor(order): 주문 검증 로직 분리
refactor: Validator 클래스로 추출
```

### perf (성능)
성능 개선
```
perf(order): N+1 쿼리 제거
perf: 캐싱 추가
```

### docs (문서)
문서 수정
```
docs: README 업데이트
docs(api): API 명세 추가
```

### test (테스트)
테스트 추가/수정
```
test(seller): 엣지 케이스 추가
test: 통합 테스트 작성
```

### chore (기타)
빌드, 설정 등
```
chore: 의존성 업데이트
chore: application.yml 수정
```

## Subject 규칙

### 한글
- 50자 이내
- 명령형 (동사 원형)
- 마침표 없음

```
✅ 주문 취소 기능 추가
❌ 주문 취소 기능을 추가했습니다.
```

### 영문
```
✅ Add order cancel feature
❌ Added order cancel feature
```

## Body (선택)

3개 이상 변경 시:
```
feat(seller): 셀러 신청 기능 추가

- SellerService 신규 생성
- 암호화 로직 추가 (AES-256)
- 계좌 실명 조회 연동
```

## Footer (선택)

```
Resolves: #123
Related: #456, #789
BREAKING CHANGE: API 응답 형식 변경
```

## 예시

### 예시 1: 기능 추가
```
feat(seller): 셀러 승인 API 추가

- 관리자 권한 검증
- 승인 일시 기록
- 알림 발송

Resolves: #234
```

### 예시 2: 버그 수정
```
fix(payment): 결제 타임아웃 처리 수정

결제 API 타임아웃 시 주문 상태가
PENDING으로 남는 문제 수정
```

### 예시 3: 리팩토링
```
refactor(seller): 검증 로직 분리

SellerValidator 클래스로 추출하여
단일 책임 원칙 준수
```