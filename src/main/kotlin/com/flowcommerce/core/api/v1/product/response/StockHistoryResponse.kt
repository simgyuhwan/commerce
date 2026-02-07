package com.flowcommerce.core.api.v1.product.response

import com.flowcommerce.core.domain.product.StockChangeType
import com.flowcommerce.core.storage.product.StockHistoryEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class StockHistoryResponse(
    @field:Schema(description = "이력 ID")
    val id: Long,

    @field:Schema(description = "상품 ID")
    val productId: Long,

    @field:Schema(description = "변경 유형")
    val changeType: StockChangeType,

    @field:Schema(description = "변경 전 수량")
    val previousQuantity: Int,

    @field:Schema(description = "변경 수량")
    val changeQuantity: Int,

    @field:Schema(description = "변경 후 수량")
    val afterQuantity: Int,

    @field:Schema(description = "변경 사유")
    val reason: String?,

    @field:Schema(description = "처리자 ID")
    val processedBy: Long?,

    @field:Schema(description = "참조 ID")
    val referenceId: Long?,

    @field:Schema(description = "참조 유형")
    val referenceType: String?,

    @field:Schema(description = "생성일시")
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(entity: StockHistoryEntity): StockHistoryResponse = StockHistoryResponse(
            id = entity.id,
            productId = entity.productId,
            changeType = entity.changeType,
            previousQuantity = entity.previousQuantity,
            changeQuantity = entity.changeQuantity,
            afterQuantity = entity.afterQuantity,
            reason = entity.reason,
            processedBy = entity.processedBy,
            referenceId = entity.referenceId,
            referenceType = entity.referenceType,
            createdAt = entity.createdAt
        )
    }
}
