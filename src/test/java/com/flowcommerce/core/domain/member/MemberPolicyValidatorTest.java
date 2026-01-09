package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.storage.member.MemberRepository;
import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberPolicyValidatorTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberPolicyValidator memberPolicyValidator;

    @Test
    void 이미_가입된_회원은_다시가입할_수_없다() {
        SignUp signUp = MemberFixture.createSignUp();
        given(memberRepository.existsByEmail(anyString())).willReturn(true);

        assertThatThrownBy(()-> memberPolicyValidator.validateNew(signUp))
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.DUPLICATE_EMAIL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1234567"})
    void 비밀번호가_8자_미만이면_예외가_발생한다(String password){
        SignUp signUp = MemberFixture.createSignUp("email@naver.com", password, "test");
        given(memberRepository.existsByEmail(signUp.email())).willReturn(false);

        assertThatThrownBy(() -> memberPolicyValidator.validateNew(signUp))
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_PASSWORD_POLICY);
    }


    @ParameterizedTest
    @ValueSource(strings = {"password1!", "nocapital1!", "alllower1!"})
    void 비밀번호에_대문자가_없으면_예외가_발생한다(String password) {
        // given
        SignUp signUp = new SignUp("test@example.com", password, "test");
        given(memberRepository.existsByEmail(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberPolicyValidator.validateNew(signUp))
                .isInstanceOf(CoreException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABCDEFGHI1!", "DDDDDD!2", "1234567!"})
    void 비밀번호에_소문자가_없으면_예외가_발생한다(String password){
        SignUp signUp = new SignUp("email@naver.com", password, "test");
        given(memberRepository.existsByEmail(anyString())).willReturn(false);

        assertThatThrownBy(() -> memberPolicyValidator.validateNew(signUp))
                .isInstanceOf(CoreException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Password1", "NoSpecial1", "TestPass123"})
    void 비밀번호에_특수문자가_없으면_예외가_발생한다(String password) {
        // given
        SignUp signUp = new SignUp("test@example.com", password, "test");
        given(memberRepository.existsByEmail(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberPolicyValidator.validateNew(signUp))
                .isInstanceOf(CoreException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Password1!", "ValidPass123@", "GoodPwd#456"})
    void 비밀번호가_정책을_만족하면_정상_처리된다(String password) {
        // given
        SignUp signUp = new SignUp("test@example.com", password, "test");
        given(memberRepository.existsByEmail(anyString())).willReturn(false);

        // when & then
        assertThatCode(() -> memberPolicyValidator.validateNew(signUp))
                .doesNotThrowAnyException();
    }
}