package com.flowcommerce.core.api.v1.product.response

import com.flowcommerce.core.domain.product.ProductCategory
import com.flowcommerce.core.domain.product.ProductStatus
import com.flowcommerce.core.storage.product.ProductEntity
import com.flowcommerce.core.storage.product.ProductImageEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDetailResponse(
    @field:Schema(description = "상품 ID")
    val id: Long,

    @field:Schema(description = "셀러 ID")
    val sellerId: Long,

    @field:Schema(description = "상품명")
    val productName: String,

    @field:Schema(description = "상품 설명")
    val description: String?,

    @field:Schema(description = "정가")
    val originalPrice: BigDecimal,

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

    @field:Schema(description = "라이브 특가")
    val liveDiscountPrice: BigDecimal?,

    @field:Schema(description = "상품 이미지 목록")
    val images: List<ProductImageResponse>,

    @field:Schema(description = "생성일시")
    val createdAt: LocalDateTime?,

    @field:Schema(description = "수정일시")
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(entity: ProductEntity, images: List<ProductImageEntity>): ProductDetailResponse =
            ProductDetailResponse(
                id = entity.id,
                sellerId = entity.sellerId,
                productName = entity.productName,
                description = entity.description,
                originalPrice = entity.originalPrice,
                sellingPrice = entity.sellingPrice,
                discountRate = entity.discountRate,
                category = entity.category,
                status = entity.productStatus,
                thumbnailUrl = entity.thumbnailUrl,
                liveExclusive = entity.liveExclusive,
                liveDiscountPrice = entity.liveDiscountPrice,
                images = images.map { ProductImageResponse.from(it) },
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
    }

    data class ProductImageResponse(
        val id: Long,
        val imageUrl: String,
        val displayOrder: Int,
        val isThumbnail: Boolean
    ) {
        companion object {
            fun from(entity: ProductImageEntity): ProductImageResponse = ProductImageResponse(
                id = entity.id,
                imageUrl = entity.imageUrl,
                displayOrder = entity.displayOrder,
                isThumbnail = entity.isThumbnail
            )
        }
    }
}
