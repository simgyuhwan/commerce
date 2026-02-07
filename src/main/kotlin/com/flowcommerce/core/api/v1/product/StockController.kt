package com.flowcommerce.core.api.v1.product

import com.flowcommerce.core.api.v1.product.request.StockAdjustRequest
import com.flowcommerce.core.api.v1.product.request.StockAlertSettingRequest
import com.flowcommerce.core.api.v1.product.request.StockReserveRequest
import com.flowcommerce.core.api.v1.product.response.StockHistoryResponse
import com.flowcommerce.core.api.v1.product.response.StockResponse
import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.product.service.StockService
import com.flowcommerce.core.support.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@Tag(name = "Stock", description = "재고 관리 API")
@RequestMapping("/api/v1/products/{productId}/stock")
@RestController
class StockController(
    private val stockService: StockService
) {
    @Operation(summary = "재고 조회", description = "상품의 현재 재고를 조회합니다.")
    @GetMapping
    fun getStock(
        user: User,
        @PathVariable productId: Long
    ): ApiResponse<StockResponse> {
        val stock = stockService.getStockWithValidation(user, productId)
        return ApiResponse.success(StockResponse.from(stock))
    }

    @Operation(summary = "재고 증가", description = "재고를 증가시킵니다 (입고).")
    @PostMapping("/increase")
    fun increaseStock(
        user: User,
        @PathVariable productId: Long,
        @RequestBody @Valid request: StockAdjustRequest
    ): ApiResponse<Void> {
        stockService.increaseStock(user, productId, request.toStockAdjust())
        return ApiResponse.success()
    }

    @Operation(summary = "재고 감소", description = "재고를 감소시킵니다.")
    @PostMapping("/decrease")
    fun decreaseStock(
        user: User,
        @PathVariable productId: Long,
        @RequestBody @Valid request: StockAdjustRequest
    ): ApiResponse<Void> {
        stockService.decreaseStock(user, productId, request.toStockAdjust())
        return ApiResponse.success()
    }

    @Operation(summary = "재고 조정", description = "재고를 특정 값으로 조정합니다 (실사 후).")
    @PostMapping("/adjust")
    fun adjustStock(
        user: User,
        @PathVariable productId: Long,
        @RequestBody @Valid request: StockAdjustRequest
    ): ApiResponse<Void> {
        stockService.adjustStock(user, productId, request.toStockAdjust())
        return ApiResponse.success()
    }

    @Operation(summary = "재고 알림 설정", description = "재고 부족 알림 설정을 변경합니다.")
    @PutMapping("/alert-settings")
    fun updateAlertSettings(
        user: User,
        @PathVariable productId: Long,
        @RequestBody @Valid request: StockAlertSettingRequest
    ): ApiResponse<Void> {
        stockService.updateAlertSettings(user, productId, request.threshold, request.enabled)
        return ApiResponse.success()
    }

    @Operation(summary = "재고 이력 조회", description = "재고 변경 이력을 조회합니다.")
    @GetMapping("/history")
    fun getStockHistory(
        user: User,
        @PathVariable productId: Long,
        pageable: Pageable
    ): ApiResponse<Page<StockHistoryResponse>> {
        val history = stockService.getStockHistory(productId, pageable)
        return ApiResponse.success(history.map { StockHistoryResponse.from(it) })
    }

    @Operation(summary = "라이브 재고 예약", description = "라이브 커머스용 재고를 예약합니다.")
    @PostMapping("/reserve")
    fun reserveStock(
        @PathVariable productId: Long,
        @RequestBody @Valid request: StockReserveRequest
    ): ApiResponse<Boolean> {
        val result = stockService.reserveStock(productId, request.quantity, request.liveId)
        return ApiResponse.success(result)
    }

    @Operation(summary = "예약 해제", description = "예약된 재고를 해제합니다.")
    @PostMapping("/release")
    fun releaseReservation(
        @PathVariable productId: Long,
        @RequestBody @Valid request: StockReserveRequest
    ): ApiResponse<Void> {
        stockService.releaseReservation(productId, request.quantity, request.liveId, "LIVE")
        return ApiResponse.success()
    }

    @Operation(summary = "판매 확정", description = "예약된 재고를 판매 확정 처리합니다.")
    @PostMapping("/confirm")
    fun confirmSale(
        @PathVariable productId: Long,
        @RequestParam quantity: Int,
        @RequestParam orderId: Long
    ): ApiResponse<Void> {
        stockService.confirmSale(productId, quantity, orderId)
        return ApiResponse.success()
    }
}
