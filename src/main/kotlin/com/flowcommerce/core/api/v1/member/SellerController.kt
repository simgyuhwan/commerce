package com.flowcommerce.core.api.v1.member

import com.flowcommerce.core.api.v1.member.request.SellerApplyRequest
import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.member.service.SellerService
import com.flowcommerce.core.support.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/api/v1/sellers")
@RestController
class SellerController(
    private val sellerService: SellerService
) {
    @PostMapping("/apply", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun applySeller(
        user: User,
        @RequestPart("data") @Valid request: SellerApplyRequest,
        @RequestPart("businessLicense") businessLicense: MultipartFile,
        @RequestPart("bankbookCopy") bankbookCopy: MultipartFile,
        @RequestPart("onlineSalesLicense") onlineSalesLicense: MultipartFile
    ): ApiResponse<Void> {
        sellerService.apply(user, request.toSellerApply(), businessLicense, bankbookCopy, onlineSalesLicense)
        return ApiResponse.success()
    }
}
