package com.flowcommerce.core.api.v1.product.response

import com.flowcommerce.core.domain.product.ProductCategory
import com.flowcommerce.core.domain.product.ProductStatus
import com.flowcommerce.core.storage.product.ProductEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductResponse(
    @field:Schema(description = "상품 ID")
    val id: Long,

    @field:Schema(description = "상품명")
    val productName: String,

    @field:Schema(description = "판매가")
    val sellingPrice: BigDecimal,

    @field:Schema(description = "할인율 (%)")
    val discountRate: BigDecimal?,

    @field:Schema(description = "카테고리")
    val category: ProductCategory,

    @field:Schema(description = "상품 상태")
    val status: ProductStatus,

    @field:Schema(description = "썸네일 URL")
    val thumbnailUrl: String?,

    @field:Schema(description = "라이브 전용 여부")
    val liveExclusive: Boolean,

    @field:Schema(description = "생성일시")
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(entity: ProductEntity): ProductResponse = ProductResponse(
            id = entity.id,
            productName = entity.productName,
            sellingPrice = entity.sellingPrice,
            discountRate = entity.discountRate,
            category = entity.category,
            status = entity.productStatus,
            thumbnailUrl = entity.thumbnailUrl,
            liveExclusive = entity.liveExclusive,
            createdAt = entity.createdAt
        )
    }
}
