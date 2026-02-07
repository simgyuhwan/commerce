package com.flowcommerce.core.domain.member

data class PaymentInfo(
    val accountHolder: String,
    val bankName: String,
    val accountNumber: String
)
