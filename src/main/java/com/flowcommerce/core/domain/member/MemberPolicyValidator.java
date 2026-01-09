package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.storage.member.MemberRepository;
import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class MemberPolicyValidator {
    private final MemberRepository memberRepository;
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    public void validateNew(SignUp signUp){
        if(memberRepository.existsByEmail(signUp.email())){
            throw new CoreException(ErrorType.DUPLICATE_EMAIL);
        }

        validatePasswordPolicy(signUp.password());
    }

    private void validatePasswordPolicy(String password) {
        if(!StringUtils.hasText(password)) {
            throw new CoreException(ErrorType.INVALID_PASSWORD_POLICY);
        }

        if (!password.matches(PASSWORD_PATTERN)) {
            throw new CoreException(ErrorType.INVALID_PASSWORD_POLICY);
        }
    }
}
