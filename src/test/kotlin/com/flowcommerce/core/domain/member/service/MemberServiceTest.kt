package com.flowcommerce.core.domain.member.service

import com.flowcommerce.core.IntegrationTestSupport
import com.flowcommerce.core.domain.member.AuthProvider
import com.flowcommerce.core.domain.member.MemberFixture
import com.flowcommerce.core.domain.member.MemberStatus
import com.flowcommerce.core.storage.member.MemberRepository
import com.flowcommerce.core.support.error.CoreException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MemberServiceTest : IntegrationTestSupport() {

    @Autowired
    lateinit var memberService: MemberService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Test
    fun `회원가입이 가능하다`() {
        // given
        val signUp = MemberFixture.createSignUp()

        // when
        val savedId = memberService.signUp(signUp)
        val member = memberRepository.findById(savedId)
            .orElseThrow { AssertionError("회원이 저장되지 않았습니다.") }

        // then
        assertThat(member.email).isEqualTo(signUp.email)
        assertThat(member.name).isEqualTo(signUp.name)
        assertThat(member.memberStatus).isEqualTo(MemberStatus.ACTIVE)
        assertThat(member.authProvider).isEqualTo(AuthProvider.LOCAL)
    }

    @Test
    fun `중복된 이메일로 회원가입 불가`() {
        // given
        val signUp1 = MemberFixture.createSignUpWithEmail("dup@test.com")
        memberService.signUp(signUp1)
        val signUp2 = MemberFixture.createSignUpWithEmail("dup@test.com")

        // when, then
        assertThatThrownBy { memberService.signUp(signUp2) }
            .isInstanceOf(CoreException::class.java)
    }
}
