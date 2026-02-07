package com.flowcommerce.core.domain.product

import com.flowcommerce.core.storage.product.ProductEntity
import com.flowcommerce.core.storage.product.ProductImageEntity
import com.flowcommerce.core.storage.product.ProductImageRepository
import com.flowcommerce.core.storage.product.ProductRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import com.flowcommerce.core.support.utils.DocumentService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class ProductWriter(
    private val productRepository: ProductRepository,
    private val productImageRepository: ProductImageRepository,
    private val documentService: DocumentService
) {
    @Transactional
    fun save(sellerId: Long, productCreate: ProductCreate, images: List<MultipartFile>?): Long {
        val productEntity = productRepository.save(
            ProductEntity.create(
                sellerId,
                productCreate.productName,
                productCreate.description,
                productCreate.originalPrice,
                productCreate.sellingPrice,
                calculateDiscountRate(productCreate.originalPrice, productCreate.sellingPrice),
                productCreate.category,
                ProductStatus.DRAFT,
                productCreate.thumbnailUrl,
                productCreate.liveExclusive,
                productCreate.liveDiscountPrice
            )
        )

        if (!images.isNullOrEmpty()) {
            saveProductImages(productEntity.id, images)
        }

        return productEntity.id
    }

    @Transactional
    fun update(product: ProductEntity, productUpdate: ProductUpdate, newImages: List<MultipartFile>?) {
        product.update(
            productUpdate.productName,
            productUpdate.description,
            productUpdate.originalPrice,
            productUpdate.sellingPrice,
            calculateDiscountRate(productUpdate.originalPrice, productUpdate.sellingPrice),
            productUpdate.category,
            productUpdate.thumbnailUrl,
            productUpdate.liveExclusive,
            productUpdate.liveDiscountPrice
        )
        productRepository.save(product)

        if (!newImages.isNullOrEmpty()) {
            productImageRepository.deleteByProductId(product.id)
            saveProductImages(product.id, newImages)
        }
    }

    @Transactional
    fun delete(product: ProductEntity) {
        product.delete()
        productRepository.save(product)
    }

    @Transactional
    fun startSale(product: ProductEntity) {
        product.startSale()
        productRepository.save(product)
    }

    @Transactional
    fun hide(product: ProductEntity) {
        product.hide()
        productRepository.save(product)
    }

    private fun saveProductImages(productId: Long, images: List<MultipartFile>) {
        images.forEachIndexed { index, image ->
            try {
                val fileInfo = documentService.save("product/$productId", image)

                productImageRepository.save(
                    ProductImageEntity.create(
                        productId,
                        fileInfo.filePath,
                        index,
                        index == 0
                    )
                )
            } catch (e: Exception) {
                throw CoreException(ErrorType.FILE_SAVE_FAILED)
            }
        }
    }

    private fun calculateDiscountRate(originalPrice: BigDecimal, sellingPrice: BigDecimal): BigDecimal {
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO
        }
        return originalPrice.subtract(sellingPrice)
            .divide(originalPrice, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP)
    }
}
