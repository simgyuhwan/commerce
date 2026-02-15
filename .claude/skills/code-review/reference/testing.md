# Testing 검증

참조: `.claude/rules/testing.md`

## 자동 검증

### Test 파일 존재
```bash
# Test 파일 vs 소스 파일 비율
SRC=$(find src/main -name "*.java" | wc -l)
TEST=$(find src/test -name "*Test.java" | wc -l)
echo "Test Coverage: $((TEST * 100 / SRC))%"
```

### 테스트 네이밍
```bash
# 잘못된 네이밍
grep -rn "test1\|test2\|testA" --include="*Test.java"
```

### Mock 과다 사용
```bash
grep -rn "@Mock" --include="*Test.java" | wc -l
```

## 체크리스트

### 커버리지
- [ ] 전체 70% 이상
- [ ] Domain 90% 이상
- [ ] Service 80% 이상

### 테스트 품질
- [ ] AAA 패턴
- [ ] 의미있는 네이밍
- [ ] 독립성
- [ ] 엣지 케이스

### Mock vs Real
- [ ] 외부 API: Mock
- [ ] Repository: Real (H2)
- [ ] Domain: Real

## 리포트 형식

```markdown
## ✅ Testing (N건)

### [T-1] 엣지 케이스 미흡
**위치**: `OrderServiceTest.java`
**문제**: 정상 케이스만 테스트
**누락 케이스**:
- null 입력
- 재고 0
- 중복 주문

**추가 필요**:
```java
@Test
void createOrder_whenOutOfStock_throwsException() {
    // 재고 0인 상품 주문 시도
    assertThatThrownBy(() -> 
        orderService.create(user, outOfStockProduct)
    ).isInstanceOf(OutOfStockException.class);
}
```
```