package com.flowcommerce.core.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void 일반_회원가입이_가능하다() {
        // given
        String email = "test@test.net";
        String password = "Password!";
        String name = "first";

        // when
        Member member = Member.create(name, email, password);

        // then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getAuthProvider()).isEqualTo(AuthProvider.LOCAL);
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getProviderId()).isNull();
    }

}