package com.flowcommerce.core.domain.member

object MemberFixture {
    fun createSignUp(): SignUp = SignUp("test@test.com", "Password1!", "test")

    fun createSignUp(email: String, password: String, name: String): SignUp =
        SignUp(email, password, name)

    fun createSignUpWithEmail(email: String): SignUp =
        SignUp(email, "Password1!", "test")
}
