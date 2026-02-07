package com.flowcommerce.core.domain.product

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

data class Product(
    val id: Long?,
    val sellerId: Long,
    val productName: String,
    val description: String,
    val originalPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val discountRate: BigDecimal,
    val category: ProductCategory,
    val status: ProductStatus,
    val thumbnailUrl: String?,
    val liveExclusive: Boolean,
    val liveDiscountPrice: BigDecimal?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun create(sellerId: Long, command: ProductCreate): Product =
            Product(
                id = null,
                sellerId = sellerId,
                productName = command.productName,
                description = command.description,
                originalPrice = command.originalPrice,
                sellingPrice = command.sellingPrice,
                discountRate = calculateDiscountRate(command.originalPrice, command.sellingPrice),
                category = command.category,
                status = ProductStatus.DRAFT,
                thumbnailUrl = command.thumbnailUrl,
                liveExclusive = command.liveExclusive,
                liveDiscountPrice = command.liveDiscountPrice,
                createdAt = null,
                updatedAt = null
            )

        private fun calculateDiscountRate(original: BigDecimal, selling: BigDecimal): BigDecimal {
            if (original.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO
            }
            return original.subtract(selling)
                .divide(original, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
        }
    }
}
