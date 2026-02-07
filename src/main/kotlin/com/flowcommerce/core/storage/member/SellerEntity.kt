package com.flowcommerce.core.storage.member

import com.flowcommerce.core.domain.member.SellerStatus
import com.flowcommerce.core.domain.member.SettlementBasis
import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "seller")
class SellerEntity(
    @Column(nullable = false, unique = true)
    var memberId: Long,

    @Column(nullable = false, unique = true)
    var sellerName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sellerStatus: SellerStatus,

    var companyName: String?,

    @Column(unique = true, nullable = false)
    var businessNumber: String,

    var onlineSalesNumber: String?,
    var businessType: String?,
    var companyPhone: String?,
    var address: String?,

    var contactName: String?,
    var contactPhone: String?,
    var contactEmail: String?,

    var accountHolder: String?,
    var bankName: String?,
    var accountNumber: String?,

    var commissionRate: BigDecimal?,

    @Enumerated(EnumType.STRING)
    var settlementBasis: SettlementBasis?,

    var approvedBy: Long?,
    var approvedAt: LocalDateTime?
) : BaseEntity() {
    protected constructor() : this(
        0L, "", SellerStatus.PENDING, null, "", null, null, null, null,
        null, null, null, null, null, null, null, null, null, null
    )
}
