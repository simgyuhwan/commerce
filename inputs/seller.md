# 셀러(Seller) 시스템 상세 요구사항 명세서

**문서 버전**: v1.1  
**최종 수정일**: 2026-02-14  
**작성자**: G  
**상태**: 개발 대기 (Ready for Dev)

---

## 1. 개요 (Overview)

본 문서는 일반 회원이 판매자(Seller)로 전환하기 위한 신청 프로세스와 관리자의 심사(승인/거절) 프로세스를 정의한다.

**셀러가 되어야만 방송 및 상품 등록, 정산이 가능하다.**

### 1.1 핵심 정책

- **1인 1셀러 정책**: 한 명의 회원은 하나의 셀러 계정만 신청/보유할 수 있다.
- **선검증 후심사**: 시스템 자동 검증(형식, 중복, 계좌실명)을 통과한 건에 한해 관리자 심사가 진행된다.
- **데이터 보안**: 사업자등록번호와 계좌번호는 DB 저장 시 반드시 암호화(AES-256) 처리한다.
- **상태 기반 로직**: 셀러의 상태(PENDING, APPROVED, REJECTED)에 따라 기능 접근을 제어한다.

---

## 2. 데이터 모델 (Data Model)

### 2.1 셀러 정보 (sellers)

| 필드명    | 컬럼명               | 타입           | 필수 | 제약조건 / 설명                                          | 암호화 |
|--------|-------------------|--------------|------|----------------------------------------------------|--------|
| ID     | `id`              | BIGINT       | Y | PK, Auto Increment                                 | N |
| 사용자 ID | `user_id`         | BIGINT       | Y | users.id, Unique                                   | N |
| 셀러명    | `seller_name`     | VARCHAR(50)  | Y | 한글/영문/숫자, 특수문자 불가                                  | N |
| 업체명    | `company_name`    | VARCHAR(50)  | Y | 법인명 또는 상호명                                         | N |
| 사업자번호  | `business_no`     | VARCHAR(20)  | Y | 형식: XXX-XX-XXXXX                                   | **Y** |
| 통신판매업  | `mail_order_no`   | VARCHAR(30)  | Y | 예: 제2024-서울강남-0000호                                | N |
| 대표자명   | `rep_name`        | VARCHAR(20)  | Y | -                                                  | N |
| 업태     | `business_type`   | VARCHAR(30)  | Y | 예: 도소매, 제조업                                        | N |
| 은행코드   | `bank_code`       | CHAR(3)      | Y | 공통 은행 코드 (예: 004, 088)                             | N |
| 계좌번호   | `account_no`      | VARCHAR(30)  | Y | 숫자만 허용                                             | **Y** |
| 예금주명   | `account_holder`  | VARCHAR(20)  | Y | 실명 확인 필수                                           | N |
| 기본수수료  | `commission_rate` | DECIMAL(5,2) | Y | 범위: 0.00 ~ 30.00 (%)                               | N |
| 회사전화   | `company_phone`   | VARCHAR(15)  | Y | 지역번호 포함 (02-1234-5678)                             | N |
| 정산유형   | `settlement_rule` | ENUM         | Y | PURCHASE_CONFIRMED(구매확정), SHIPPING_COMPLETED(발송완료) | N |
| 담당자명   | `manager_name`    | VARCHAR(20)  | Y | -                                                  | N |
| 담당자폰   | `manager_phone`   | VARCHAR(15)  | Y | 휴대폰 형식 (010-XXXX-XXXX)                             | N |
| 이메일    | `email`           | VARCHAR(100) | Y | 이메일 형식 검증                                          | N |
| 우편번호   | `zip_code`        | CHAR(5)      | Y | -                                                  | N |
| 주소     | `address`         | VARCHAR(100) | Y | 기본 주소                                              | N |
| 상세주소   | `address_detail`  | VARCHAR(100) | N | 상세 주소                                              | N |
| 기타사항   | `note`            | TEXT         | N | 최대 500자                                            | N |
| 신청상태   | `apply_status`    | ENUM         | Y | PENDING, APPROVED, REJECTED, INACTIVE              | N |
| 신청일시   | `applied_at`      | DATETIME     | Y | -                                                  | N |
| 상태     | `status`          | ENUM         | Y | ACTIVE, DELETED                                    | N |
| 생성일    | `createdAt`       | DATETIME         | Y |                                  | N |
| 수정일    | `updatedAt`       | DATETIME         | Y |                                  | N |

### 2.2 제출 서류 (seller_documents)

신청 시 3가지 서류가 모두 업로드되어야 함. 기본은 로컬에 저장(application.yml 경로로 저장) 추후 S3 URL로 저장.

| 필드명 | 컬럼명 | 타입 | 설명 |
|--------|--------|------|------|
| ID | `id` | BIGINT | PK |
| 셀러 ID | `seller_id` | BIGINT | FK |
| 문서 유형 | `doc_type` | ENUM | BUSINESS_LICENSE(사업자등록증), MAIL_ORDER_LICENSE(통신판매업), BANK_BOOK(통장사본) |
| 파일 URL | `file_url` | VARCHAR(255) | S3 Presigned URL 또는 CDN URL |

---

## 3. 기능 상세 (Functional Requirements)

### 3.1 파일 업로드

- **제약**: 파일당 5MB 이하, 확장자(jpg, png, pdf) 제한.
- **보안**: 업로드된 파일은 유추 불가능한 UUID 파일명으로 저장.
- **응답**: 업로드 성공 시 파일 URL을 반환하여 신청 API에 사용.

### 3.2 셀러 신청 (User)

#### 사전 검증
- 현재 로그인한 사용자가 이미 셀러(PENDING/APPROVED)인지 확인.
- REJECTED 상태인 경우 재신청 허용.

#### 입력값 검증
- 사업자번호 형식 검증 (10자리, 검증 알고리즘 적용 권장).
- 필수 첨부파일 3건 존재 여부 확인.
- 수수료율 범위(0~30%) 확인.

#### 계좌 실명 조회 (외부 API 연동)
- 은행코드 + 계좌번호 + 예금주명이 일치하는지 확인.
- 개발 환경에서는 Mocking 처리 (특정 번호 입력 시 성공/실패 분기).

#### 저장
- 검증 통과 시 상태를 PENDING으로 저장.
- 민감정보(사업자번호, 계좌번호) 암호화 저장.

### 3.3 관리자 심사 (Admin)

#### 승인 (Approve)
- 셀러 상태를 APPROVED로 변경.
- 승인 일시(approved_at) 기록.

#### 거절 (Reject)
- 셀러 상태를 REJECTED로 변경.
- 거절 사유(reject_reason) 필수 입력.

---

## 4. API 명세 (API Specifications)

### 4.1 파일 업로드

**POST** `/api/v1/files/upload`

**Request**: MultipartFile (form-data)

**Response**:
```json
{
  "success": true,
  "data": {
    "fileUrl": "https://s3.ap-northeast-2.amazonaws.com/.../uuid_file.pdf",
    "fileName": "original_name.pdf"
  }
}
```

### 4.2 셀러 신청

**POST** `/api/v1/sellers/apply`

**Header**: `Authorization: Bearer {token}`

**Body**:
```json
{
  "sellerName": "홍길동마켓",
  "companyName": "(주)홍길동무역",
  "businessNo": "123-45-67890",
  "mailOrderNo": "제2024-서울강남-00123호",
  "repName": "홍길동",
  "businessType": "도소매",
  "bankCode": "004",
  "accountNo": "110123456789",
  "accountHolder": "홍길동",
  "commissionRate": 10.5,
  "companyPhone": "02-1234-5678",
  "settlementRule": "PURCHASE_CONFIRMED",
  "managerName": "김담당",
  "managerPhone": "010-1234-5678",
  "email": "manager@example.com",
  "zipCode": "06234",
  "address": "서울특별시 강남구 테헤란로 123",
  "addressDetail": "ABC빌딩 4층",
  "note": "특이사항 없음",
  "documents": [
    { "type": "BUSINESS_LICENSE", "url": "https://..." },
    { "type": "MAIL_ORDER_LICENSE", "url": "https://..." },
    { "type": "BANK_BOOK", "url": "https://..." }
  ]
}
```

### 4.3 관리자 승인/거절

**POST** `/api/v1/admin/sellers/{sellerId}/approve`

**POST** `/api/v1/admin/sellers/{sellerId}/reject`

**Reject Body**:
```json
{
  "reason": "사업자등록증 식별 불가로 인한 반려"
}
```

---

## 5. 보안 및 예외 처리 (Security & Exception)

### 5.1 암호화 정책

- **대상**: `business_no` (사업자번호), `account_no` (계좌번호)
- **알고리즘**: AES-256 (양방향 암호화)
- **Key 관리**: 환경변수 또는 KMS로 관리, 코드 내 하드코딩 금지.

### 5.2 에러 코드 (Error Codes)

| HTTP Code | Error Code | Message | 처리 가이드 |
|-----------|------------|---------|-------------|
| 409 | DUPLICATE_APPLICATION | 이미 신청 중이거나 승인된 셀러입니다. | '내 정보' 페이지로 리다이렉트 |
| 400 | INVALID_BUSINESS_NO | 사업자등록번호 형식이 올바르지 않습니다. | 입력 폼 에러 메시지 노출 |
| 400 | ACCOUNT_VERIFY_FAILED | 계좌 실명 확인에 실패했습니다. | 은행, 계좌, 예금주 확인 요청 |
| 400 | INVALID_COMMISSION | 수수료율은 0~30% 사이여야 합니다. | - |
| 400 | MISSING_DOCUMENT | 필수 서류가 누락되었습니다. | 3개 파일 모두 업로드 확인 |
| 413 | FILE_SIZE_EXCEEDED | 파일 크기는 5MB를 초과할 수 없습니다. | - |

---

## 6. 테스트 시나리오 (QA Checklist)

- **정상 신청**: 모든 필수값 입력 + 유효한 파일 3개 → 상태 PENDING 저장 확인.
- **중복 신청**: PENDING 상태에서 다시 신청 API 호출 → 409 Conflict 확인.
- **재신청**: REJECTED 상태인 계정으로 다시 신청 → 정상 접수 확인.
- **계좌 오류**: 예금주명이 다른 경우 → 400 Error 및 저장되지 않음 확인.
- **관리자 승인**: PENDING → APPROVED 변경 및 승인일시 기록 확인.
- **암호화 확인**: DB 직접 조회 시 사업자번호/계좌번호가 암호문으로 보이는지 확인.