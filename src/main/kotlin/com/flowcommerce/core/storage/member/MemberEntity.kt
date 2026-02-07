package com.flowcommerce.core.storage.member

import com.flowcommerce.core.domain.member.AuthProvider
import com.flowcommerce.core.domain.member.MemberStatus
import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "members")
class MemberEntity protected constructor() : BaseEntity() {

    @Column(unique = true, nullable = false)
    var email: String = ""
        protected set

    var password: String? = null
        protected set

    var name: String? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "member_status")
    var memberStatus: MemberStatus = MemberStatus.ACTIVE
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var authProvider: AuthProvider = AuthProvider.LOCAL
        protected set

    var providerId: String? = null
        protected set

    companion object {
        fun forLocalSignUp(email: String, encodedPassword: String, name: String): MemberEntity {
            return MemberEntity().apply {
                this.email = email
                this.password = encodedPassword
                this.name = name
                this.memberStatus = MemberStatus.ACTIVE
                this.authProvider = AuthProvider.LOCAL
            }
        }
    }
}
