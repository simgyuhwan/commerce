package com.flowcommerce.core.storage.member

import org.springframework.data.jpa.repository.JpaRepository

interface SellerRepository : JpaRepository<SellerEntity, Long> {
    fun existsBySellerName(sellerName: String): Boolean
    fun existsByBusinessNumber(businessNumber: String): Boolean
    fun findByMemberId(memberId: Long): SellerEntity?
}
