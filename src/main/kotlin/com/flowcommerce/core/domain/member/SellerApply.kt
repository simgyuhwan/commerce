package com.flowcommerce.core.domain.member

import java.math.BigDecimal

data class SellerApply(
    val memberId: Long,
    val sellerName: String,
    val status: SellerStatus,
    val companyName: String,
    val businessNumber: String,
    val onlineSalesNumber: String,
    val businessType: String,
    val companyPhone: String,
    val address: String,
    val contactName: String,
    val contactPhone: String,
    val contactEmail: String,
    val accountHolder: String,
    val bankName: String,
    val accountNumber: String,
    val commissionRate: BigDecimal,
    val settlementBasis: SettlementBasis
)
