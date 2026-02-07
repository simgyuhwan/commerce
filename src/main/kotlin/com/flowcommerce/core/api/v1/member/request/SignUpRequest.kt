package com.flowcommerce.core.api.v1.member.request

import com.flowcommerce.core.domain.member.SignUp
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignUpRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수입니다.")
    val email: String,

    @field:Schema(description = "비밀번호 (8자 이상, 영문/숫자/특수문자)", example = "Password123!")
    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String,

    @field:Schema(description = "이름", example = "홍길동")
    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String
) {
    fun toSignUp(): SignUp = SignUp(email, password, name)
}
