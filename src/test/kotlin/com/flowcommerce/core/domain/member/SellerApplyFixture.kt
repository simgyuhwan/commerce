package com.flowcommerce.core.domain.member

import com.flowcommerce.core.TestFixtureConfig.fixtureMonkey
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

object SellerApplyFixture {
    fun createDefault(): SellerApply = SellerApply(
        memberId = 1L,
        sellerName = "테스트판매자${System.currentTimeMillis()}",
        status = SellerStatus.PENDING,
        companyName = "테스트컴퍼니",
        businessNumber = generateBusinessNumber(),
        onlineSalesNumber = generateOnlineSalesNumber(),
        businessType = "도소매업",
        companyPhone = "02-1234-5678",
        address = "서울시 강남구",
        contactName = "홍길동",
        contactPhone = "010-1234-5678",
        contactEmail = "test@example.com",
        accountHolder = "홍길동",
        bankName = "신한은행",
        accountNumber = "110-123-456789",
        commissionRate = BigDecimal("10.0"),
        settlementBasis = SettlementBasis.PURCHASE_CONFIRMED
    )

    fun createValid(): SellerApply = createDefault()

    fun createWithSellerName(sellerName: String): SellerApply = createDefault().copy(sellerName = sellerName)

    fun createWithBusinessNumber(businessNumber: String): SellerApply = createDefault().copy(businessNumber = businessNumber)

    fun createWithAccount(accountHolder: String, bankName: String, accountNumber: String): SellerApply =
        createDefault().copy(
            accountHolder = accountHolder,
            bankName = bankName,
            accountNumber = accountNumber
        )

    private fun generateBusinessNumber(): String =
        String.format(
            "%03d-%02d-%05d",
            ThreadLocalRandom.current().nextInt(100, 999),
            ThreadLocalRandom.current().nextInt(10, 99),
            ThreadLocalRandom.current().nextInt(10000, 99999)
        )

    private fun generateOnlineSalesNumber(): String =
        String.format("2024-%05d", ThreadLocalRandom.current().nextInt(10000, 99999))
}
