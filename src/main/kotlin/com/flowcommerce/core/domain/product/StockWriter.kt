package com.flowcommerce.core.domain.product

import com.flowcommerce.core.storage.product.StockEntity
import com.flowcommerce.core.storage.product.StockHistoryEntity
import com.flowcommerce.core.storage.product.StockHistoryRepository
import com.flowcommerce.core.storage.product.StockRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StockWriter(
    private val stockRepository: StockRepository,
    private val stockHistoryRepository: StockHistoryRepository
) {
    @Transactional
    fun createInitial(
        productId: Long,
        initialQuantity: Int,
        alertThreshold: Int?,
        alertEnabled: Boolean?,
        createdBy: Long
    ) {
        val stock = stockRepository.save(
            StockEntity.createInitial(productId, initialQuantity, alertThreshold, alertEnabled)
        )

        stockHistoryRepository.save(
            StockHistoryEntity.create(
                stock.id,
                productId,
                StockChangeType.INITIAL,
                0,
                initialQuantity,
                initialQuantity,
                "초기 재고 등록",
                createdBy,
                null,
                "MANUAL"
            )
        )
    }

    @Transactional
    fun increase(stock: StockEntity, quantity: Int, reason: String, processedBy: Long) {
        val previousQuantity = stock.availableQuantity
        stock.increase(quantity)
        stockRepository.save(stock)

        stockHistoryRepository.save(
            StockHistoryEntity.create(
                stock.id,
                stock.productId,
                StockChangeType.INCREASE,
                previousQuantity,
                quantity,
                stock.availableQuantity,
                reason,
                processedBy,
                null,
                "MANUAL"
            )
        )
    }

    @Transactional
    fun decrease(stock: StockEntity, quantity: Int, reason: String, processedBy: Long) {
        val previousQuantity = stock.availableQuantity
        stock.decrease(quantity)
        stockRepository.save(stock)

        stockHistoryRepository.save(
            StockHistoryEntity.create(
                stock.id,
                stock.productId,
                StockChangeType.DECREASE,
                previousQuantity,
                -quantity,
                stock.availableQuantity,
                reason,
                processedBy,
                null,
                "MANUAL"
            )
        )
    }

    @Transactional
    fun adjust(stock: StockEntity, newQuantity: Int, reason: String, processedBy: Long) {
        val previousQuantity = stock.totalQuantity
        val changeAmount = newQuantity - previousQuantity
        stock.adjust(newQuantity)
        stockRepository.save(stock)

        stockHistoryRepository.save(
            StockHistoryEntity.create(
                stock.id,
                stock.productId,
                StockChangeType.ADJUSTMENT,
                previousQuantity,
                changeAmount,
                newQuantity,
                reason,
                processedBy,
                null,
                "MANUAL"
            )
        )
    }
}
