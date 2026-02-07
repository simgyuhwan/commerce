package com.flowcommerce.core.domain.member

import java.math.BigDecimal

object SellerFixture {
    fun createSellerApply(): SellerApply = createSellerApply("테스트셀러")

    fun createSellerApply(sellerName: String): SellerApply = SellerApply(
        memberId = 1L,
        sellerName = sellerName,
        status = SellerStatus.PENDING,
        companyName = "테스트컴퍼니",
        businessNumber = "123-45-67890",
        onlineSalesNumber = "2023-서울-1234",
        businessType = "도소매업",
        companyPhone = "02-1234-5678",
        address = "서울시 강남구 테스트로 123",
        contactName = "홍길동",
        contactPhone = "010-1234-5678",
        contactEmail = "contact@test.com",
        accountHolder = "홍길동",
        bankName = "신한은행",
        accountNumber = "110-123-456789",
        commissionRate = BigDecimal("0.15"),
        settlementBasis = SettlementBasis.PURCHASE_CONFIRMED
    )

    fun createSellerApplyWithBusinessNumber(businessNumber: String): SellerApply = SellerApply(
        memberId = 1L,
        sellerName = "테스트셀러",
        status = SellerStatus.PENDING,
        companyName = "테스트컴퍼니",
        businessNumber = businessNumber,
        onlineSalesNumber = "2023-서울-1234",
        businessType = "도소매업",
        companyPhone = "02-1234-5678",
        address = "서울시 강남구 테스트로 123",
        contactName = "홍길동",
        contactPhone = "010-1234-5678",
        contactEmail = "contact@test.com",
        accountHolder = "홍길동",
        bankName = "신한은행",
        accountNumber = "110-123-456789",
        commissionRate = BigDecimal("0.15"),
        settlementBasis = SettlementBasis.PURCHASE_CONFIRMED
    )

    fun createSellerApply(businessNumber: String, sellerName: String): SellerApply = SellerApply(
        memberId = 1L,
        sellerName = sellerName,
        status = SellerStatus.PENDING,
        companyName = "테스트컴퍼니",
        businessNumber = businessNumber,
        onlineSalesNumber = "2023-서울-1234",
        businessType = "도소매업",
        companyPhone = "02-1234-5678",
        address = "서울시 강남구 테스트로 123",
        contactName = "홍길동",
        contactPhone = "010-1234-5678",
        contactEmail = "contact@test.com",
        accountHolder = "홍길동",
        bankName = "신한은행",
        accountNumber = "110-123-456789",
        commissionRate = BigDecimal("0.15"),
        settlementBasis = SettlementBasis.PURCHASE_CONFIRMED
    )
}
