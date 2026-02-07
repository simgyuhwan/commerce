package com.flowcommerce.core.storage.product

import com.flowcommerce.core.domain.product.StockChangeType
import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "stock_history",
    indexes = [
        Index(name = "idx_stock_history_product", columnList = "productId"),
        Index(name = "idx_stock_history_stock", columnList = "stockId")
    ]
)
class StockHistoryEntity protected constructor() : BaseEntity() {

    @Column(nullable = false)
    var stockId: Long = 0
        protected set

    @Column(nullable = false)
    var productId: Long = 0
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var changeType: StockChangeType = StockChangeType.INITIAL
        protected set

    @Column(nullable = false)
    var previousQuantity: Int = 0
        protected set

    @Column(nullable = false)
    var changeQuantity: Int = 0
        protected set

    @Column(nullable = false)
    var afterQuantity: Int = 0
        protected set

    @Column(length = 500)
    var reason: String? = null
        protected set

    var processedBy: Long? = null
        protected set

    var referenceId: Long? = null
        protected set

    @Column(length = 50)
    var referenceType: String? = null
        protected set

    companion object {
        fun create(
            stockId: Long,
            productId: Long,
            changeType: StockChangeType,
            previousQuantity: Int,
            changeQuantity: Int,
            afterQuantity: Int,
            reason: String?,
            processedBy: Long?,
            referenceId: Long?,
            referenceType: String?
        ): StockHistoryEntity {
            return StockHistoryEntity().apply {
                this.stockId = stockId
                this.productId = productId
                this.changeType = changeType
                this.previousQuantity = previousQuantity
                this.changeQuantity = changeQuantity
                this.afterQuantity = afterQuantity
                this.reason = reason
                this.processedBy = processedBy
                this.referenceId = referenceId
                this.referenceType = referenceType
            }
        }
    }
}
