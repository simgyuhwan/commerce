package com.flowcommerce.core.domain.product

import java.math.BigDecimal

data class ProductCreate(
    val productName: String,
    val description: String,
    val originalPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val category: ProductCategory,
    val thumbnailUrl: String?,
    val liveExclusive: Boolean,
    val liveDiscountPrice: BigDecimal?,
    val initialStock: Int,
    val alertThreshold: Int,
    val alertEnabled: Boolean
)
