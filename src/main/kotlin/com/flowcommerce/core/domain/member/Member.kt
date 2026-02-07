package com.flowcommerce.core.domain.member

data class Member(
    val id: Long?,
    val email: String,
    val password: String,
    val name: String,
    val authProvider: AuthProvider,
    val providerId: String?,
    val status: MemberStatus
) {
    companion object {
        fun create(name: String, email: String, password: String): Member =
            Member(
                id = null,
                email = email,
                password = password,
                name = name,
                authProvider = AuthProvider.LOCAL,
                providerId = null,
                status = MemberStatus.ACTIVE
            )
    }
}
