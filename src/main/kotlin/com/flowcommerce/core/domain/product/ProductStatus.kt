package com.flowcommerce.core.domain.product

enum class ProductStatus {
    DRAFT,      // 임시저장
    ON_SALE,    // 판매중
    SOLD_OUT,   // 품절
    HIDDEN      // 숨김처리
}
