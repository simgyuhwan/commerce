# Performance 검증

참조: `.claude/rules/performance.md`

## 자동 검증

### N+1 쿼리
```bash
# JPA Lazy Loading
grep -rn "@OneToMany\|@ManyToOne" --include="*.java" | grep -v "fetch.*=.*EAGER\|JOIN FETCH"
```

### 인덱스 누락
```bash
# WHERE절 자주 사용되는 컬럼
grep -rn "findBy\|WHERE" --include="*Repository.java"
```

### Connection Pool
```bash
# application.yml 확인
grep -A5 "hikari:" application*.yml
```

### 캐싱 누락
```bash
# @Cacheable 사용 여부
grep -rn "@Cacheable" --include="*.java"
```

## 체크리스트

### Database
- [ ] N+1 쿼리
- [ ] JOIN FETCH 사용
- [ ] 인덱스 전략
- [ ] Connection Pool (min 10, max 50)
- [ ] Slow Query 로그

### Caching
- [ ] 정적 데이터 캐싱
- [ ] TTL 설정
- [ ] 무효화 전략

### API
- [ ] 페이징 (20건)
- [ ] 응답 시간 목표
- [ ] 타임아웃 (10초)

### Batch
- [ ] Chunk 단위 (1000건)
- [ ] 비동기 처리
- [ ] 메모리 관리

## 리포트 형식

```markdown
## 🚀 Performance (N건)

### [P-1] N+1 쿼리 발견
**위치**: `OrderService.java:45`
**문제**:
```java
List<Order> orders = orderRepository.findAll();
orders.forEach(o -> o.getItems()); // N개 쿼리
```
**영향**: 100개 주문 → 101번 쿼리
**해결**:
```java
@Query("SELECT o FROM Order o JOIN FETCH o.items")
List<Order> findAllWithItems();
```
```