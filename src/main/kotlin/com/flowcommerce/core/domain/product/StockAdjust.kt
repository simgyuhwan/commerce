package com.flowcommerce.core.domain.product

data class StockAdjust(
    val quantity: Int,
    val newQuantity: Int,
    val reason: String
)
