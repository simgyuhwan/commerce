package com.flowcommerce.core.api.v1.product.request

import com.flowcommerce.core.domain.product.StockAdjust
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class StockAdjustRequest(
    @field:Schema(description = "수량 (increase/decrease 시 사용)", example = "10")
    @field:Min(value = 1, message = "수량은 1 이상이어야 합니다")
    val quantity: Int?,

    @field:Schema(description = "조정 후 재고 (adjust 시 사용)", example = "50")
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    val newQuantity: Int?,

    @field:Schema(description = "변경 사유", example = "입고")
    @field:Size(max = 500)
    val reason: String?
) {
    fun toStockAdjust(): StockAdjust = StockAdjust(
        quantity = quantity ?: 0,
        newQuantity = newQuantity ?: 0,
        reason = reason ?: ""
    )
}
