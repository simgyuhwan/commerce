package com.flowcommerce.core.domain.product

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.storage.member.SellerRepository
import com.flowcommerce.core.storage.product.ProductRepository
import com.flowcommerce.core.storage.product.StockEntity
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class StockValidator(
    private val sellerRepository: SellerRepository,
    private val productRepository: ProductRepository
) {
    fun validateIncrease(user: User, stock: StockEntity, adjust: StockAdjust) {
        validateStockOwnership(user, stock.productId)
        if (adjust.quantity <= 0) {
            throw CoreException(ErrorType.STOCK_QUANTITY_INVALID)
        }
    }

    fun validateDecrease(user: User, stock: StockEntity, adjust: StockAdjust) {
        validateStockOwnership(user, stock.productId)
        if (adjust.quantity <= 0) {
            throw CoreException(ErrorType.STOCK_QUANTITY_INVALID)
        }
        if (stock.availableQuantity < adjust.quantity) {
            throw CoreException(ErrorType.STOCK_INSUFFICIENT)
        }
    }

    fun validateAdjustment(user: User, stock: StockEntity, adjust: StockAdjust) {
        validateStockOwnership(user, stock.productId)
        if (adjust.newQuantity < 0) {
            throw CoreException(ErrorType.STOCK_QUANTITY_INVALID)
        }
    }

    fun validateAlertUpdate(user: User, stock: StockEntity) {
        validateStockOwnership(user, stock.productId)
    }

    fun validateStockAccess(user: User, productId: Long) {
        validateStockOwnership(user, productId)
    }

    private fun validateStockOwnership(user: User, productId: Long) {
        val product = productRepository.findById(productId)
            .orElseThrow { CoreException(ErrorType.PRODUCT_NOT_FOUND) }

        val seller = sellerRepository.findByMemberId(user.id)
        if (seller == null || seller.id != product.sellerId) {
            throw CoreException(ErrorType.STOCK_ACCESS_DENIED)
        }
    }
}
