# 개발 규칙 (Development Rules)

## 언어

**Kotlin + Spring Boot**

## 기본 원칙

### 1. 심플함 우선 (Simplicity First)
- 복잡한 추상화보다 명확한 코드
- YAGNI: You Aren't Gonna Need It
- 필요할 때 리팩토링

### 2. 테스트 주도 (Test-Driven)
- 핵심 로직은 테스트 작성
- 엣지 케이스 커버
- Mock보다 실제 객체 우선

### 3. 예외 처리
- 구체적인 예외 타입
- 사용자 친화적 메시지
- 로깅 필수

### 4. 로깅
- ERROR: 즉시 대응 필요
- WARN: 주의 필요
- INFO: 중요 이벤트
- DEBUG: 개발용

### 5. 트랜잭션
- Service Layer에 `@Transactional`
- 외부 API는 트랜잭션 밖
- 읽기는 `readOnly = true`

## 코드 스타일

### 네이밍
```java
// Class: PascalCase
public class OrderService {}

// Method: camelCase (동사 시작)
public void createOrder() {}

// Variable: camelCase
String userName;

// Constant: UPPER_SNAKE_CASE
static final int MAX_RETRY = 3;
```

### 메서드 크기
- 한 메서드 = 한 가지 일
- 최대 20줄 권장
- 복잡하면 분리

### 주석
- 코드가 설명 (주석 최소화)
- WHY 설명 (WHAT 아님)
- TODO는 티켓으로

## 금지 사항

❌ 하드코딩
❌ System.out.println
❌ catch (Exception e) {}
❌ null 반환 (Optional 사용)
❌ primitive obsession

## Kotlin 코딩 스타일

### BaseEntity 패턴
```kotlin
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR")
    private var status: EntityStatus = EntityStatus.ACTIVE

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.MIN

    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.MIN

    fun active() { status = EntityStatus.ACTIVE }
    fun isActive() = status == EntityStatus.ACTIVE
    fun delete() { status = EntityStatus.DELETED }
    fun isDeleted() = status == EntityStatus.DELETED
}
```

**원칙**:
- Soft Delete (EntityStatus)
- 생성/수정 시간 자동
- 상태 변경은 메서드로

### 예외 처리
```kotlin
class CoreException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)

enum class ErrorType(
    val status: HttpStatus,
    val code: ErrorCode,
    val message: String,
    val logLevel: LogLevel
) {
    DEFAULT_ERROR(INTERNAL_SERVER_ERROR, E500, "알 수 없는 오류", ERROR),
    INVALID_REQUEST(BAD_REQUEST, E400, "요청 오류", INFO),
    NOT_FOUND_DATA(BAD_REQUEST, E401, "데이터 없음", ERROR),
}
```

**사용**:
```kotlin
val order = orderRepository.findById(id) 
    ?: throw CoreException(ErrorType.NOT_FOUND_DATA)
```

### API 응답
```kotlin
data class ApiResponse<T> private constructor(
    val result: ResultType,
    val data: T? = null,
    val error: ErrorMessage? = null,
) {
    companion object {
        fun success() = ApiResponse<Any>(SUCCESS, null, null)
        fun <S> success(data: S) = ApiResponse(SUCCESS, data, null)
        fun <S> error(error: ErrorType, errorData: Any? = null) = 
            ApiResponse<S>(ERROR, null, ErrorMessage(error, errorData))
    }
}
```

**사용**:
```kotlin
@GetMapping
fun get() = ApiResponse.success(data)

@PostMapping
fun create() = ApiResponse.success()
```

### 페이징
```kotlin
data class OffsetLimit(val offset: Int, val limit: Int) {
    fun toPageable() = PageRequest.of(offset / limit, limit)
}

data class Page<T>(val content: List<T>, val hasNext: Boolean)

data class PageResponse<T>(val content: List<T>, val hasNext: Boolean)
```

**사용**:
```kotlin
fun search(offsetLimit: OffsetLimit): Page<Product> {
    val page = repository.findAll(offsetLimit.toPageable())
    return Page(page.content, page.hasNext())
}
```

### 트랜잭션 스타일
```kotlin
@Transactional
fun processPayment(orderKey: String, amount: BigDecimal): Long {
    // 1. 조회 + 검증
    val order = orderRepository.findByOrderKey(orderKey) 
        ?: throw CoreException(ErrorType.NOT_FOUND_DATA)
    
    val payment = paymentRepository.findByOrderId(order.id) 
        ?: throw CoreException(ErrorType.NOT_FOUND_DATA)
    
    // 2. 비즈니스 검증
    if (payment.userId != order.userId) 
        throw CoreException(ErrorType.NOT_FOUND_DATA)
    if (payment.state != PaymentState.READY) 
        throw CoreException(ErrorType.PAYMENT_INVALID_STATE)
    if (payment.paidAmount != amount) 
        throw CoreException(ErrorType.PAYMENT_AMOUNT_MISMATCH)
    
    // 3. 외부 API (PG 승인)
    // NOTE: 주석으로 명확히
    
    // 4. 도메인 메서드 호출
    payment.success(externalPaymentKey, PaymentMethod.CARD, approvalNo)
    order.paid()
    
    // 5. 부가 작업
    if (payment.hasAppliedCoupon()) {
        ownedCouponRepository.findByIdOrNull(payment.ownedCouponId)?.use()
    }
    
    pointHandler.deduct(User(payment.userId), PointType.PAYMENT, payment.id, payment.usedPoint)
    
    // 6. 이력 저장
    transactionHistoryRepository.save(...)
    
    return payment.id
}
```

**원칙**:
- 검증 먼저
- 도메인 메서드로 상태 변경
- 부가 작업은 명확히 구분
- NOTE 주석 활용

### 테스트 전략

**단위 테스트** (Domain, API):
```kotlin
class OrderTest {
    @Test
    fun `주문 생성 성공`() {
        // given
        val order = Order(...)
        
        // when
        order.create()
        
        // then
        assertThat(order.state).isEqualTo(OrderState.CREATED)
    }
}
```

**통합 테스트** (핵심 비즈니스만):
```kotlin
@SpringBootTest
class PaymentIntegrationTest : TestBase() {  // 공통 클래스 상속
    @Autowired
    lateinit var paymentService: PaymentService
    
    @Test
    fun `결제 처리 성공`() {
        // given
        val order = createOrder()
        
        // when
        val paymentId = paymentService.processPayment(...)
        
        // then
        assertThat(paymentId).isNotNull()
    }
}

// 공통 클래스
@SpringBootTest
@Transactional
abstract class TestBase {
    // 공통 setup
}
```

**원칙**:
- @SpringBootTest 최소화 (느림)
- 공통 클래스 상속으로 재사용
- 핵심 비즈니스만 통합 테스트
- Domain/API는 단위 테스트