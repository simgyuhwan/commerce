package com.flowcommerce.core.storage.member

import com.flowcommerce.core.domain.HistoryType
import com.flowcommerce.core.domain.member.RejectionReason
import com.flowcommerce.core.domain.member.SellerStatus
import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class SellerHistoryEntity(
    var sellerId: Long?,

    @Enumerated(EnumType.STRING)
    var type: HistoryType?,

    @Enumerated(EnumType.STRING)
    var previousStatus: SellerStatus?,

    @Enumerated(EnumType.STRING)
    var newStatus: SellerStatus?,

    @Enumerated(EnumType.STRING)
    var rejectionReason: RejectionReason?,

    var rejectionDetail: String?,

    var processedBy: Long?
) : BaseEntity() {
    protected constructor() : this(null, null, null, null, null, null, null)
}
