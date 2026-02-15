# 테스트 규칙 (Testing Rules)

## 원칙

### 1. AAA 패턴
```java
@Test
void createOrder_success() {
    // Arrange (준비)
    User user = createUser();
    Product product = createProduct();

    // Act (실행)
    Order order = orderService.create(user, product);

    // Assert (검증)
    assertThat(order.getStatus()).isEqualTo(PENDING);
}
```

### 2. 독립성
- 테스트 간 의존 금지
- 실행 순서 무관
- 공유 상태 없음

### 3. 반복성
- 언제 실행해도 동일 결과
- 외부 의존성 Mock

## 테스트 종류

### Unit Test (70%) - Kotlin
```kotlin
class OrderServiceTest {
    private lateinit var orderService: OrderService
    private lateinit var orderRepository: OrderRepository
    
    @BeforeEach
    fun setup() {
        orderRepository = mockk()
        orderService = OrderService(orderRepository)
    }
    
    @Test
    fun `주문 생성 성공`() {
        // given
        val user = User(1L)
        val product = Product(1L, stock = 10)
        every { orderRepository.save(any()) } returns Order(...)
        
        // when
        val order = orderService.create(user, product)
        
        // then
        assertThat(order.status).isEqualTo(OrderStatus.CREATED)
        verify { orderRepository.save(any()) }
    }
}
```

### Integration Test (20%) - Kotlin
```kotlin
@SpringBootTest
@Transactional
class OrderIntegrationTest : TestBase() {  // 공통 클래스 상속
    @Autowired
    lateinit var orderService: OrderService
    
    @Autowired
    lateinit var orderRepository: OrderRepository
    
    @Test
    fun `주문 생성 및 조회 성공`() {
        // given
        val user = createUser()
        val product = createProduct()
        
        // when
        val orderId = orderService.create(user, product)
        val order = orderRepository.findById(orderId).get()
        
        // then
        assertThat(order.userId).isEqualTo(user.id)
    }
}

// 공통 테스트 베이스
@SpringBootTest
@Transactional
abstract class TestBase {
    @Autowired
    protected lateinit var entityManager: EntityManager
    
    protected fun createUser() = User(...).apply {
        entityManager.persist(this)
    }
    
    protected fun createProduct() = Product(...).apply {
        entityManager.persist(this)
    }
}
```

### E2E Test (10%)
```kotlin
@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrderE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void test() {
        // API 테스트
    }
}
```

## 커버리지 목표

- 전체: 70% 이상
- Domain: 90% 이상
- Service: 80% 이상

## 테스트 네이밍

```java
// [메서드명]_[시나리오]_[예상결과]
void createOrder_whenOutOfStock_throwsException() {}
void findById_whenNotFound_returnsEmpty() {}
void update_success() {}
```

## 엣지 케이스

필수 테스트:
- null 입력
- 빈 리스트
- 경계값 (0, -1, MAX)
- 중복
- 동시성

## Mock vs Real

### Mock 사용
- 외부 API
- 느린 작업
- 불안정한 의존성

### Real 사용
- Repository (H2)
- Domain 로직
- 간단한 의존성

## 예시

```kotlin
@Test
fun `재고 부족 시 예외 발생`() {
    // given
    val product = Product.builder()
        .stock(0)
        .build()
    
    // when & then
    assertThrows<OutOfStockException> {
        orderService.create(user, product)
    }.apply {
        assertThat(message).isEqualTo("재고가 부족합니다")
    }
}

@Test
fun `주문 생성 성공`() {
    // given
    val product = createProduct(stock = 10)
    every { orderRepository.save(any()) } returns Order(...)
    
    // when
    val order = orderService.create(user, product)
    
    // then
    assertThat(order.state).isEqualTo(OrderState.CREATED)
    verify(exactly = 1) { orderRepository.save(any()) }
}
```

## @SpringBootTest 최소화

**❌ 나쁜 예**:
```kotlin
@SpringBootTest  // 매 테스트마다 Spring Context 로드
class ProductServiceTest {
    @Test fun test1() {}
    @Test fun test2() {}
}

@SpringBootTest
class OrderServiceTest {
    @Test fun test1() {}
}
```

**✅ 좋은 예**:
```kotlin
// 단위 테스트
class ProductServiceTest {
    @Test fun test1() {}  // 빠름
    @Test fun test2() {}
}

// 통합 테스트 (공통 클래스)
@SpringBootTest
@Transactional
abstract class TestBase {
    // 공통 설정
}

// 상속받아 재사용
class OrderIntegrationTest : TestBase() {
    @Test fun test1() {}
}

class PaymentIntegrationTest : TestBase() {
    @Test fun test2() {}
}
```

## 금지 사항

❌ Thread.sleep()
❌ 실제 외부 API 호출
❌ 테스트 간 의존
❌ 랜덤 값
❌ 현재 시간 직접 사용