package com.flowcommerce.core.domain.product.service

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.product.ProductCreate
import com.flowcommerce.core.domain.product.ProductUpdate
import com.flowcommerce.core.domain.product.ProductValidator
import com.flowcommerce.core.domain.product.ProductWriter
import com.flowcommerce.core.storage.product.ProductEntity
import com.flowcommerce.core.storage.product.ProductImageEntity
import com.flowcommerce.core.storage.product.ProductImageRepository
import com.flowcommerce.core.storage.product.ProductRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ProductService(
    private val productValidator: ProductValidator,
    private val productWriter: ProductWriter,
    private val productRepository: ProductRepository,
    private val productImageRepository: ProductImageRepository,
    private val stockService: StockService
) {
    @Transactional
    fun registerProduct(
        user: User,
        sellerId: Long,
        productCreate: ProductCreate,
        images: List<MultipartFile>?
    ): Long {
        productValidator.validateProductCreate(user, sellerId, productCreate)

        val productId = productWriter.save(sellerId, productCreate, images)

        stockService.createInitialStock(
            productId,
            productCreate.initialStock,
            productCreate.alertThreshold,
            productCreate.alertEnabled,
            user.id
        )

        return productId
    }

    @Transactional
    fun updateProduct(
        user: User,
        productId: Long,
        productUpdate: ProductUpdate,
        newImages: List<MultipartFile>?
    ) {
        val product = findProductOrThrow(productId)
        productValidator.validateProductUpdate(user, product, productUpdate)
        productWriter.update(product, productUpdate, newImages)
    }

    @Transactional
    fun deleteProduct(user: User, productId: Long) {
        val product = findProductOrThrow(productId)
        productValidator.validateProductDelete(user, product)
        productWriter.delete(product)
    }

    @Transactional
    fun startSale(user: User, productId: Long) {
        val product = findProductOrThrow(productId)
        productValidator.validateStartSale(user, product)
        productWriter.startSale(product)
    }

    @Transactional
    fun hideProduct(user: User, productId: Long) {
        val product = findProductOrThrow(productId)
        productValidator.validateSellerAccess(user, product.sellerId)
        productWriter.hide(product)
    }

    @Transactional(readOnly = true)
    fun getSellerProducts(user: User, sellerId: Long, pageable: Pageable): Page<ProductEntity> {
        productValidator.validateSellerAccess(user, sellerId)
        return productRepository.findActiveProductsBySeller(sellerId, pageable)
    }

    @Transactional(readOnly = true)
    fun getProduct(user: User, sellerId: Long, productId: Long): ProductEntity {
        productValidator.validateSellerAccess(user, sellerId)
        return findProductOrThrow(productId)
    }

    @Transactional(readOnly = true)
    fun getProductImages(productId: Long): List<ProductImageEntity> {
        return productImageRepository.findByProductIdOrderByDisplayOrder(productId)
    }

    private fun findProductOrThrow(productId: Long): ProductEntity {
        return productRepository.findById(productId)
            .filter { it.isActive }
            .orElseThrow { CoreException(ErrorType.PRODUCT_NOT_FOUND) }
    }
}
