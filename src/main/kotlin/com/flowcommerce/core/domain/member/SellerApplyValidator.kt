package com.flowcommerce.core.domain.member

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.storage.member.SellerRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class SellerApplyValidator(
    private val sellerRepository: SellerRepository,
    private val accountVerificationService: AccountVerificationService
) {
    fun validateApply(user: User, apply: SellerApply) {
        validateExistingSeller(user.id)
        validateDuplicate(apply)
        validateAccount(apply)
    }

    private fun validateAccount(apply: SellerApply) {
        if (!accountVerificationService.verify(
                apply.accountHolder,
                apply.bankName,
                apply.accountNumber
            )
        ) {
            throw CoreException(ErrorType.ACCOUNT_VERIFICATION_FAILED)
        }
    }

    private fun validateDuplicate(apply: SellerApply) {
        if (sellerRepository.existsBySellerName(apply.sellerName)) {
            throw CoreException(ErrorType.SELLER_NAME_DUPLICATE)
        }
        if (sellerRepository.existsByBusinessNumber(apply.businessNumber)) {
            throw CoreException(ErrorType.BUSINESS_NUMBER_DUPLICATE)
        }
    }

    private fun validateExistingSeller(memberId: Long) {
        val sellerEntity = sellerRepository.findByMemberId(memberId) ?: return

        if (sellerEntity.sellerStatus == SellerStatus.PENDING) {
            throw CoreException(ErrorType.SELLER_APPLICATION_PENDING)
        }
        if (sellerEntity.sellerStatus == SellerStatus.APPROVED) {
            throw CoreException(ErrorType.SELLER_ALREADY_EXISTS)
        }
    }
}
