# Seller 전환/심사 프로세스 보완 분석 및 대안 요구사항내역서 (기획+CTO 관점)

- 작성일: 2026-02-15
- 기준 문서:
`inputs/seller.md` (v1.1, 2026-02-14)
`.claude/reports/validations/seller-validation.md` (검증 리포트)
- 목적: 기존 명세의 강점은 유지하고, 실제 운영/확장/감사까지 견디는 수준의 요구사항내역서로 재정의

---

## 1) 결론 요약

현재 문서는 MVP 기준의 핵심 흐름(신청-심사-승인/거절)은 잘 정의되어 있습니다. 다만 운영 환경에서 즉시 문제가 되는 항목이 남아 있어, 개발 착수 전 다음 4가지를 필수 확정해야 합니다.

1. 상태 모델 고도화: `INACTIVE/DELETED/재신청` 규칙과 전이 조건 명확화
2. 데이터 감사 추적: 신청/심사/변경 이력의 불변 로그(Audit Trail) 도입
3. API 완결성: 조회/수정/취소/다운로드 권한검증 API 포함한 전체 계약 확정
4. 운영/보안 기준: 파일 접근통제, 외부 API 장애전략, SLA/SLO, 모니터링/알림 기준 정의

---

## 2) 기존 문서 대비 보완/누락 분석

## 2.1 Critical (출시 전 필수)

1. 상태 전이 정의 불충분
- 문제: `PENDING/APPROVED/REJECTED/INACTIVE`는 있으나 전이 조건, 권한, 예외가 없음
- 리스크: 운영자/고객센터의 임의 처리, 분쟁 시 근거 부족
- 보완: 상태전이표 + 전이 사유코드 + 수행자(actor) + timestamp 저장

2. 이력/감사 테이블 부재
- 문제: 재신청 허용 정책 대비 과거 심사/반려사유 이력의 정합성 보장 없음
- 리스크: 감사 대응 불가, 반복 악성 신청 탐지 불가
- 보완: `seller_application_history` 및 이벤트 로그 추가

3. 관리자 권한 모델 미정
- 문제: Admin API 존재하지만 세부 RBAC(심사자/관리자/감사자) 없음
- 리스크: 과권한 접근, 내부통제 미흡
- 보완: 역할별 권한 매트릭스 정의 및 감사로그(누가/언제/무엇)

4. 파일 보안 모델 불완전
- 문제: URL 직접 노출 시 우회 접근 가능
- 리스크: 사업자등록증/통장사본 유출
- 보완: 파일 ID 기반 다운로드 API + 권한검사 + 단기 presigned URL

5. 외부 계좌실명 API 실패 전략 없음
- 문제: 타임아웃/장애 시 신청 흐름 정의 부재
- 리스크: 신청 대량 실패 또는 무기한 대기
- 보완: Retry + Circuit Breaker + 수동심사 fallback + 사용자 안내 문구

## 2.2 High (출시 직후 장애/운영비용과 직결)

1. API 계약 누락
- `GET /sellers/me`, 관리자 목록 페이징, 신청취소, 변경요청 API 필요

2. 동시성/멱등성 미정
- 중복 신청 race condition 방지 (`user_id` unique + 상태 조건 + idempotency key)

3. 데이터 수명주기/보존정책 부재
- 반려/탈퇴/삭제 데이터와 첨부파일 보존기간 및 파기 시점 필요

4. 관측성 미정
- 필수 메트릭(신청수, 승인율, 심사대기시간, 외부API 실패율)과 알림 기준 필요

5. 백오피스 운영 요구 누락
- 심사 큐 필터, 긴급처리, 반려사유 템플릿, 재심 워크플로우 필요

## 2.3 Medium (확장성/품질 고도화)

1. 수수료/정산 정책 버전관리
- 정책 변경 시 소급적용 여부, 유효기간(from/to) 필요

2. 문서 진위 검증 확장성
- OCR/진위확인 연계 가능성 고려한 설계 필요

3. 개인정보 최소화/마스킹 기준
- 조회 응답/로그/백오피스 화면별 마스킹 규칙 명확화

---

## 3) 대안 아키텍처/운영 원칙

1. 상태 우선 설계
- 기능보다 상태전이와 전이 불변조건을 먼저 확정

2. 계약 우선 API
- 요청/응답/오류코드/멱등성/권한을 OpenAPI로 선확정

3. 보안 기본값 강제
- 저장 암호화 + 전송 TLS + 접근제어 + 감사로그 기본 탑재

4. 장애 내성
- 외부 연동은 실패를 정상 시나리오로 간주하고 fallback 제공

5. 운영 자동화
- 모니터링/알림/배치 정리/데이터 보존 정책까지 MVP 범위에 포함

---

## 4) 대안: 완성도 높은 요구사항내역서 (권장안 v2.0)

## 4.1 범위/목표

- 목표: 일반 회원의 Seller 전환을 안전하게 처리하고, 관리자 심사/감사/운영을 자동화
- 인스코프:
신청, 파일업로드, 자동검증, 관리자 심사, 재신청, 변경요청, 비활성/탈퇴, 감사로그, 백오피스 조회
- 아웃스코프(후속): OCR 자동심사, 이상탐지 고도화, 다국가 규정 대응

## 4.2 역할 및 권한

| 역할 | 권한 |
|---|---|
| USER | 신청/재신청, 내 정보 조회, 신청 취소, 변경 요청 |
| REVIEWER | 심사(승인/거절), 반려사유 입력, 증빙 열람 |
| ADMIN | REVIEWER 권한 + 강제중지/비활성/재활성 |
| AUDITOR | 읽기 전용 이력/로그 열람 |

## 4.3 상태 모델

| 상태 | 설명 | 허용 전이 |
|---|---|---|
| DRAFT | 임시작성 | PENDING, CANCELLED |
| PENDING | 심사대기 | APPROVED, REJECTED, CANCELLED |
| APPROVED | 승인완료 | INACTIVE |
| REJECTED | 반려 | DRAFT (재신청), CANCELLED |
| INACTIVE | 운영중지 | APPROVED, DELETED |
| DELETED | 파기/종료 | 없음 |
| CANCELLED | 사용자 취소 | DRAFT |

전이 공통 규칙
- 모든 전이는 `actor_id`, `actor_role`, `reason_code`, `changed_at` 저장
- `APPROVED -> INACTIVE -> DELETED`는 정산/주문 상태 검증 후만 허용

## 4.4 기능 요구사항 (FR)

| ID | 요구사항 | 수용 기준(Acceptance) |
|---|---|---|
| FR-001 | 1인 1셀러 정책 적용 | `user_id` 기준 동시 신청에서도 중복 레코드 미발생 |
| FR-002 | 신청 시 3종 서류 필수 | 누락 시 `MISSING_DOCUMENT` 반환 |
| FR-003 | 사업자번호/계좌 형식 검증 | 잘못된 형식은 저장 전 차단 |
| FR-004 | 계좌실명 조회 연동 | 불일치/실패 시 상태 유지 + 오류코드 반환 |
| FR-005 | 관리자 승인/거절 | 거절 시 사유 필수, 승인/거절 시각 저장 |
| FR-006 | 재신청 지원 | `REJECTED` 사용자는 신규 신청 가능, 이력 연결 유지 |
| FR-007 | 신청 취소 기능 | `PENDING`에서만 가능, 취소 이력 저장 |
| FR-008 | 셀러 정보 조회/수정 | `GET/PATCH /sellers/me` 제공, 민감정보 마스킹 |
| FR-009 | 중요정보 변경요청 | 계좌/사업자번호 변경은 재심사 플로우 |
| FR-010 | 파일 다운로드 접근통제 | 본인/권한자만 다운로드 가능 |
| FR-011 | 관리자 목록 조회 | 상태/기간/키워드 필터 + 페이징 제공 |
| FR-012 | 알림 발송 | 신청/승인/거절/추가보완 요청 시 알림 |

## 4.5 비기능 요구사항 (NFR)

| ID | 항목 | 요구사항 |
|---|---|---|
| NFR-001 | 성능 | 신청 API p95 < 800ms(외부 API 제외), 목록 API p95 < 400ms |
| NFR-002 | 가용성 | 월 가용성 99.9% |
| NFR-003 | 보안 | 저장 암호화(AES-256), 전송 TLS1.2+, 키는 KMS/Vault 관리 |
| NFR-004 | 감사 | 모든 상태변경/권한행위 감사로그 1년 이상 보존 |
| NFR-005 | 관측성 | 필수 메트릭/로그/트레이스 수집 및 경보 설정 |
| NFR-006 | 복구 | RPO 15분, RTO 1시간 |
| NFR-007 | 확장성 | 외부 API 장애 시 fallback으로 심사 플로우 지속 |

## 4.6 데이터 요구사항

핵심 테이블
- `sellers`
- `seller_documents`
- `seller_application_history` (불변)
- `seller_change_requests`

필수 컬럼 추가 권장
- `sellers`: `approved_at`, `rejected_at`, `reject_reason`, `reviewer_id`, `version`
- `seller_documents`: `original_filename`, `file_size`, `mime_type`, `status`, `created_at`

필수 인덱스
- `sellers(user_id)` unique
- `sellers(apply_status, applied_at desc)`
- `seller_documents(seller_id, status)`
- `seller_application_history(seller_id, created_at desc)`

## 4.7 API 요구사항

필수 엔드포인트
- `POST /api/v1/files/upload`
- `GET /api/v1/files/{fileId}`
- `POST /api/v1/sellers/apply`
- `DELETE /api/v1/sellers/apply` (취소)
- `GET /api/v1/sellers/me`
- `PATCH /api/v1/sellers/me`
- `POST /api/v1/sellers/change-request`
- `GET /api/v1/admin/sellers`
- `GET /api/v1/admin/sellers/{sellerId}`
- `POST /api/v1/admin/sellers/{sellerId}/approve`
- `POST /api/v1/admin/sellers/{sellerId}/reject`
- `POST /api/v1/admin/sellers/{sellerId}/suspend`

오류 응답 표준
- `success=false`, `error.code`, `error.message`, `traceId`, `timestamp`, `path`

## 4.8 보안/컴플라이언스 요구사항

- 민감정보 로그 출력 금지, 마스킹 규칙 적용
- 파일 업로드 시 확장자+MIME+크기 검증
- presigned URL 만료 5분, 1회성 토큰 권장
- 관리자 행위 4-eyes(선택): 고위험 작업은 승인자 분리

## 4.9 운영 요구사항

배치
- 장기 미처리 `PENDING` 알림/에스컬레이션
- `DELETED` 대상 파일 지연 삭제 배치

모니터링
- 신청 성공률, 승인율, 평균 심사시간, 외부 API timeout rate
- 경보: 외부 API 실패율 5분 평균 5% 초과 시

## 4.10 테스트/검증 기준

- 단위: 핵심 도메인 로직 커버리지 85%+
- 통합: API/권한/암호화/파일 접근 테스트 필수
- E2E: 신청-심사-승인/거절-재신청 시나리오
- 부하: 피크 시간 동시 신청 200req/min에서 오류율 <1%

## 4.11 마이그레이션/릴리스 기준

- DB 마이그레이션은 backward-compatible 1차 배포 후 코드 전환
- Feature flag로 `change-request`, `suspend` 단계적 오픈
- 롤백 계획: 스키마 유지 + 앱 버전 롤백 가능성 보장

---

## 5) 실행 우선순위

P0 (즉시)
- 상태전이표 확정
- 이력 테이블/감사로그 도입
- 파일 접근통제 API 적용
- 관리자 RBAC 및 API 계약 확정

P1 (단기)
- 변경요청 플로우
- 백오피스 심사 큐/검색/필터
- 모니터링/알림/대시보드

P2 (중기)
- 심사 SLA 자동화
- 문서 검증 자동화(OCR/진위확인)
- 정책 버전관리(수수료/정산)

---

## 6) 의사결정 필요 항목

1. 재신청 대기기간 필요 여부(예: 거절 후 7일)
2. 감사로그/문서 보존기간(예: 1년/3년)
3. 외부 계좌실명 API 공급자 및 장애시 fallback 정책
4. `INACTIVE -> DELETED` 전이 시점의 정산/주문 정리 원칙
5. 관리자 역할 분리 수준(REVIEWER/ADMIN/AUDITOR)

---

## 7) 최종 제안

- 현재 명세는 MVP 수준으로는 충분하나, 운영형 서비스 기준으로는 상태/감사/보안/운영 요구가 부족합니다.
- 위 v2.0 요구사항으로 재정의 후 개발하면, 출시 이후 정책변경/감사/장애 대응 비용을 크게 줄일 수 있습니다.
