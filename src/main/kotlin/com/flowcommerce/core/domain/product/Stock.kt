package com.flowcommerce.core.domain.product

data class Stock(
    val id: Long?,
    val productId: Long,
    val totalQuantity: Int,
    val availableQuantity: Int,
    val reservedQuantity: Int,
    val soldQuantity: Int,
    val alertThreshold: Int,
    val alertEnabled: Boolean
) {
    fun isLowStock(): Boolean = alertEnabled && availableQuantity <= alertThreshold

    fun isSoldOut(): Boolean = availableQuantity <= 0

    fun canReserve(quantity: Int): Boolean = availableQuantity >= quantity

    companion object {
        fun createInitial(
            productId: Long,
            initialQuantity: Int,
            alertThreshold: Int?,
            alertEnabled: Boolean?
        ): Stock = Stock(
            id = null,
            productId = productId,
            totalQuantity = initialQuantity,
            availableQuantity = initialQuantity,
            reservedQuantity = 0,
            soldQuantity = 0,
            alertThreshold = alertThreshold ?: 10,
            alertEnabled = alertEnabled ?: true
        )
    }
}
