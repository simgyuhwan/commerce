package com.flowcommerce.core.domain.product

import java.math.BigDecimal

data class ProductUpdate(
    val productName: String,
    val description: String,
    val originalPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val category: ProductCategory,
    val thumbnailUrl: String?,
    val liveExclusive: Boolean,
    val liveDiscountPrice: BigDecimal?
)
