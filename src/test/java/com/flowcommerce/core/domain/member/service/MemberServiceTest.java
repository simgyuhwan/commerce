package com.flowcommerce.core.domain.member.service;

import com.flowcommerce.core.IntegrationTestSupport;
import com.flowcommerce.core.domain.member.AuthProvider;
import com.flowcommerce.core.domain.member.MemberFixture;
import com.flowcommerce.core.domain.member.MemberStatus;
import com.flowcommerce.core.domain.member.SignUp;
import com.flowcommerce.core.storage.member.MemberEntity;
import com.flowcommerce.core.storage.member.MemberRepository;
import com.flowcommerce.core.support.error.CoreException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입이_가능하다(){
        // given
        SignUp signUp = MemberFixture.createSignUp();

        // when
        Long savedId = memberService.signUp(signUp);
        MemberEntity member = memberRepository.findById(savedId)
                .orElseThrow(() -> new AssertionError("회원이 저장되지 않앗습니다."));

        // then
        assertThat(member.getEmail()).isEqualTo(signUp.email());
        assertThat(member.getName()).isEqualTo(signUp.name());
        assertThat(member.getMemberStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getAuthProvider()).isEqualTo(AuthProvider.LOCAL);
    }

    @Test
    void 중복된이메일로_회원가입_불가() {
        // given
        SignUp signUp1 = MemberFixture.createSignUpWithEmail("dup@test.com");
        memberService.signUp(signUp1);
        SignUp signUp2 = MemberFixture.createSignUpWithEmail("dup@test.com");

        // when, then
        assertThatThrownBy(() -> memberService.signUp(signUp2))
                .isInstanceOf(CoreException.class);
    }

}