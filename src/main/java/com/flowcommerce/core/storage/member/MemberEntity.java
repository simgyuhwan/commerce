package com.flowcommerce.core.storage.member;

import com.flowcommerce.core.domain.member.AuthProvider;
import com.flowcommerce.core.domain.member.MemberStatus;
import com.flowcommerce.core.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "member_status")
    private MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    private String providerId;

    public static MemberEntity forLocalSignUp(String email, String encodedPassword, String name) {
        return new MemberEntity(
                email,
                encodedPassword,
                name,
                MemberStatus.ACTIVE,
                AuthProvider.LOCAL,
                null
        );
    }
}
