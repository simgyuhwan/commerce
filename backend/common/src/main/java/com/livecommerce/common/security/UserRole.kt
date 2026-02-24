package com.livecommerce.common.security

enum class UserRole {
    ADMIN, SELLER, BUYER;

    fun toAuthority() = "ROLE_$name"
}