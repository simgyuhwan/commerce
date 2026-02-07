package com.flowcommerce.core.storage.product

import com.flowcommerce.core.domain.product.ProductCategory
import com.flowcommerce.core.domain.product.ProductStatus
import com.flowcommerce.core.storage.EntityStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<ProductEntity, Long> {

    fun findBySellerIdAndStatusNot(sellerId: Long, status: EntityStatus, pageable: Pageable): Page<ProductEntity>

    fun findBySellerIdAndProductStatus(sellerId: Long, productStatus: ProductStatus): List<ProductEntity>

    @Query("SELECT p FROM ProductEntity p WHERE p.sellerId = :sellerId AND p.status = 'ACTIVE'")
    fun findActiveProductsBySeller(@Param("sellerId") sellerId: Long, pageable: Pageable): Page<ProductEntity>

    fun findByProductStatusAndStatusNot(productStatus: ProductStatus, entityStatus: EntityStatus, pageable: Pageable): Page<ProductEntity>

    @Query("SELECT p FROM ProductEntity p WHERE p.productName LIKE %:keyword% AND p.status = 'ACTIVE'")
    fun searchByName(@Param("keyword") keyword: String, pageable: Pageable): Page<ProductEntity>

    fun findByCategoryAndProductStatusAndStatusNot(
        category: ProductCategory,
        productStatus: ProductStatus,
        entityStatus: EntityStatus,
        pageable: Pageable
    ): Page<ProductEntity>

    fun existsBySellerIdAndProductNameAndStatusNot(sellerId: Long, productName: String, status: EntityStatus): Boolean

    fun countBySellerIdAndStatusNot(sellerId: Long, status: EntityStatus): Long

    @Query("SELECT p FROM ProductEntity p WHERE p.sellerId = :sellerId AND p.productStatus = 'ON_SALE' AND p.status = 'ACTIVE'")
    fun findOnSaleProductsBySeller(@Param("sellerId") sellerId: Long): List<ProductEntity>
}
