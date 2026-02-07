package com.flowcommerce.core.domain.member

import com.flowcommerce.core.storage.member.MemberRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

@Component
class MemberPolicyValidator(
    private val memberRepository: MemberRepository
) {
    companion object {
        private const val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$"
    }

    fun validateNew(signUp: SignUp) {
        if (memberRepository.existsByEmail(signUp.email)) {
            throw CoreException(ErrorType.DUPLICATE_EMAIL)
        }
        validatePasswordPolicy(signUp.password)
    }

    private fun validatePasswordPolicy(password: String) {
        if (!StringUtils.hasText(password)) {
            throw CoreException(ErrorType.INVALID_PASSWORD_POLICY)
        }
        if (!password.matches(Regex(PASSWORD_PATTERN))) {
            throw CoreException(ErrorType.INVALID_PASSWORD_POLICY)
        }
    }
}
