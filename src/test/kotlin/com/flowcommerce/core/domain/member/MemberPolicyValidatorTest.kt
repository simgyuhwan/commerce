package com.flowcommerce.core.domain.member

import com.flowcommerce.core.storage.member.MemberRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.BDDMockito.given
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MemberPolicyValidatorTest {

    @Mock
    lateinit var memberRepository: MemberRepository

    @InjectMocks
    lateinit var memberPolicyValidator: MemberPolicyValidator

    @Test
    fun `이미 가입된 회원은 다시 가입할 수 없다`() {
        val signUp = MemberFixture.createSignUp()
        given(memberRepository.existsByEmail(anyString())).willReturn(true)

        assertThatThrownBy { memberPolicyValidator.validateNew(signUp) }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.DUPLICATE_EMAIL)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "1234567"])
    fun `비밀번호가 8자 미만이면 예외가 발생한다`(password: String) {
        val signUp = MemberFixture.createSignUp("email@naver.com", password, "test")
        given(memberRepository.existsByEmail(signUp.email)).willReturn(false)

        assertThatThrownBy { memberPolicyValidator.validateNew(signUp) }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_PASSWORD_POLICY)
    }

    @ParameterizedTest
    @ValueSource(strings = ["password1!", "nocapital1!", "alllower1!"])
    fun `비밀번호에 대문자가 없으면 예외가 발생한다`(password: String) {
        // given
        val signUp = SignUp("test@example.com", password, "test")
        given(memberRepository.existsByEmail(anyString())).willReturn(false)

        // when & then
        assertThatThrownBy { memberPolicyValidator.validateNew(signUp) }
            .isInstanceOf(CoreException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["ABCDEFGHI1!", "DDDDDD!2", "1234567!"])
    fun `비밀번호에 소문자가 없으면 예외가 발생한다`(password: String) {
        val signUp = SignUp("email@naver.com", password, "test")
        given(memberRepository.existsByEmail(anyString())).willReturn(false)

        assertThatThrownBy { memberPolicyValidator.validateNew(signUp) }
            .isInstanceOf(CoreException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Password1", "NoSpecial1", "TestPass123"])
    fun `비밀번호에 특수문자가 없으면 예외가 발생한다`(password: String) {
        // given
        val signUp = SignUp("test@example.com", password, "test")
        given(memberRepository.existsByEmail(anyString())).willReturn(false)

        // when & then
        assertThatThrownBy { memberPolicyValidator.validateNew(signUp) }
            .isInstanceOf(CoreException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Password1!", "ValidPass123@", "GoodPwd#456"])
    fun `비밀번호가 정책을 만족하면 정상 처리된다`(password: String) {
        // given
        val signUp = SignUp("test@example.com", password, "test")
        given(memberRepository.existsByEmail(anyString())).willReturn(false)

        // when & then
        assertThatCode { memberPolicyValidator.validateNew(signUp) }
            .doesNotThrowAnyException()
    }
}
