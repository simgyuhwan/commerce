package com.flowcommerce.core.api.v1.product.response

import com.flowcommerce.core.storage.product.StockEntity
import io.swagger.v3.oas.annotations.media.Schema

data class StockResponse(
    @field:Schema(description = "상품 ID")
    val productId: Long,

    @field:Schema(description = "총 재고")
    val totalQuantity: Int,

    @field:Schema(description = "판매 가능 재고")
    val availableQuantity: Int,

    @field:Schema(description = "예약된 재고")
    val reservedQuantity: Int,

    @field:Schema(description = "판매된 수량")
    val soldQuantity: Int,

    @field:Schema(description = "재고 알림 임계값")
    val alertThreshold: Int,

    @field:Schema(description = "재고 알림 활성화 여부")
    val alertEnabled: Boolean,

    @field:Schema(description = "재고 부족 여부")
    val isLowStock: Boolean,

    @field:Schema(description = "품절 여부")
    val isSoldOut: Boolean
) {
    companion object {
        fun from(entity: StockEntity): StockResponse = StockResponse(
            productId = entity.productId,
            totalQuantity = entity.totalQuantity,
            availableQuantity = entity.availableQuantity,
            reservedQuantity = entity.reservedQuantity,
            soldQuantity = entity.soldQuantity,
            alertThreshold = entity.alertThreshold,
            alertEnabled = entity.alertEnabled,
            isLowStock = entity.isLowStock(),
            isSoldOut = entity.isSoldOut()
        )
    }
}
