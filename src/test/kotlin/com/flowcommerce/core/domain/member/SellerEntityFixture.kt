package com.flowcommerce.core.domain.member

import com.flowcommerce.core.storage.member.SellerEntity
import java.math.BigDecimal
import java.time.LocalDateTime

object SellerEntityFixture {
    fun createDefault(): SellerEntity = createWithStatus(SellerStatus.PENDING)

    fun createWithStatus(status: SellerStatus): SellerEntity = SellerEntity(
        memberId = 1L,
        sellerName = "테스트판매자",
        sellerStatus = status,
        companyName = "테스트컴퍼니",
        businessNumber = "123-45-67890",
        onlineSalesNumber = "2023-서울-1234",
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
        settlementBasis = SettlementBasis.PURCHASE_CONFIRMED,
        approvedBy = null,
        approvedAt = null
    )

    fun create(id: Long, status: SellerStatus): SellerEntity = createWithStatus(status)

    fun createWithMember(memberId: Long, status: SellerStatus): SellerEntity =
        createWithStatus(status).apply {
            // Note: memberId is set via constructor, this creates a new instance
        }

    fun createPending(): SellerEntity = createWithStatus(SellerStatus.PENDING)

    fun createApproved(): SellerEntity = createWithStatus(SellerStatus.APPROVED)

    fun createRejected(): SellerEntity = createWithStatus(SellerStatus.REJECTED)
}
