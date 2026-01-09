package com.flowcommerce.core.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Member{
    private Long id;
    private String email;
    private String password;
    private String name;
    private AuthProvider authProvider;
    private String providerId;
    private MemberStatus status;

    private Member(String name, String email, String password, MemberStatus status, AuthProvider authProvider) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
        this.authProvider = authProvider;
    }

    public static Member create(String name, String email, String password) {
        return new Member(name, email, password, MemberStatus.ACTIVE, AuthProvider.LOCAL);
    }
}
