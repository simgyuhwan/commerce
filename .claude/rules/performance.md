# 성능 규칙 (Performance Rules)

## 데이터베이스

### N+1 쿼리 방지
```java
// ✅ Good - JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items")
List<Order> findAllWithItems();

// ❌ Bad - N+1
List<Order> orders = orderRepository.findAll();
orders.forEach(o -> o.getItems()); // N개 쿼리
```

### 인덱스 전략
```sql
-- 자주 조회되는 컬럼
CREATE INDEX idx_user_email ON users(email);

-- 복합 인덱스
CREATE INDEX idx_order_user_created ON orders(user_id, created_at);
```

### Connection Pool
```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
```

### 페이징
```java
// 20건씩
Pageable pageable = PageRequest.of(page, 20);
```

## 캐싱

### 전략
```java
// 상품 목록 (5분)
@Cacheable(value = "products", key = "#categoryId")
public List<Product> findByCategory(Long categoryId) {}

// 무효화
@CacheEvict(value = "products", allEntries = true)
public void updateProduct(Product product) {}
```

### TTL
- 정적 데이터: 1시간
- 동적 데이터: 5분
- 세션: 30분

## 비동기 처리

### 적용 대상
- 이메일 발송
- 파일 처리
- 외부 API 호출 (결과 불필요)

```java
@Async
public CompletableFuture<Void> sendEmail(String to) {}
```

## 응답 시간 목표

- API 평균: 200ms
- P95: 500ms
- P99: 1000ms
- Timeout: 10초

## 대용량 처리

### Batch
```java
// 1000건씩
@Transactional
public void processBatch(List<Order> orders) {
    for (int i = 0; i < orders.size(); i += 1000) {
        // 처리
        entityManager.flush();
        entityManager.clear();
    }
}
```

## 금지 사항

❌ SELECT *
❌ findAll() 무제한
❌ Lazy Loading in Loop
❌ 트랜잭션 내 외부 API
❌ 동기 처리 (대량 작업)