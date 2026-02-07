package com.flowcommerce.core.api.v1.product.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class StockAlertSettingRequest(
    @field:Schema(description = "재고 알림 임계값", example = "10")
    @field:Min(value = 1, message = "임계값은 1 이상이어야 합니다")
    val threshold: Int?,

    @field:Schema(description = "재고 알림 활성화 여부", example = "true")
    val enabled: Boolean?
)
