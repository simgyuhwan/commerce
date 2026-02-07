package com.flowcommerce.core.domain.product

enum class StockChangeType {
    INITIAL,        // 초기 등록
    INCREASE,       // 입고/추가
    DECREASE,       // 수동 차감
    ADJUSTMENT,     // 재고 조정 (실사 후)
    RESERVED,       // 라이브 예약
    RELEASED,       // 예약 해제
    SOLD,           // 판매 확정
    CANCELLED,      // 주문 취소로 인한 복원
    RETURN          // 반품으로 인한 복원
}
