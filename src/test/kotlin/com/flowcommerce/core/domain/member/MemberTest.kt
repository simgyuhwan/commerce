package com.flowcommerce.core.domain.member

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemberTest {

    @Test
    fun `일반 회원가입이 가능하다`() {
        // given
        val email = "test@test.net"
        val password = "Password!"
        val name = "first"

        // when
        val member = Member.create(name, email, password)

        // then
        assertThat(member.email).isEqualTo(email)
        assertThat(member.name).isEqualTo(name)
        assertThat(member.authProvider).isEqualTo(AuthProvider.LOCAL)
        assertThat(member.status).isEqualTo(MemberStatus.ACTIVE)
        assertThat(member.providerId).isNull()
    }
}
