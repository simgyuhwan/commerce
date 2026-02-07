package com.flowcommerce.core.storage.product

import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "stock")
class StockEntity protected constructor() : BaseEntity() {

    @Column(nullable = false, unique = true)
    var productId: Long = 0
        protected set

    @Column(nullable = false)
    var totalQuantity: Int = 0
        protected set

    @Column(nullable = false)
    var availableQuantity: Int = 0
        protected set

    @Column(nullable = false)
    var reservedQuantity: Int = 0
        protected set

    @Column(nullable = false)
    var soldQuantity: Int = 0
        protected set

    @Column(nullable = false)
    var alertThreshold: Int = 10
        protected set

    @Column(nullable = false)
    var alertEnabled: Boolean = true
        protected set

    @Version
    var version: Long? = null
        protected set

    fun increase(quantity: Int) {
        this.totalQuantity += quantity
        this.availableQuantity += quantity
    }

    fun decrease(quantity: Int) {
        check(this.availableQuantity >= quantity) { "Insufficient available stock" }
        this.totalQuantity -= quantity
        this.availableQuantity -= quantity
    }

    fun reserve(quantity: Int) {
        check(this.availableQuantity >= quantity) { "Insufficient available stock to reserve" }
        this.availableQuantity -= quantity
        this.reservedQuantity += quantity
    }

    fun releaseReservation(quantity: Int) {
        check(this.reservedQuantity >= quantity) { "Cannot release more than reserved" }
        this.reservedQuantity -= quantity
        this.availableQuantity += quantity
    }

    fun confirmSale(quantity: Int) {
        check(this.reservedQuantity >= quantity) { "Cannot confirm more than reserved" }
        this.reservedQuantity -= quantity
        this.soldQuantity += quantity
    }

    fun adjust(newTotalQuantity: Int) {
        val diff = newTotalQuantity - this.totalQuantity
        this.totalQuantity = newTotalQuantity
        this.availableQuantity = maxOf(0, this.availableQuantity + diff)
    }

    fun isLowStock(): Boolean = alertEnabled && availableQuantity <= alertThreshold

    fun isSoldOut(): Boolean = availableQuantity <= 0

    fun canReserve(quantity: Int): Boolean = availableQuantity >= quantity

    fun updateAlertSettings(threshold: Int?, enabled: Boolean?) {
        threshold?.let { this.alertThreshold = it }
        enabled?.let { this.alertEnabled = it }
    }

    companion object {
        fun createInitial(
            productId: Long,
            initialQuantity: Int,
            alertThreshold: Int?,
            alertEnabled: Boolean?
        ): StockEntity {
            return StockEntity().apply {
                this.productId = productId
                this.totalQuantity = initialQuantity
                this.availableQuantity = initialQuantity
                this.reservedQuantity = 0
                this.soldQuantity = 0
                this.alertThreshold = alertThreshold ?: 10
                this.alertEnabled = alertEnabled ?: true
            }
        }
    }
}
