package com.flowcommerce.core.api.v1.product.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class StockReserveRequest(
    @field:Schema(description = "예약 수량", example = "5")
    @field:NotNull(message = "수량은 필수입니다")
    @field:Min(value = 1, message = "수량은 1 이상이어야 합니다")
    val quantity: Int,

    @field:Schema(description = "라이브 ID", example = "1")
    @field:NotNull(message = "라이브 ID는 필수입니다")
    val liveId: Long
)
