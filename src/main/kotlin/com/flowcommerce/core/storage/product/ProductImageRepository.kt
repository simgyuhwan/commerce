package com.flowcommerce.core.storage.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductImageRepository : JpaRepository<ProductImageEntity, Long> {
    fun findByProductIdOrderByDisplayOrder(productId: Long): List<ProductImageEntity>
    fun deleteByProductId(productId: Long)
    fun findByProductIdAndIsThumbnailTrue(productId: Long): ProductImageEntity?
}
