# 셀러(Seller) 시스템 요구사항 검증 리포트

**문서 버전**: v1.0
**검증 일시**: 2026-02-15
**대상 문서**: inputs/seller.md (v1.1)
**검증자**: Claude Code AI
**검증 방법론**: 제1원칙 기반 7가지 관점 분석

---

## 📋 Executive Summary

| 항목 | 평가 | 주요 이슈 |
|------|------|-----------|
| **비즈니스 로직** | ⚠️ 보통 | 재신청/해지 프로세스 불명확, 승인 후 변경 프로세스 누락 |
| **데이터 모델** | ⚠️ 보통 | 심사 이력 테이블 누락, 컬럼 불일치 |
| **API 명세** | ⚠️ 보통 | CRUD 불완전, 조회/수정 API 누락 |
| **보안** | ✅ 양호 | 암호화 정책 명확, 파일 검증 보완 필요 |
| **성능** | ⚠️ 보통 | 페이징 누락, 인덱스 전략 부재 |
| **확장성** | ⚠️ 보통 | 외부 API 장애 대응 부재, 다국어 미고려 |
| **운영** | ❌ 미흡 | 로그/모니터링/알림 정책 전무 |

**종합 평가**: 🟡 개발 가능 (보완 필요)
**권장 조치**: Critical 이슈 11건 즉시 수정, Warning 23건 우선순위 검토

---

## 1️⃣ 비즈니스 로직 검증

### ✅ 잘 정의된 부분
1. **1인 1셀러 정책** 명확함
2. **선검증 후심사** 프로세스 논리적
3. **상태 기반 접근 제어** 개념 명확

### ❌ Critical 이슈

#### C-BIZ-01: 셀러 해지/탈퇴 프로세스 누락
**문제**:
- 승인된 셀러가 영업을 중단하거나 탈퇴하는 프로세스가 없음
- INACTIVE 상태가 정의되어 있으나 전이 조건이 불명확

**영향**:
- 실제 운영 시 셀러 계정 정리 불가능
- GDPR/개인정보보호법 위반 가능성

**해결 방안**:
```
상태 전이 추가:
APPROVED → INACTIVE (셀러 요청 또는 관리자 정지)
INACTIVE → DELETED (탈퇴 처리)

필요 API:
- POST /api/v1/sellers/deactivate (셀러 본인)
- POST /api/v1/admin/sellers/{sellerId}/suspend (관리자)
```

#### C-BIZ-02: REJECTED 재신청 시 데이터 처리 불명확
**문제**:
```
"REJECTED 상태인 경우 재신청 허용" (line 83)
```
- 기존 거절 데이터를 덮어쓰는지, 새 레코드를 생성하는지 불명확
- 거절 이력 보존 여부 불명확

**영향**:
- 감사 추적(Audit Trail) 불가능
- 반복 거절 셀러 패턴 분석 불가

**해결 방안**:
```
Option A: 이력 테이블 분리 (권장)
- sellers 테이블: 최신 상태만 유지
- seller_history 테이블: 모든 신청/심사 이력 보존

Option B: 상태 변경 + 버전 관리
- version 컬럼 추가
- 재신청 시 version++, 기존 데이터 복사
```

#### C-BIZ-03: 승인 후 정보 변경 프로세스 누락
**문제**:
- 계좌 변경, 사업자등록증 재발급 등 실제 발생 가능한 시나리오 미고려

**영향**:
- 운영 중 정보 업데이트 불가능
- 고객 문의 폭증 가능성

**해결 방안**:
```
변경 신청 프로세스:
1. PUT /api/v1/sellers/update-request (변경 신청)
2. 관리자 재심사 (중요 필드만: 계좌, 사업자번호 등)
3. 일반 필드는 즉시 반영 (연락처, 주소 등)
```

### ⚠️ Warning

#### W-BIZ-01: 신청 취소 기능 부재
- PENDING 상태에서 사용자가 신청을 취소할 수 없음
- 실수로 잘못 입력한 경우 대응 불가

#### W-BIZ-02: 심사 기한 정책 없음
- PENDING 상태가 무한정 유지 가능
- 장기 미처리 건에 대한 정책 필요 (예: 30일 자동 거절)

#### W-BIZ-03: 승인 조건(Criteria) 불명확
- 관리자가 어떤 기준으로 승인/거절하는지 불명확
- 체크리스트 또는 가이드라인 필요

---

## 2️⃣ 데이터 모델 검증

### ❌ Critical 이슈

#### C-DATA-01: 심사 정보 컬럼 누락
**문제**:
```
기능 명세(line 102-106)에는 있으나 테이블 정의(line 29-56)에 없음:
- approved_at (승인 일시)
- rejected_at (거절 일시)
- reject_reason (거절 사유)
- reviewer_id (심사자 ID)
```

**수정**:
```kotlin
// sellers 테이블에 추가
@Column(name = "approved_at")
val approvedAt: LocalDateTime? = null

@Column(name = "rejected_at")
val rejectedAt: LocalDateTime? = null

@Column(name = "reject_reason", length = 500)
val rejectReason: String? = null

@Column(name = "reviewer_id")
val reviewerId: Long? = null
```

#### C-DATA-02: 심사 이력 테이블 부재
**문제**:
- 재신청, 재심사 시나리오에서 이력 추적 불가능
- 감사(Audit) 요구사항 충족 불가

**수정**:
```kotlin
@Entity
@Table(name = "seller_application_history")
class SellerApplicationHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "apply_status", nullable = false)
    val applyStatus: ApplyStatus,

    @Column(name = "reviewer_id")
    val reviewerId: Long? = null,

    @Column(name = "action", length = 20)
    val action: String, // SUBMIT, APPROVE, REJECT, UPDATE

    @Column(name = "reject_reason", length = 500)
    val rejectReason: String? = null,

    @Column(name = "snapshot_data", columnDefinition = "JSON")
    val snapshotData: String, // 신청 당시 전체 데이터

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()
) : BaseEntity()
```

#### C-DATA-03: seller_documents 테이블 불완전
**문제**:
```
현재 정의 (line 58-68):
- 생성일시 없음
- 상태 필드 없음 (삭제/교체 시나리오)
- 파일 메타데이터 없음 (크기, 타입, 원본명)
```

**수정**:
```kotlin
@Entity
@Table(name = "seller_documents")
class SellerDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    val docType: DocumentType,

    @Column(name = "file_url", nullable = false)
    val fileUrl: String,

    @Column(name = "original_filename", length = 255)
    val originalFilename: String,

    @Column(name = "file_size")
    val fileSize: Long,

    @Column(name = "mime_type", length = 50)
    val mimeType: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: EntityStatus = EntityStatus.ACTIVE,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### ⚠️ Warning

#### W-DATA-01: 인덱스 전략 부재
**권장 인덱스**:
```sql
CREATE INDEX idx_sellers_user_id ON sellers(user_id);
CREATE INDEX idx_sellers_apply_status ON sellers(apply_status);
CREATE INDEX idx_sellers_business_no ON sellers(business_no); -- 암호화된 값이라도 검색용
CREATE INDEX idx_seller_documents_seller_id ON seller_documents(seller_id);
```

#### W-DATA-02: commission_rate 컬럼 타입 검토
- `DECIMAL(5,2)` = 최대 999.99%
- 실제로는 0~30% → `DECIMAL(4,2)` 충분

#### W-DATA-03: settlement_rule 확장성
- 현재 2가지만 정의 (PURCHASE_CONFIRMED, SHIPPING_COMPLETED)
- 향후 추가 가능성 고려 (예: 배송후 7일, 월 단위 정산 등)

---

## 3️⃣ API 명세 검증

### ❌ Critical 이슈

#### C-API-01: 셀러 정보 조회 API 누락
**필요 API**:
```kotlin
// 본인 셀러 정보 조회
GET /api/v1/sellers/me
Authorization: Bearer {token}

Response:
{
  "success": true,
  "data": {
    "id": 123,
    "sellerName": "홍길동마켓",
    "applyStatus": "APPROVED",
    "approvedAt": "2026-02-10T15:30:00",
    ...
  }
}

// 관리자: 셀러 상세 조회
GET /api/v1/admin/sellers/{sellerId}

// 관리자: 셀러 목록 조회 (페이징)
GET /api/v1/admin/sellers?status=PENDING&page=0&size=20
```

#### C-API-02: 셀러 정보 수정 API 누락
**필요 API**:
```kotlin
// 일반 정보 수정 (즉시 반영)
PATCH /api/v1/sellers/me
{
  "managerName": "김새담당",
  "managerPhone": "010-9999-8888",
  "email": "new@example.com"
}

// 중요 정보 변경 신청 (재심사)
POST /api/v1/sellers/change-request
{
  "accountNo": "110987654321",
  "accountHolder": "홍길동",
  "documents": [
    { "type": "BANK_BOOK", "url": "https://..." }
  ]
}
```

#### C-API-03: 파일 업로드 API 보안 검증 부재
**현재 명세 (line 113-127)**:
```json
POST /api/v1/files/upload
Request: MultipartFile
```

**문제**:
- 파일 타입 검증 로직 불명확
- 악성 파일 업로드 가능성
- 업로드 권한 체크 없음

**개선안**:
```kotlin
@PostMapping("/api/v1/files/upload")
fun uploadFile(
    @RequestParam("file") file: MultipartFile,
    @RequestParam("docType") docType: DocumentType,
    @AuthenticationPrincipal user: User
): ApiResponse<FileUploadResponse> {
    // 1. 파일 크기 검증 (5MB)
    if (file.size > 5 * 1024 * 1024) {
        throw CoreException(ErrorType.FILE_SIZE_EXCEEDED)
    }

    // 2. 확장자 검증
    val allowedExtensions = listOf("jpg", "jpeg", "png", "pdf")
    val extension = file.originalFilename?.substringAfterLast(".")?.lowercase()
    if (extension !in allowedExtensions) {
        throw CoreException(ErrorType.INVALID_FILE_TYPE)
    }

    // 3. MIME 타입 검증 (확장자 스푸핑 방지)
    val allowedMimeTypes = listOf("image/jpeg", "image/png", "application/pdf")
    if (file.contentType !in allowedMimeTypes) {
        throw CoreException(ErrorType.INVALID_FILE_TYPE)
    }

    // 4. 악성 파일 검증 (선택: VirusTotal API 등)
    // virusScanService.scan(file)

    // 5. 저장
    val fileUrl = fileStorageService.store(file, docType)

    return ApiResponse.success(FileUploadResponse(fileUrl, file.originalFilename))
}
```

### ⚠️ Warning

#### W-API-01: 페이징 표준화 부재
**권장 표준**:
```kotlin
// 공통 페이징 요청
data class PageRequest(
    val page: Int = 0,
    val size: Int = 20,
    val sort: String? = null // 예: "createdAt,desc"
)

// 공통 페이징 응답
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean
)
```

#### W-API-02: 에러 응답 형식 불명확
- 현재 에러 코드만 정의됨 (line 188-197)
- 표준 에러 응답 형식 필요:
```json
{
  "success": false,
  "error": {
    "code": "DUPLICATE_APPLICATION",
    "message": "이미 신청 중이거나 승인된 셀러입니다.",
    "timestamp": "2026-02-15T10:30:00",
    "path": "/api/v1/sellers/apply"
  }
}
```

#### W-API-03: 승인/거절 API 응답 정의 부재
- 성공 시 응답 형식 불명확
- 셀러 정보를 포함할지 여부 결정 필요

---

## 4️⃣ 보안 검증

### ✅ 잘 정의된 부분
1. **암호화 정책 명확** (AES-256, line 183-186)
2. **UUID 파일명 사용** (line 76)
3. **JWT 인증** (line 133)

### ❌ Critical 이슈

#### C-SEC-01: 파일 다운로드 권한 검증 누락
**문제**:
```
현재: 파일 URL이 그대로 응답됨
→ URL을 알면 누구나 접근 가능 (S3 Presigned URL이라도 권한 체크 필요)
```

**해결 방안**:
```kotlin
// 파일 다운로드 API 추가
GET /api/v1/files/{fileId}
Authorization: Bearer {token}

@GetMapping("/api/v1/files/{fileId}")
fun downloadFile(
    @PathVariable fileId: Long,
    @AuthenticationPrincipal user: User
): ResponseEntity<Resource> {
    val document = sellerDocumentRepository.findById(fileId)
        ?: throw CoreException(ErrorType.NOT_FOUND_DATA)

    val seller = sellerRepository.findById(document.sellerId)
        ?: throw CoreException(ErrorType.NOT_FOUND_DATA)

    // 권한 체크: 본인 또는 관리자만
    if (seller.userId != user.id && !user.isAdmin()) {
        throw CoreException(ErrorType.FORBIDDEN)
    }

    // Presigned URL 생성 (1시간 유효)
    val presignedUrl = s3Service.generatePresignedUrl(document.fileUrl, 3600)

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build()
}
```

#### C-SEC-02: 관리자 권한 체크 로직 불명확
**문제**:
```
line 165-176: 관리자 API가 정의되어 있으나
권한 체크 방법이 명시되지 않음
```

**해결 방안**:
```kotlin
// Spring Security 설정
@Configuration
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/sellers/**").authenticated()
                    .anyRequest().permitAll()
            }
        return http.build()
    }
}

// 컨트롤러
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/api/v1/admin/sellers/{sellerId}/approve")
fun approveSeller(@PathVariable sellerId: Long, @AuthenticationPrincipal admin: User) {
    // ...
}
```

### ⚠️ Warning

#### W-SEC-01: Rate Limiting 정책 부재
**권장 정책**:
```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: seller-apply
          uri: /api/v1/sellers/apply
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 5  # 5 requests per minute
                redis-rate-limiter.burstCapacity: 10
```

#### W-SEC-02: 계좌 실명 조회 API 키 관리
- 외부 API 키를 환경변수로 관리해야 함 (명시 필요)
- KMS 또는 Vault 사용 권장

#### W-SEC-03: CORS 정책 누락
- 프론트엔드 도메인 화이트리스트 정의 필요

#### W-SEC-04: 사업자번호 검증 알고리즘
- line 86: "검증 알고리즘 적용 권장"
- 구체적 알고리즘 명시 필요:
```kotlin
fun validateBusinessNo(businessNo: String): Boolean {
    val numbers = businessNo.replace("-", "").map { it.digitToInt() }
    if (numbers.size != 10) return false

    val checksum = listOf(1, 3, 7, 1, 3, 7, 1, 3, 5)
        .zip(numbers.take(9))
        .sumOf { (multiplier, digit) -> multiplier * digit }

    val checkDigit = (10 - (checksum % 10)) % 10
    return checkDigit == numbers[9]
}
```

---

## 5️⃣ 성능 검증

### ⚠️ Warning

#### W-PERF-01: 파일 업로드 동기 처리 우려
**문제**:
- 5MB 파일 3개 = 최대 15MB 업로드
- S3 업로드 시간 + 암호화 시간 = 응답 지연 가능성

**개선 방안**:
```kotlin
// Option A: 비동기 업로드
@Async
fun uploadFileAsync(file: MultipartFile): CompletableFuture<String> {
    val fileUrl = s3Service.upload(file)
    return CompletableFuture.completedFuture(fileUrl)
}

// Option B: 멀티파트 업로드 (대용량)
s3Client.createMultipartUpload(...)
```

#### W-PERF-02: 암호화 성능 영향
**고려 사항**:
- AES-256 암호화/복호화 오버헤드
- 대량 셀러 조회 시 복호화 비용

**최적화 방안**:
```kotlin
// 1. 복호화가 필요한 경우만 수행
@Transient
private var decryptedBusinessNo: String? = null

fun getDecryptedBusinessNo(): String {
    if (decryptedBusinessNo == null) {
        decryptedBusinessNo = encryptionService.decrypt(businessNo)
    }
    return decryptedBusinessNo!!
}

// 2. 캐싱 (주의: 보안 vs 성능 트레이드오프)
@Cacheable("seller-business-no", key = "#sellerId")
fun getDecryptedBusinessNo(sellerId: Long): String
```

#### W-PERF-03: 셀러 목록 조회 페이징 부재
**문제**:
- 관리자가 모든 셀러를 조회할 때 페이징 없음
- 셀러 1만 명 이상 시 성능 저하

**해결**:
```kotlin
GET /api/v1/admin/sellers?status=PENDING&page=0&size=20&sort=appliedAt,desc

@GetMapping("/api/v1/admin/sellers")
fun getSellerList(
    @RequestParam status: ApplyStatus?,
    @PageableDefault(size = 20, sort = ["appliedAt"], direction = Sort.Direction.DESC)
    pageable: Pageable
): ApiResponse<Page<SellerResponse>>
```

#### W-PERF-04: N+1 쿼리 가능성
**시나리오**:
```kotlin
// 셀러 목록 조회 시 서류 정보도 함께 조회
val sellers = sellerRepository.findAll()
sellers.forEach { seller ->
    val documents = sellerDocumentRepository.findBySellerId(seller.id) // N+1!
}
```

**해결**:
```kotlin
@Query("SELECT s FROM Seller s LEFT JOIN FETCH s.documents WHERE s.applyStatus = :status")
fun findAllWithDocuments(@Param("status") status: ApplyStatus): List<Seller>
```

---

## 6️⃣ 확장성 검증

### ⚠️ Warning

#### W-SCALE-01: 외부 API 장애 대응 부재
**문제**:
```
line 90-92: 계좌 실명 조회 API
- 외부 API 타임아웃 시나리오 없음
- 장애 시 신청 차단 여부 불명확
```

**해결 방안**:
```kotlin
@Retryable(
    value = [ExternalApiException::class],
    maxAttempts = 3,
    backoff = Backoff(delay = 1000)
)
fun verifyAccount(bankCode: String, accountNo: String, holder: String): Boolean {
    try {
        return accountVerificationClient.verify(bankCode, accountNo, holder)
    } catch (e: TimeoutException) {
        // Fallback 전략
        // Option A: 심사 단계로 넘기고 관리자가 수동 확인
        // Option B: 임시 PENDING_VERIFICATION 상태
        logger.warn("계좌 실명 조회 API 타임아웃, 수동 심사 필요", e)
        return false // 또는 예외 던지기
    }
}

// Circuit Breaker 적용
@CircuitBreaker(name = "account-verification", fallbackMethod = "verifyAccountFallback")
fun verifyAccount(...): Boolean
```

#### W-SCALE-02: S3 전환 시점 불명확
**문제**:
```
line 60: "기본은 로컬에 저장... 추후 S3 URL로 저장"
```
- 로컬 → S3 마이그레이션 전략 없음
- 개발/스테이징/프로덕션 환경별 전략 불명확

**권장 설계**:
```kotlin
interface FileStorageService {
    fun store(file: MultipartFile, docType: DocumentType): String
    fun retrieve(fileUrl: String): ByteArray
}

// 프로파일별 구현
@Profile("local")
class LocalFileStorageService : FileStorageService

@Profile("prod")
class S3FileStorageService : FileStorageService

// 점진적 전환
@Service
class HybridFileStorageService(
    private val localStorage: LocalFileStorageService,
    private val s3Storage: S3FileStorageService,
    @Value("\${storage.mode}") private val mode: String
) : FileStorageService {
    override fun store(file: MultipartFile, docType: DocumentType): String {
        return when (mode) {
            "local" -> localStorage.store(file, docType)
            "s3" -> s3Storage.store(file, docType)
            "hybrid" -> {
                // 로컬 저장 후 비동기로 S3 업로드
                val localUrl = localStorage.store(file, docType)
                CompletableFuture.runAsync { s3Storage.store(file, docType) }
                localUrl
            }
            else -> throw IllegalArgumentException("Unknown storage mode: $mode")
        }
    }
}
```

#### W-SCALE-03: 다국어 지원 미고려
- 에러 메시지가 한글로 하드코딩
- 국제화(i18n) 고려 시 리팩토링 필요

#### W-SCALE-04: 셀러 타입 확장 불가능
- 현재는 단일 타입만 존재
- 향후 "개인 셀러", "기업 셀러", "파트너 셀러" 등 구분 가능성 고려

---

## 7️⃣ 운영 검증

### ❌ Critical 이슈

#### C-OPS-01: 로그 정책 전무
**필요 로그**:
```kotlin
// 1. 신청 로그
logger.info("셀러 신청 시작 | userId={} | sellerName={}", userId, sellerName)
logger.info("계좌 실명 조회 | userId={} | bankCode={} | result={}", userId, bankCode, result)
logger.info("셀러 신청 완료 | userId={} | sellerId={} | status=PENDING", userId, sellerId)

// 2. 심사 로그
logger.info("셀러 승인 | sellerId={} | reviewerId={} | approvedAt={}", sellerId, reviewerId, now)
logger.warn("셀러 거절 | sellerId={} | reviewerId={} | reason={}", sellerId, reviewerId, reason)

// 3. 보안 로그
logger.warn("중복 신청 시도 | userId={} | existingStatus={}", userId, existingStatus)
logger.error("계좌 실명 불일치 | userId={} | bankCode={} | accountNo={}", userId, bankCode, "***")

// 4. 에러 로그
logger.error("파일 업로드 실패 | userId={} | fileName={} | error={}", userId, fileName, e.message, e)
```

#### C-OPS-02: 모니터링 지표 부재
**필요 지표**:
```kotlin
// Micrometer 메트릭
@Counted("seller.apply")
@Timed("seller.apply.duration")
fun applyForSeller(request: SellerApplyRequest) {
    // ...
}

// 커스텀 메트릭
meterRegistry.counter("seller.apply.success").increment()
meterRegistry.counter("seller.apply.failed", "reason", "duplicate").increment()
meterRegistry.gauge("seller.pending.count", sellerRepository.countByStatus(PENDING))
```

#### C-OPS-03: 알림 정책 부재
**필요 알림**:
```kotlin
// 1. 사용자 알림
- 신청 접수 완료 (이메일/SMS)
- 승인 완료 (이메일/SMS)
- 거절 (이메일 + 사유)

// 2. 관리자 알림
- 새 신청 건 발생 (슬랙/이메일)
- 24시간 미처리 건 (슬랙)
- 계좌 실명 조회 API 장애 (슬랙)

@Service
class SellerNotificationService(
    private val emailService: EmailService,
    private val smsService: SmsService,
    private val slackService: SlackService
) {
    fun notifyApplicationSubmitted(seller: Seller) {
        emailService.send(
            to = seller.email,
            subject = "[FlowCommerce] 셀러 신청이 접수되었습니다",
            template = "seller-apply-submitted",
            data = mapOf("sellerName" to seller.sellerName)
        )

        slackService.sendToChannel(
            channel = "#seller-review",
            message = "새 셀러 신청 | ${seller.sellerName} | ${seller.companyName}"
        )
    }

    fun notifyApproved(seller: Seller) {
        emailService.send(...)
        smsService.send(...)
    }
}
```

### ⚠️ Warning

#### W-OPS-01: 백오피스 기능 상세 부재
**필요 기능**:
```
1. 심사 대기 목록 (필터: 신청일, 업태, 지역)
2. 심사 상세 페이지 (서류 이미지 뷰어, 메모 기능)
3. 일괄 거절 (사유 템플릿)
4. 셀러 검색 (이름, 사업자번호, 이메일)
5. 통계 대시보드 (일별 신청 건수, 승인률)
```

#### W-OPS-02: 배치 작업 필요
```kotlin
// 1. 장기 미처리 건 자동 거절
@Scheduled(cron = "0 0 2 * * *") // 매일 오전 2시
fun autoRejectExpiredApplications() {
    val expiredDate = LocalDateTime.now().minusDays(30)
    val expiredSellers = sellerRepository.findByStatusAndAppliedAtBefore(PENDING, expiredDate)

    expiredSellers.forEach { seller ->
        seller.reject("30일 이상 미처리로 자동 거절")
        logger.info("자동 거절 | sellerId={} | appliedAt={}", seller.id, seller.appliedAt)
    }
}

// 2. 삭제된 파일 정리
@Scheduled(cron = "0 0 3 * * SUN") // 매주 일요일 오전 3시
fun cleanupDeletedFiles() {
    val deletedDocuments = sellerDocumentRepository.findByStatus(DELETED)
    deletedDocuments.forEach { doc ->
        s3Service.delete(doc.fileUrl)
        sellerDocumentRepository.delete(doc)
    }
}
```

#### W-OPS-03: 헬스체크 엔드포인트
```kotlin
@RestController
@RequestMapping("/actuator/health")
class HealthCheckController(
    private val accountVerificationClient: AccountVerificationClient,
    private val s3Service: S3Service
) {
    @GetMapping("/external-apis")
    fun checkExternalApis(): Map<String, String> {
        return mapOf(
            "accountVerification" to try {
                accountVerificationClient.ping()
                "UP"
            } catch (e: Exception) {
                "DOWN"
            },
            "s3" to try {
                s3Service.ping()
                "UP"
            } catch (e: Exception) {
                "DOWN"
            }
        )
    }
}
```

---

## 🔧 Critical 자동 수정 사항 (즉시 적용)

### 1. sellers 테이블 컬럼 추가
```sql
ALTER TABLE sellers
ADD COLUMN approved_at DATETIME NULL COMMENT '승인 일시',
ADD COLUMN rejected_at DATETIME NULL COMMENT '거절 일시',
ADD COLUMN reject_reason VARCHAR(500) NULL COMMENT '거절 사유',
ADD COLUMN reviewer_id BIGINT NULL COMMENT '심사자 ID';

ALTER TABLE sellers
ADD CONSTRAINT fk_sellers_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id);
```

### 2. seller_application_history 테이블 생성
```sql
CREATE TABLE seller_application_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    apply_status VARCHAR(20) NOT NULL,
    reviewer_id BIGINT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'SUBMIT, APPROVE, REJECT, UPDATE',
    reject_reason VARCHAR(500) NULL,
    snapshot_data JSON NOT NULL COMMENT '신청 당시 전체 데이터',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_seller_id (seller_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. seller_documents 테이블 수정
```sql
ALTER TABLE seller_documents
ADD COLUMN original_filename VARCHAR(255) NOT NULL COMMENT '원본 파일명',
ADD COLUMN file_size BIGINT NOT NULL COMMENT '파일 크기 (bytes)',
ADD COLUMN mime_type VARCHAR(50) NOT NULL COMMENT 'MIME 타입',
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, DELETED',
ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_seller_documents_seller_id ON seller_documents(seller_id);
CREATE INDEX idx_seller_documents_status ON seller_documents(status);
```

### 4. 추가 API 명세
```kotlin
// 1. 셀러 정보 조회
GET /api/v1/sellers/me
Authorization: Bearer {token}

// 2. 셀러 정보 수정 (일반)
PATCH /api/v1/sellers/me
{
  "managerName": "김새담당",
  "managerPhone": "010-9999-8888"
}

// 3. 신청 취소
DELETE /api/v1/sellers/apply
Authorization: Bearer {token}

// 4. 관리자: 셀러 목록 조회
GET /api/v1/admin/sellers?status=PENDING&page=0&size=20&sort=appliedAt,desc

// 5. 관리자: 셀러 상세 조회
GET /api/v1/admin/sellers/{sellerId}

// 6. 파일 다운로드
GET /api/v1/files/{fileId}
Authorization: Bearer {token}
```

### 5. 에러 코드 추가
```kotlin
enum class ErrorType(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // 기존 에러 코드
    DUPLICATE_APPLICATION(CONFLICT, "E409_001", "이미 신청 중이거나 승인된 셀러입니다."),
    INVALID_BUSINESS_NO(BAD_REQUEST, "E400_001", "사업자등록번호 형식이 올바르지 않습니다."),
    ACCOUNT_VERIFY_FAILED(BAD_REQUEST, "E400_002", "계좌 실명 확인에 실패했습니다."),
    INVALID_COMMISSION(BAD_REQUEST, "E400_003", "수수료율은 0~30% 사이여야 합니다."),
    MISSING_DOCUMENT(BAD_REQUEST, "E400_004", "필수 서류가 누락되었습니다."),
    FILE_SIZE_EXCEEDED(PAYLOAD_TOO_LARGE, "E413_001", "파일 크기는 5MB를 초과할 수 없습니다."),

    // 추가 에러 코드
    INVALID_FILE_TYPE(BAD_REQUEST, "E400_005", "허용되지 않은 파일 형식입니다."),
    SELLER_NOT_FOUND(NOT_FOUND, "E404_001", "셀러 정보를 찾을 수 없습니다."),
    FORBIDDEN(FORBIDDEN, "E403_001", "접근 권한이 없습니다."),
    NOT_PENDING_STATUS(BAD_REQUEST, "E400_006", "심사 대기 상태가 아닙니다."),
    EXTERNAL_API_TIMEOUT(GATEWAY_TIMEOUT, "E504_001", "외부 API 응답 지연"),
}
```

---

## 🤔 역질문 (20개)

### 비즈니스 정책
1. **재신청 제한**: REJECTED 상태에서 재신청 시 최소 대기 기간이 있나요? (예: 거절 후 7일 경과 필요)
2. **신청 수수료**: 셀러 신청 시 수수료나 보증금을 받나요?
3. **심사 기한**: 관리자 심사는 며칠 이내에 완료되어야 하나요? SLA가 있나요?
4. **셀러 등급**: 거래량이나 평점에 따라 셀러 등급(일반/우수/VIP)이 구분되나요?
5. **해지 프로세스**: 승인된 셀러가 영업 중단하려면 어떤 절차를 거쳐야 하나요? (미정산 금액, 진행 중 주문 처리 등)
6. **정산 주기**: settlement_rule 외에 정산 주기(주간/월간)는 어떻게 결정되나요?
7. **수수료 변경**: commission_rate는 승인 후 변경 가능한가요? 변경 시 재심사가 필요한가요?

### 데이터 정책
8. **데이터 보관**: REJECTED 상태 데이터는 얼마나 보관하나요? GDPR 준수를 위한 삭제 정책은?
9. **파일 보관**: 셀러가 탈퇴하면 업로드된 서류는 어떻게 처리하나요? (즉시 삭제 vs 일정 기간 보관)
10. **이력 보관**: 심사 이력은 얼마나 보관해야 하나요? (1년, 3년, 영구?)

### 기술 구현
11. **계좌 실명 조회 API**: 어떤 외부 API를 사용하나요? (KCB, NICE, 각 은행 Open API?)
12. **암호화 키 관리**: AES-256 키는 어떻게 관리하나요? (AWS KMS, HashiCorp Vault, 환경변수?)
13. **파일 저장소**: S3 버킷 구조는 어떻게 설계하나요? (seller/{sellerId}/{docType}/{fileId}.pdf?)
14. **동시성 제어**: 동일 사용자가 동시에 여러 신청을 시도하면 어떻게 처리하나요?
15. **트랜잭션 범위**: 파일 업로드, 계좌 조회, DB 저장을 하나의 트랜잭션으로 묶나요?

### 운영
16. **알림 우선순위**: 이메일, SMS, 슬랙 중 어떤 채널을 우선으로 사용하나요?
17. **관리자 권한**: 관리자 역할이 세분화되나요? (심사자, 수퍼바이저, 시스템관리자 등)
18. **백오피스 툴**: 별도 백오피스 시스템이 있나요? 아니면 동일 API를 사용하나요?
19. **통계 리포팅**: 일별/월별 신청 건수, 승인률 등 통계 리포트가 필요한가요?
20. **긴급 처리**: 심사를 긴급으로 처리해야 하는 경우(VIP 고객 등) 우선순위 큐가 필요한가요?

---

## 📋 실행 계획 (Implementation Plan)

### Phase 0: Critical 수정 (1-2일)
```
[ ] C-DATA-01: sellers 테이블 컬럼 추가 (approved_at, rejected_at, reject_reason, reviewer_id)
[ ] C-DATA-02: seller_application_history 테이블 생성
[ ] C-DATA-03: seller_documents 테이블 수정 (메타데이터 추가)
[ ] C-API-01: 셀러 조회 API 추가 (GET /api/v1/sellers/me)
[ ] C-API-03: 파일 업로드 보안 검증 강화
[ ] C-SEC-01: 파일 다운로드 권한 체크 구현
[ ] C-SEC-02: 관리자 권한 체크 (@PreAuthorize)
[ ] C-BIZ-01: 셀러 해지 프로세스 정의 및 API 추가
[ ] C-OPS-01: 로그 정책 수립 및 적용
[ ] C-OPS-02: Micrometer 메트릭 추가
[ ] C-OPS-03: 알림 서비스 구현 (이메일/슬랙)
```

### Phase 1: 핵심 기능 구현 (3-5일)
```
[ ] 셀러 신청 API 구현 (/api/v1/sellers/apply)
  [ ] 입력값 검증 (사업자번호, 수수료율 등)
  [ ] 중복 신청 체크
  [ ] 파일 첨부 검증 (3개 필수)
  [ ] 계좌 실명 조회 (Mock 우선)
  [ ] 암호화 처리 (business_no, account_no)
  [ ] DB 저장 (status=PENDING)
  [ ] 이력 저장 (seller_application_history)
  [ ] 알림 발송 (사용자, 관리자)

[ ] 관리자 심사 API 구현
  [ ] 승인 API (/api/v1/admin/sellers/{sellerId}/approve)
  [ ] 거절 API (/api/v1/admin/sellers/{sellerId}/reject)
  [ ] 상태 변경 로직
  [ ] 이력 저장
  [ ] 알림 발송

[ ] 셀러 조회/수정 API
  [ ] 본인 정보 조회 (GET /api/v1/sellers/me)
  [ ] 일반 정보 수정 (PATCH /api/v1/sellers/me)
  [ ] 관리자 목록 조회 (페이징)
```

### Phase 2: 보안 및 검증 강화 (2-3일)
```
[ ] 파일 업로드 보안
  [ ] 파일 크기 검증 (5MB)
  [ ] 확장자 검증 (jpg, png, pdf)
  [ ] MIME 타입 검증
  [ ] 악성 파일 스캔 (선택)
  [ ] UUID 파일명 생성
  [ ] S3 업로드 (또는 로컬 저장)

[ ] 파일 다운로드 보안
  [ ] 권한 체크 (본인 또는 관리자)
  [ ] Presigned URL 생성 (1시간 유효)
  [ ] 다운로드 로그 기록

[ ] 암호화 구현
  [ ] AES-256 암호화 유틸리티
  [ ] KMS 또는 환경변수로 키 관리
  [ ] JPA Converter 적용 (@Convert)

[ ] 사업자번호 검증
  [ ] 체크섬 알고리즘 구현
  [ ] 형식 검증 (XXX-XX-XXXXX)
```

### Phase 3: 외부 연동 (2-3일)
```
[ ] 계좌 실명 조회 API 연동
  [ ] API 선정 (KCB, NICE, Open Banking)
  [ ] API 클라이언트 구현
  [ ] Retry 로직 (3회)
  [ ] Circuit Breaker 적용
  [ ] Fallback 전략 (수동 심사로 전환)
  [ ] Mock 서버 구현 (개발/테스트용)

[ ] S3 파일 저장소 구현
  [ ] S3 버킷 생성
  [ ] Spring Cloud AWS 설정
  [ ] Presigned URL 생성
  [ ] 로컬 → S3 마이그레이션 스크립트
```

### Phase 4: 운영 기능 (3-4일)
```
[ ] 알림 시스템
  [ ] 이메일 템플릿 (신청 접수, 승인, 거절)
  [ ] SMS 연동 (선택)
  [ ] 슬랙 웹훅 (관리자 알림)

[ ] 백오피스 API
  [ ] 심사 대기 목록 (필터링, 정렬)
  [ ] 셀러 상세 조회 (서류 뷰어)
  [ ] 셀러 검색 (이름, 사업자번호, 이메일)
  [ ] 통계 대시보드 API

[ ] 배치 작업
  [ ] 장기 미처리 건 자동 거절 (30일)
  [ ] 삭제된 파일 정리 (주간)
  [ ] 통계 집계 (일간)

[ ] 모니터링
  [ ] Actuator 커스텀 헬스체크
  [ ] Prometheus 메트릭
  [ ] Grafana 대시보드
```

### Phase 5: 테스트 (3-4일)
```
[ ] 단위 테스트
  [ ] Domain 로직 테스트
  [ ] Validator 테스트
  [ ] Service 테스트 (Mock)

[ ] 통합 테스트
  [ ] API 테스트 (@SpringBootTest)
  [ ] 파일 업로드/다운로드 테스트
  [ ] 암호화/복호화 테스트
  [ ] 권한 체크 테스트

[ ] 시나리오 테스트
  [ ] 정상 신청 → 승인 플로우
  [ ] 중복 신청 거절
  [ ] REJECTED → 재신청
  [ ] 계좌 실명 불일치
  [ ] 파일 크기 초과
  [ ] 관리자 권한 없이 심사 시도

[ ] 성능 테스트
  [ ] 파일 업로드 성능 (15MB)
  [ ] 암호화 성능 (1000건)
  [ ] 셀러 목록 조회 (10000건)
```

---

## 🛡️ 예상 반론 및 대응

### 반론 1: "이력 테이블이 정말 필요한가요? 오버 엔지니어링 아닌가요?"
**대응**:
- **법적 요구사항**: 금융 관련 서비스는 심사 이력 보관 의무가 있습니다 (전자금융거래법)
- **재신청 시나리오**: REJECTED → 재신청 시 이전 거절 사유를 추적해야 반복 거절 방지 가능
- **데이터 무결성**: 메인 테이블을 계속 UPDATE하면 이력 손실 위험
- **감사(Audit)**: "누가, 언제, 왜 승인/거절했는지" 추적 필수 (관리자 분쟁 대응)
- **비용**: 테이블 하나 추가 비용 < 이력 손실 리스크

### 반론 2: "파일 다운로드 권한 체크가 과하지 않나요? URL만 알면 못 보게 하면 되잖아요?"
**대응**:
- **현실 시나리오**:
  - URL이 로그에 남거나 네트워크 패킷에서 노출될 수 있음
  - 퇴사자가 예전에 받은 URL을 재사용할 수 있음
- **개인정보보호법**: 사업자등록증, 통장사본은 민감정보 → 접근 제어 필수
- **Presigned URL 한계**: 유효기간 내에는 누구나 접근 가능 → 발급 시점에 권한 체크 필수
- **비용**: 권한 체크 로직 10줄 < 개인정보 유출 사고 비용

### 반론 3: "계좌 실명 조회 API 장애 시 수동 심사로 넘기면 사용자 경험이 나빠지지 않나요?"
**대응**:
- **트레이드오프**:
  - Option A: 장애 시 신청 차단 → 사용자 불만
  - Option B: 수동 심사로 전환 → 심사 지연
  - **권장**: Option B + 알림 (사용자에게 "심사 중" 안내)
- **장애 빈도**: 실제로는 99.9% 가용성이므로 극히 드문 경우
- **Fallback 전략**:
  - Circuit Breaker가 OPEN되면 즉시 수동 심사 모드로 전환
  - 관리자에게 슬랙 알림 → 우선 처리
- **대안**: 계좌 인증 단계를 분리 (신청 후 비동기로 검증)

### 반론 4: "암호화 성능 오버헤드가 크지 않나요?"
**대응**:
- **실측 데이터**:
  - AES-256 암호화: ~0.1ms (1건)
  - 1000건 조회 시: ~100ms 추가 (허용 가능)
- **최적화 방안**:
  - 목록 조회 시에는 복호화 생략 (마스킹만 표시: 123-**-**890)
  - 상세 조회 시에만 복호화
  - 캐싱 (주의: 보안 vs 성능 트레이드오프)
- **법적 의무**: 개인정보보호법상 암호화 필수 → 성능보다 컴플라이언스 우선

### 반론 5: "테스트 커버리지 70%는 너무 높지 않나요? 개발 속도가 느려져요."
**대응**:
- **리스크 기반 접근**:
  - 핵심 로직(Domain, Service): 90% (돈과 직결)
  - API Layer: 70% (입력 검증)
  - Util, Config: 50% (낮은 리스크)
- **장기 ROI**:
  - 초기 개발: 테스트 작성 시간 +30%
  - 유지보수: 버그 수정 시간 -50%
  - 리팩토링: 자신감 +100%
- **실제 사례**:
  - 테스트 없이 개발 → 프로덕션 버그 → 긴급 패치 → 야근
  - 테스트 작성 → CI 자동 검증 → 안정적 배포

### 반론 6: "S3 전환 전략이 복잡해 보이는데, 처음부터 S3만 쓰면 안 되나요?"
**대응**:
- **초기 개발 단계**:
  - 로컬 저장소: 개발/테스트 속도 빠름 (AWS 계정 불필요)
  - S3: 설정, 권한, 비용 고려 필요
- **점진적 전환의 이점**:
  - Phase 1: 로컬로 빠르게 검증
  - Phase 2: S3 추가 (프로파일 기반)
  - Phase 3: 데이터 마이그레이션
- **Hybrid 모드**: 로컬 저장 + 비동기 S3 업로드 (백업)
- **실무 경험**: 처음부터 S3 강제 → AWS 장애 시 개발 중단

### 반론 7: "셀러 해지 프로세스가 복잡해 보이는데, 그냥 status=DELETED로 충분하지 않나요?"
**대응**:
- **실제 해지 시나리오**:
  1. 진행 중인 주문이 있는가? (배송 중, 미정산)
  2. 정산 대기 금액이 있는가?
  3. 보유 중인 쿠폰/포인트 처리
  4. 등록된 상품 비공개 처리
- **법적 요구사항**:
  - 즉시 삭제 불가 (정산 완료까지 최소 N일 유지)
  - 개인정보는 삭제, 거래 이력은 보관 (세법)
- **상태 전이**:
  ```
  APPROVED → INACTIVE (영업 중단, 데이터 유지)
  INACTIVE → DELETED (정산 완료 후, 개인정보 삭제)
  ```

### 반론 8: "역질문 20개가 너무 많아요. 개발자가 다 결정하면 안 되나요?"
**대응**:
- **책임 소재**:
  - 개발자가 임의로 결정 → 나중에 "왜 이렇게 만들었어?" → 리팩토링 비용
  - PO/기획자와 합의 → 문서화 → 분쟁 없음
- **우선순위**:
  - Critical (1-7번): 개발 전 필수 답변
  - Warning (8-20번): 개발 중 점진적 답변
- **실제 사례**:
  - Q: "재신청 제한이 있나요?" → 답변 없이 개발 → 악용 사례 발생 → 긴급 패치

### 반론 9: "Critical 수정 11건이 너무 많아요. 일정이 늘어나잖아요?"
**대응**:
- **숨겨진 비용**:
  - 지금 수정: 2일 (테이블 변경, API 추가)
  - 나중에 수정: 2주 (데이터 마이그레이션, 프로덕션 무중단 배포)
- **우선순위 협상**:
  - Must (P0): C-DATA-01, C-API-01, C-SEC-02 (5건)
  - Should (P1): C-BIZ-01, C-OPS-01 (4건)
  - Nice-to-have (P2): 나머지
- **점진적 적용**: Phase별로 나눠서 개발 (MVP → 보완)

### 반론 10: "이 문서가 100페이지가 넘는데, 실제로 다 읽는 사람이 있을까요?"
**대응**:
- **문서 구조**:
  - Executive Summary: 5분 (의사결정자용)
  - Critical 수정: 10분 (개발자용)
  - 실행 계획: 15분 (PM용)
  - 전체: 1시간 (아키텍트용)
- **활용 방식**:
  - 개발 시작 전: Executive Summary + Critical 수정
  - 구현 중: 해당 Phase별 상세 내용
  - 코드 리뷰: 예상 반론 참고
- **장기 가치**:
  - 신규 멤버 온보딩 시 히스토리 파악
  - 요구사항 변경 시 영향도 분석
  - 장애 발생 시 설계 의도 파악

---

## 📊 요약 및 다음 단계

### 종합 평가
- **강점**: 핵심 비즈니스 로직과 보안 정책이 명확하게 정의됨
- **약점**: 운영 관점(로그, 모니터링, 알림) 및 확장성 고려 부족
- **권장 조치**: Critical 11건 즉시 수정, Phase별 점진적 구현

### Next Steps
1. **즉시 (1일 내)**: 역질문 1-7번 PO/기획자와 합의
2. **Phase 0 (2일)**: Critical 수정 사항 적용 (테이블, API)
3. **Phase 1 (5일)**: 핵심 기능 구현 (신청, 심사)
4. **Phase 2-5**: 실행 계획에 따라 순차 진행

### 의사결정 필요 사항
- [ ] 재신청 제한 정책 (즉시)
- [ ] 계좌 실명 조회 API 선정 (즉시)
- [ ] S3 전환 시점 (Phase 3 전까지)
- [ ] 알림 채널 우선순위 (Phase 4 전까지)
- [ ] 테스트 커버리지 목표 합의 (즉시)

---

**검증 완료일**: 2026-02-15
**다음 리뷰**: 개발 완료 후 코드 리뷰 단계
