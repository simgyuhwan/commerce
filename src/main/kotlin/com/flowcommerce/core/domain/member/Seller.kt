package com.flowcommerce.core.domain.member

import java.time.LocalDateTime

data class Seller(
    val id: Long?,
    val memberId: Long,
    val sellerName: String,
    val status: SellerStatus,
    val companyInfo: CompanyInfo,
    val contactInfo: ContactInfo,
    val paymentInfo: PaymentInfo,
    val commission: Commission,
    val approvedBy: Long?,
    val approvedAt: LocalDateTime?
)
