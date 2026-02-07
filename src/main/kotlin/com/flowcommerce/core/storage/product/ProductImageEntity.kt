package com.flowcommerce.core.storage.product

import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "product_image")
class ProductImageEntity protected constructor() : BaseEntity() {

    @Column(nullable = false)
    var productId: Long = 0
        protected set

    @Column(nullable = false, length = 500)
    var imageUrl: String = ""
        protected set

    @Column(nullable = false)
    var displayOrder: Int = 0
        protected set

    @Column(nullable = false)
    var isThumbnail: Boolean = false
        protected set

    companion object {
        fun create(productId: Long, imageUrl: String, displayOrder: Int, isThumbnail: Boolean?): ProductImageEntity {
            return ProductImageEntity().apply {
                this.productId = productId
                this.imageUrl = imageUrl
                this.displayOrder = displayOrder
                this.isThumbnail = isThumbnail ?: false
            }
        }
    }
}
