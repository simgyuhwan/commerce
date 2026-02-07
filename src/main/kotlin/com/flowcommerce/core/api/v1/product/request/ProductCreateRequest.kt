package com.flowcommerce.core.api.v1.product.request

import com.flowcommerce.core.domain.product.ProductCategory
import com.flowcommerce.core.domain.product.ProductCreate
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ProductCreateRequest(
    @field:Schema(description = "상품명", example = "프리미엄 코튼 티셔츠")
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(max = 200)
    val productName: String,

    @field:Schema(description = "상품 설명", example = "부드러운 프리미엄 코튼 소재의 기본 티셔츠입니다.")
    val description: String?,

    @field:Schema(description = "정가", example = "35000")
    @field:NotNull(message = "정가는 필수입니다")
    @field:DecimalMin(value = "0.01", message = "정가는 0보다 커야 합니다")
    val originalPrice: BigDecimal,

    @field:Schema(description = "판매가", example = "29000")
    @field:NotNull(message = "판매가는 필수입니다")
    @field:DecimalMin(value = "0.01", message = "판매가는 0보다 커야 합니다")
    val sellingPrice: BigDecimal,

    @field:Schema(description = "카테고리", example = "FASHION")
    @field:NotNull(message = "카테고리는 필수입니다")
    val category: ProductCategory,

    @field:Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
    val thumbnailUrl: String?,

    @field:Schema(description = "라이브 전용 상품 여부", example = "false")
    val liveExclusive: Boolean = false,

    @field:Schema(description = "라이브 특가", example = "25000")
    val liveDiscountPrice: BigDecimal?,

    @field:Schema(description = "초기 재고", example = "100")
    @field:NotNull(message = "초기 재고는 필수입니다")
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    val initialStock: Int,

    @field:Schema(description = "재고 알림 임계값", example = "10")
    @field:Min(value = 1)
    val alertThreshold: Int = 10,

    @field:Schema(description = "재고 알림 활성화 여부", example = "true")
    val alertEnabled: Boolean = true
) {
    fun toProductCreate(): ProductCreate = ProductCreate(
        productName = productName,
        description = description ?: "",
        originalPrice = originalPrice,
        sellingPrice = sellingPrice,
        category = category,
        thumbnailUrl = thumbnailUrl,
        liveExclusive = liveExclusive,
        liveDiscountPrice = liveDiscountPrice,
        initialStock = initialStock,
        alertThreshold = alertThreshold,
        alertEnabled = alertEnabled
    )
}
