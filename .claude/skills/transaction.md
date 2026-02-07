# Transaction Patterns

## 원칙: 트랜잭션 최소화

### ❌ 안 좋음
@Transactional
public void create() {
// 외부 API 호출 (3초)
// DB 저장
}

### ✅ 좋음
public void create() {
// 외부 API (트랜잭션 밖)
saveToDb(); // 최소 트랜잭션
}

@Transactional
private void saveToDb() { }