package com.flowcommerce.core.storage.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, Long> {
    fun existsByEmail(email: String): Boolean
}
