package com.flowcommerce.core.domain.member.service

import com.flowcommerce.core.domain.member.MemberPolicyValidator
import com.flowcommerce.core.domain.member.SignUp
import com.flowcommerce.core.storage.member.MemberEntity
import com.flowcommerce.core.storage.member.MemberRepository
import com.flowcommerce.core.support.auth.PasswordEncoder
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberPolicyValidator: MemberPolicyValidator,
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository
) {
    @Transactional
    fun signUp(signUp: SignUp): Long {
        memberPolicyValidator.validateNew(signUp)

        return memberRepository.save(
            MemberEntity.forLocalSignUp(
                signUp.email,
                passwordEncoder.encode(signUp.password),
                signUp.name
            )
        ).id
    }
}
