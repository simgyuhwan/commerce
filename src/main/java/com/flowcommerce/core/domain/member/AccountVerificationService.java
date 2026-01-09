package com.flowcommerce.core.domain.member;

import org.springframework.stereotype.Service;

@Service
public class AccountVerificationService {
    // TODO 금융원 API 계좌 실명 검증
    public boolean verify(String accountHolder, String bankName, String accountNumber) {
        return true;
    }
}
