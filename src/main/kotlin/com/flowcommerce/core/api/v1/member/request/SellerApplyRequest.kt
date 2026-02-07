package com.flowcommerce.core.api.v1.member.request

import com.flowcommerce.core.domain.member.SellerApply
import com.flowcommerce.core.domain.member.SellerStatus
import com.flowcommerce.core.domain.member.SettlementBasis
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class SellerApplyRequest(
    val memberId: Long?,

    @field:NotBlank
    val sellerName: String,

    val status: SellerStatus?,

    @field:NotBlank
    val companyName: String,

    @field:NotBlank
    val businessNumber: String,

    @field:NotBlank
    val onlineSalesNumber: String,

    @field:NotBlank
    val businessType: String,

    @field:NotBlank
    val companyPhone: String,

    @field:NotBlank
    val address: String,

    @field:NotBlank
    val contactName: String,

    @field:NotBlank
    val contactPhone: String,

    @field:Email
    @field:NotBlank
    val contactEmail: String,

    @field:NotBlank
    val accountHolder: String,

    @field:NotBlank
    val bankName: String,

    @field:NotBlank
    val accountNumber: String,

    @field:NotNull
    @field:DecimalMin("0.0")
    @field:DecimalMax("100.0")
    val commissionRate: BigDecimal,

    @field:NotNull
    val settlementBasis: SettlementBasis
) {
    fun toSellerApply(): SellerApply = SellerApply(
        memberId = memberId ?: 0L,
        sellerName = sellerName,
        status = status ?: SellerStatus.PENDING,
        companyName = companyName,
        businessNumber = businessNumber,
        onlineSalesNumber = onlineSalesNumber,
        businessType = businessType,
        companyPhone = companyPhone,
        address = address,
        contactName = contactName,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        accountHolder = accountHolder,
        bankName = bankName,
        accountNumber = accountNumber,
        commissionRate = commissionRate,
        settlementBasis = settlementBasis
    )
}
