package com.flowcommerce.core.storage.product

import com.flowcommerce.core.domain.product.StockChangeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface StockHistoryRepository : JpaRepository<StockHistoryEntity, Long> {

    fun findByProductIdOrderByCreatedAtDesc(productId: Long, pageable: Pageable): Page<StockHistoryEntity>

    fun findByStockIdOrderByCreatedAtDesc(stockId: Long, pageable: Pageable): Page<StockHistoryEntity>

    fun findByProductIdAndChangeType(productId: Long, changeType: StockChangeType): List<StockHistoryEntity>

    @Query("SELECT sh FROM StockHistoryEntity sh WHERE sh.productId = :productId AND sh.createdAt BETWEEN :startDate AND :endDate ORDER BY sh.createdAt DESC")
    fun findByProductIdAndDateRange(
        @Param("productId") productId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<StockHistoryEntity>

    fun findByReferenceIdAndReferenceType(referenceId: Long, referenceType: String): List<StockHistoryEntity>
}
