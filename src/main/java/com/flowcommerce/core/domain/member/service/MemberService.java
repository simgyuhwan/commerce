package com.flowcommerce.core.domain.member.service;

import com.flowcommerce.core.domain.member.MemberPolicyValidator;
import com.flowcommerce.core.domain.member.SignUp;
import com.flowcommerce.core.storage.member.MemberEntity;
import com.flowcommerce.core.storage.member.MemberRepository;
import com.flowcommerce.core.support.auth.PasswordEncoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberPolicyValidator memberPolicyValidator;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public Long signUp(SignUp signUp) {
        memberPolicyValidator.validateNew(signUp);

        return memberRepository.save(
                MemberEntity.forLocalSignUp(
                        signUp.email(),
                        passwordEncoder.encode(signUp.password()),
                        signUp.name()
                )
        ).getId();
    }
}