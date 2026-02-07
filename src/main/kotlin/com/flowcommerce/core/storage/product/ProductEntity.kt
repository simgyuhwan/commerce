package com.flowcommerce.core.storage.product

import com.flowcommerce.core.domain.product.ProductCategory
import com.flowcommerce.core.domain.product.ProductStatus
import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "product")
class ProductEntity protected constructor() : BaseEntity() {

    @Column(nullable = false)
    var sellerId: Long = 0
        protected set

    @Column(nullable = false, length = 200)
    var productName: String = ""
        protected set

    @Column(columnDefinition = "TEXT")
    var description: String? = null
        protected set

    @Column(nullable = false, precision = 12, scale = 2)
    var originalPrice: BigDecimal = BigDecimal.ZERO
        protected set

    @Column(nullable = false, precision = 12, scale = 2)
    var sellingPrice: BigDecimal = BigDecimal.ZERO
        protected set

    @Column(precision = 5, scale = 2)
    var discountRate: BigDecimal? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var category: ProductCategory = ProductCategory.ETC
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var productStatus: ProductStatus = ProductStatus.DRAFT
        protected set

    @Column(length = 500)
    var thumbnailUrl: String? = null
        protected set

    @Column(nullable = false)
    var liveExclusive: Boolean = false
        protected set

    @Column(precision = 12, scale = 2)
    var liveDiscountPrice: BigDecimal? = null
        protected set

    fun update(
        productName: String,
        description: String?,
        originalPrice: BigDecimal,
        sellingPrice: BigDecimal,
        discountRate: BigDecimal?,
        category: ProductCategory,
        thumbnailUrl: String?,
        liveExclusive: Boolean?,
        liveDiscountPrice: BigDecimal?
    ) {
        this.productName = productName
        this.description = description
        this.originalPrice = originalPrice
        this.sellingPrice = sellingPrice
        this.discountRate = discountRate
        this.category = category
        this.thumbnailUrl = thumbnailUrl
        this.liveExclusive = liveExclusive ?: false
        this.liveDiscountPrice = liveDiscountPrice
    }

    fun updateStatus(newStatus: ProductStatus) {
        this.productStatus = newStatus
    }

    fun startSale() {
        this.productStatus = ProductStatus.ON_SALE
    }

    fun markSoldOut() {
        this.productStatus = ProductStatus.SOLD_OUT
    }

    fun hide() {
        this.productStatus = ProductStatus.HIDDEN
    }

    companion object {
        fun create(
            sellerId: Long,
            productName: String,
            description: String?,
            originalPrice: BigDecimal,
            sellingPrice: BigDecimal,
            discountRate: BigDecimal?,
            category: ProductCategory,
            status: ProductStatus,
            thumbnailUrl: String?,
            liveExclusive: Boolean?,
            liveDiscountPrice: BigDecimal?
        ): ProductEntity {
            return ProductEntity().apply {
                this.sellerId = sellerId
                this.productName = productName
                this.description = description
                this.originalPrice = originalPrice
                this.sellingPrice = sellingPrice
                this.discountRate = discountRate
                this.category = category
                this.productStatus = status
                this.thumbnailUrl = thumbnailUrl
                this.liveExclusive = liveExclusive ?: false
                this.liveDiscountPrice = liveDiscountPrice
            }
        }
    }
}
