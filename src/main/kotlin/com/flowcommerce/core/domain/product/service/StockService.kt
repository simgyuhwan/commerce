package com.flowcommerce.core.domain.product.service

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.product.StockAdjust
import com.flowcommerce.core.domain.product.StockChangeType
import com.flowcommerce.core.domain.product.StockValidator
import com.flowcommerce.core.domain.product.StockWriter
import com.flowcommerce.core.storage.product.StockEntity
import com.flowcommerce.core.storage.product.StockHistoryEntity
import com.flowcommerce.core.storage.product.StockHistoryRepository
import com.flowcommerce.core.storage.product.StockRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockValidator: StockValidator,
    private val stockWriter: StockWriter,
    private val stockRepository: StockRepository,
    private val stockHistoryRepository: StockHistoryRepository
) {
    @Transactional
    fun createInitialStock(
        productId: Long,
        initialQuantity: Int,
        alertThreshold: Int?,
        alertEnabled: Boolean?,
        createdBy: Long
    ) {
        stockWriter.createInitial(productId, initialQuantity, alertThreshold, alertEnabled, createdBy)
    }

    @Transactional
    fun increaseStock(user: User, productId: Long, adjust: StockAdjust) {
        val stock = findStockOrThrow(productId)
        stockValidator.validateIncrease(user, stock, adjust)
        stockWriter.increase(stock, adjust.quantity, adjust.reason, user.id)
    }

    @Transactional
    fun decreaseStock(user: User, productId: Long, adjust: StockAdjust) {
        val stock = findStockOrThrow(productId)
        stockValidator.validateDecrease(user, stock, adjust)
        stockWriter.decrease(stock, adjust.quantity, adjust.reason, user.id)
    }

    @Transactional
    fun adjustStock(user: User, productId: Long, adjust: StockAdjust) {
        val stock = findStockOrThrow(productId)
        stockValidator.validateAdjustment(user, stock, adjust)
        stockWriter.adjust(stock, adjust.newQuantity, adjust.reason, user.id)
    }

    @Transactional
    fun reserveStock(productId: Long, quantity: Int, liveId: Long): Boolean {
        val stock = stockRepository.findByProductIdWithLock(productId)
            .orElseThrow { CoreException(ErrorType.STOCK_NOT_FOUND) }

        if (!stock.canReserve(quantity)) {
            return false
        }

        val previousQuantity = stock.availableQuantity
        stock.reserve(quantity)
        stockRepository.save(stock)

        stockHistoryRepository.save(
            StockHistoryEntity.create(
                stock.id,
                productId,
                StockChangeType.RESERVED,
                previousQuantity,
                -quantity,
                stock.availableQuantity,
                "라이브 예약",
                null,
                liveId,
                "LIVE"
            )
        )

        return true
    }

    @Transactional
    fun releaseReservation(productId: Long, quantity: Int, referenceId: Long, referenceType: String) {
        val stock = stockRepository.findByProductIdWithLock(productId)
            .orElseThrow { CoreException(ErrorType.STOCK_NOT_FOUND) }

        val previousQuantity = stock.availableQuantity
        stock.releaseReservation(quantity)
        stockRepository.save(stock)

        stockHistoryRepository.save(
            StockHistoryEntity.create(
                stock.id,
                productId,
                StockChangeType.RELEASED,
                previousQuantity,
                quantity,
                stock.availableQuantity,
                "예약 해제",
                null,
                referenceId,
                referenceType
            )
        )
    }

    @Transactional
    fun confirmSale(productId: Long, quantity: Int, orderId: Long) {
        val stock = stockRepository.findByProductIdWithLock(productId)
            .orElseThrow { CoreException(ErrorType.STOCK_NOT_FOUND) }

        val previousSold = stock.soldQuantity
        stock.confirmSale(quantity)
        stockRepository.save(stock)

        stockHistoryRepository.save(
            StockHistoryEntity.create(
                stock.id,
                productId,
                StockChangeType.SOLD,
                previousSold,
                quantity,
                stock.soldQuantity,
                "판매 확정",
                null,
                orderId,
                "ORDER"
            )
        )
    }

    @Transactional
    fun updateAlertSettings(user: User, productId: Long, threshold: Int?, enabled: Boolean?) {
        val stock = findStockOrThrow(productId)
        stockValidator.validateAlertUpdate(user, stock)
        stock.updateAlertSettings(threshold, enabled)
        stockRepository.save(stock)
    }

    @Transactional(readOnly = true)
    fun getStock(productId: Long): StockEntity {
        return findStockOrThrow(productId)
    }

    @Transactional(readOnly = true)
    fun getStockWithValidation(user: User, productId: Long): StockEntity {
        stockValidator.validateStockAccess(user, productId)
        return findStockOrThrow(productId)
    }

    @Transactional(readOnly = true)
    fun getStockHistory(productId: Long, pageable: Pageable): Page<StockHistoryEntity> {
        return stockHistoryRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
    }

    private fun findStockOrThrow(productId: Long): StockEntity {
        return stockRepository.findByProductId(productId)
            .filter { it.isActive }
            .orElseThrow { CoreException(ErrorType.STOCK_NOT_FOUND) }
    }
}
