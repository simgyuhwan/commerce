package com.flowcommerce.core.support.auth

import org.springframework.stereotype.Component

@Component
class PasswordEncoder {

    // TODO 암호화 알고리즘 적용해야함
    fun encode(password: String): String = password

    fun matches(password: String, encodedPassword: String): Boolean =
        password == encodedPassword
}
