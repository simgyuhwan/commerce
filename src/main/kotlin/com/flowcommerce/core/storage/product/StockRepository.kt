package com.flowcommerce.core.storage.product

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface StockRepository : JpaRepository<StockEntity, Long> {

    fun findByProductId(productId: Long): Optional<StockEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockEntity s WHERE s.productId = :productId")
    fun findByProductIdWithLock(@Param("productId") productId: Long): Optional<StockEntity>

    @Query("SELECT s FROM StockEntity s WHERE s.alertEnabled = true AND s.availableQuantity <= s.alertThreshold AND s.status = 'ACTIVE'")
    fun findLowStockProducts(): List<StockEntity>

    @Query("SELECT s FROM StockEntity s WHERE s.availableQuantity <= 0 AND s.status = 'ACTIVE'")
    fun findSoldOutProducts(): List<StockEntity>

    fun findByProductIdIn(productIds: List<Long>): List<StockEntity>
}
