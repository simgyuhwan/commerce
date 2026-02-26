# LiveCommerce Backend — Claude Context

## 프로젝트 개요
- 라이브 커머스 플랫폼 (방송 중 실시간 상품 판매)
- 개발자: 1인 (Gyu)
- Spring Boot 3.4.3 / Java 21 / Gradle 9.3.1
- 언어: Kotlin (신규 코드), Java (진입점)

## 모듈 구성 (settings.gradle)
```
backend/
├── app/          → 실행 모듈 (bootJar, LiveCommerceApplication.java)
├── common/       → 공통 (JWT, 예외처리, RabbitMQ, Outbox)
├── member/       → 회원 도메인
├── commerce/     → 상품 / 재고 도메인
├── broadcast/    → 방송 도메인 (RTMP/HLS, WebSocket)
├── order/        → 주문 도메인
├── payment/      → 결제 도메인 (TossPayments)
└── settlement/   → 정산 도메인
```

## 의존성 규칙
- `common` → 다른 모듈 의존 안 함
- 각 도메인 → `common`만 의존 가능
- 도메인 간 직접 의존 금지 → RabbitMQ + Outbox 패턴으로 통신
- `app` → 모든 모듈 의존 (실행 진입점)

## common 모듈 구조
```
common/src/main/java/com/livecommerce/common/
├── messaging/
│   ├── Outbox.kt              → JPA 엔티티
│   ├── OutboxPublisher.kt     → @Scheduled 1초 주기 발행
│   ├── OutboxRepository.kt    → findTop100ByStatusOrderByCreatedAtAsc
│   ├── OutboxStatus.kt        → PENDING / PUBLISHED
│   ├── RabbitConstants.kt     → object Queues / object RoutingKeys
│   └── RabbitMQConfig.kt      → Exchange, Queue, Binding, RabbitTemplate
├── security/
│   ├── JwtAuthenticationFilter.kt
│   ├── JwtProperties.kt
│   ├── JwtProvider.kt         → generateToken / validateToken / getAuthentication
│   ├── SecurityConfig.kt      → STATELESS, /api/v1/auth/** permitAll
│   └── UserRole.kt            → ADMIN / SELLER / BUYER
└── support/
    ├── error/
    │   ├── BusinessException.kt
    │   ├── ErrorCode.kt        → E400~E500, M001~, C001~, B001~, O001~, P001~
    │   ├── ErrorMessage.kt
    │   ├── ErrorType.kt
    │   └── GlobalExceptionHandler.kt
    └── response/
        ├── ApiResponse.kt
        └── ResultType.kt
```

## RabbitMQ 이벤트 카탈로그
Exchange: `livecommerce.events` (Topic)

| RoutingKey         | Queue (Consumer)                                          |
|--------------------|-----------------------------------------------------------|
| order.created      | order.created.commerce, order.created.payment             |
| payment.completed  | payment.completed.order, payment.completed.settlement     |
| payment.failed     | payment.failed.order, payment.failed.commerce             |
| order.cancelled    | order.cancelled.commerce, order.cancelled.payment         |
| broadcast.ended    | broadcast.ended.settlement                               |

## DB 스키마
- 마이그레이션: `app/src/main/resources/db/migration/V1__init_schema.sql`
- MariaDB 11.4 / Flyway / ddl-auto: validate
- 14개 테이블: member, member_document, product, stock, product_image,
  broadcast, broadcast_product, chat_message, orders, order_item,
  payment, payment_idempotency, settlement, settlement_item, withdrawal, outbox

## 에러 응답 형식
```json
{"code": "ORDER_001", "message": "재고가 부족합니다.", "data": null}
```
- 성공: code = "SUCCESS"
- 실패: code = "DOMAIN_NNN"

## 환경 설정
- 프로파일: local / prod
- 시크릿: .env → OS 환경변수 → yml `${VAR}` 참조
- .env 위치: E:\FlowCommerce\commerce\.env (gitignore 처리됨)

## 인프라 (infra/docker-compose.yml)
| 서비스      | 포트            | 용도                    |
|-------------|-----------------|-------------------------|
| Redis       | 6379            | Cache / Pub-Sub         |
| RabbitMQ    | 5672 / 15672    | MQ / 관리콘솔           |
| Prometheus  | 9090            | 메트릭 수집             |
| Loki        | 3100            | 로그 수집               |
| Tempo       | 3200 / 4317     | 분산 트레이싱           |
| Grafana     | 3000            | 대시보드                |
| Alertmanager| 9093            | Discord 알림            |

## 로그 규칙
- ERROR: 즉시 대응 필요 (알림 대상) — log.error("메시지 [id=$id]", e)
- WARN: 이상하지만 동작함
- INFO: 비즈니스 주요 흐름 — log.info("주문 생성 [orderId=$id, amount=$amount]")
- DEBUG: 개발 중 상세 추적
- 식별자(id, orderId 등) + throwable 항상 포함

## Sprint 현황
- [x] Sprint 1: 기반 인프라
  - [x] Gradle 멀티모듈
  - [x] 공통 예외처리 / 에러 응답
  - [x] JWT 인증 필터 (RBAC)
  - [x] Flyway + DDL
  - [x] RabbitMQ Exchange / Queue 설정
  - [x] Outbox 엔티티 + OutboxPublisher
  - [ ] 모니터링 Spring 연동 (Micrometer, Loki, OTel)
  - [ ] 스파이크: WebSocket POC
  - [ ] 스파이크: RTMP 미디어 서버 POC
  - [ ] 스파이크: 토스페이먼츠 계정
- [ ] Sprint 2: Member + Commerce
- [ ] Sprint 3: Broadcast
- [ ] Sprint 4: Order + Payment
- [ ] Sprint 5: Settlement
- [ ] Sprint 6: 통합 + 배포

## 노션
- 허브: https://www.notion.so/30d238cdca6f81f1b734ffc40d689176
- Sprint Backlog: https://www.notion.so/30e238cdca6f81cabe08e2f696f20b00
