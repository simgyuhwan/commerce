package com.flowcommerce.core.domain.member

import org.springframework.stereotype.Service

@Service
class AccountVerificationService {
    // TODO 금융원 API 계좌 실명 검증
    fun verify(accountHolder: String, bankName: String, accountNumber: String): Boolean = true
}
