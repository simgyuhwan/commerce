package com.flowcommerce.core.api.v1.product

import com.flowcommerce.core.api.v1.product.request.ProductCreateRequest
import com.flowcommerce.core.api.v1.product.request.ProductUpdateRequest
import com.flowcommerce.core.api.v1.product.response.ProductDetailResponse
import com.flowcommerce.core.api.v1.product.response.ProductResponse
import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.product.service.ProductService
import com.flowcommerce.core.support.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Product", description = "상품 관리 API")
@RequestMapping("/api/v1/sellers/{sellerId}/products")
@RestController
class ProductController(
    private val productService: ProductService
) {
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun registerProduct(
        user: User,
        @PathVariable sellerId: Long,
        @RequestPart("data") @Valid request: ProductCreateRequest,
        @RequestPart("images", required = false) images: List<MultipartFile>?
    ): ApiResponse<Long> {
        val productId = productService.registerProduct(user, sellerId, request.toProductCreate(), images)
        return ApiResponse.success(productId)
    }

    @Operation(summary = "상품 목록 조회", description = "셀러의 상품 목록을 조회합니다.")
    @GetMapping
    fun getProducts(
        user: User,
        @PathVariable sellerId: Long,
        pageable: Pageable
    ): ApiResponse<Page<ProductResponse>> {
        val products = productService.getSellerProducts(user, sellerId, pageable)
        return ApiResponse.success(products.map { ProductResponse.from(it) })
    }

    @Operation(summary = "상품 상세 조회", description = "상품 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    fun getProduct(
        user: User,
        @PathVariable sellerId: Long,
        @PathVariable productId: Long
    ): ApiResponse<ProductDetailResponse> {
        val product = productService.getProduct(user, sellerId, productId)
        val images = productService.getProductImages(productId)
        return ApiResponse.success(ProductDetailResponse.from(product, images))
    }

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @PutMapping("/{productId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProduct(
        user: User,
        @PathVariable sellerId: Long,
        @PathVariable productId: Long,
        @RequestPart("data") @Valid request: ProductUpdateRequest,
        @RequestPart("images", required = false) images: List<MultipartFile>?
    ): ApiResponse<Void> {
        productService.updateProduct(user, productId, request.toProductUpdate(), images)
        return ApiResponse.success()
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    fun deleteProduct(
        user: User,
        @PathVariable sellerId: Long,
        @PathVariable productId: Long
    ): ApiResponse<Void> {
        productService.deleteProduct(user, productId)
        return ApiResponse.success()
    }

    @Operation(summary = "판매 시작", description = "상품의 판매를 시작합니다.")
    @PostMapping("/{productId}/start-sale")
    fun startSale(
        user: User,
        @PathVariable sellerId: Long,
        @PathVariable productId: Long
    ): ApiResponse<Void> {
        productService.startSale(user, productId)
        return ApiResponse.success()
    }

    @Operation(summary = "상품 숨김", description = "상품을 숨김 처리합니다.")
    @PostMapping("/{productId}/hide")
    fun hideProduct(
        user: User,
        @PathVariable sellerId: Long,
        @PathVariable productId: Long
    ): ApiResponse<Void> {
        productService.hideProduct(user, productId)
        return ApiResponse.success()
    }
}
