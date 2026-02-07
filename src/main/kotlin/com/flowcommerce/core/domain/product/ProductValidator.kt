package com.flowcommerce.core.domain.product

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.member.SellerStatus
import com.flowcommerce.core.storage.EntityStatus
import com.flowcommerce.core.storage.member.SellerEntity
import com.flowcommerce.core.storage.member.SellerRepository
import com.flowcommerce.core.storage.product.ProductEntity
import com.flowcommerce.core.storage.product.ProductRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProductValidator(
    private val sellerRepository: SellerRepository,
    private val productRepository: ProductRepository
) {
    fun validateProductCreate(user: User, sellerId: Long, productCreate: ProductCreate) {
        validateSellerApproved(sellerId)
        validateSellerOwnership(user, sellerId)
        validateProductData(productCreate)
        validateDuplicateProductName(sellerId, productCreate.productName)
    }

    fun validateProductUpdate(user: User, product: ProductEntity, productUpdate: ProductUpdate) {
        validateSellerOwnership(user, product.sellerId)
        validateEditableStatus(product)
        if (product.productName != productUpdate.productName) {
            validateDuplicateProductName(product.sellerId, productUpdate.productName)
        }
    }

    fun validateProductDelete(user: User, product: ProductEntity) {
        validateSellerOwnership(user, product.sellerId)
        validateDeletableStatus(product)
    }

    fun validateStartSale(user: User, product: ProductEntity) {
        validateSellerOwnership(user, product.sellerId)
        if (product.productStatus != ProductStatus.DRAFT && product.productStatus != ProductStatus.HIDDEN) {
            throw CoreException(ErrorType.PRODUCT_INVALID_STATUS)
        }
    }

    fun validateSellerAccess(user: User, sellerId: Long) {
        validateSellerOwnership(user, sellerId)
    }

    fun getSellerByMemberId(memberId: Long): SellerEntity? {
        return sellerRepository.findByMemberId(memberId)
    }

    private fun validateSellerApproved(sellerId: Long) {
        val seller = sellerRepository.findById(sellerId)
            .orElseThrow { CoreException(ErrorType.SELLER_NOT_FOUND) }

        if (seller.sellerStatus != SellerStatus.APPROVED) {
            throw CoreException(ErrorType.SELLER_NOT_APPROVED_FOR_PRODUCT)
        }
    }

    private fun validateSellerOwnership(user: User, sellerId: Long) {
        val seller = sellerRepository.findByMemberId(user.id)
        if (seller == null || seller.id != sellerId) {
            throw CoreException(ErrorType.PRODUCT_ACCESS_DENIED)
        }
    }

    private fun validateProductData(productCreate: ProductCreate) {
        if (productCreate.productName.isBlank()) {
            throw CoreException(ErrorType.PRODUCT_NAME_REQUIRED)
        }
        if (productCreate.originalPrice <= BigDecimal.ZERO) {
            throw CoreException(ErrorType.PRODUCT_PRICE_INVALID)
        }
        if (productCreate.sellingPrice <= BigDecimal.ZERO) {
            throw CoreException(ErrorType.PRODUCT_PRICE_INVALID)
        }
        if (productCreate.sellingPrice > productCreate.originalPrice) {
            throw CoreException(ErrorType.PRODUCT_PRICE_INVALID)
        }
        if (productCreate.initialStock < 0) {
            throw CoreException(ErrorType.STOCK_QUANTITY_INVALID)
        }
    }

    private fun validateDuplicateProductName(sellerId: Long, productName: String) {
        if (productRepository.existsBySellerIdAndProductNameAndStatusNot(sellerId, productName, EntityStatus.DELETED)) {
            throw CoreException(ErrorType.PRODUCT_NAME_DUPLICATE)
        }
    }

    private fun validateEditableStatus(product: ProductEntity) {
        if (product.productStatus == ProductStatus.ON_SALE) {
            throw CoreException(ErrorType.PRODUCT_INVALID_STATUS)
        }
    }

    private fun validateDeletableStatus(product: ProductEntity) {
        if (product.productStatus == ProductStatus.ON_SALE) {
            throw CoreException(ErrorType.PRODUCT_INVALID_STATUS)
        }
    }
}
