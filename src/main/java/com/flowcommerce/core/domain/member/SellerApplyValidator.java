package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.domain.User;
import com.flowcommerce.core.storage.member.SellerEntity;
import com.flowcommerce.core.storage.member.SellerRepository;
import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellerApplyValidator {
    private final SellerRepository sellerRepository;
    private final AccountVerificationService accountVerificationService;

    public void validateApply(User user, SellerApply apply) {
        validateExistingSeller(user.id());
        validateDuplicate(apply);
        validateAccount(apply);
    }

    private void validateAccount(SellerApply apply) {
        if(!accountVerificationService.verify(
                apply.getAccountHolder(),
                apply.getBankName(),
                apply.getAccountNumber()
        )) {
            throw new CoreException(ErrorType.ACCOUNT_VERIFICATION_FAILED);
        }
    }

    private void validateDuplicate(SellerApply apply) {
        if (sellerRepository.existsBySellerName(apply.getSellerName())) {
            throw new CoreException(ErrorType.SELLER_NAME_DUPLICATE);
        }
        
        if (sellerRepository.existsByBusinessNumber(apply.getBusinessNumber())) {
            throw new CoreException(ErrorType.BUSINESS_NUMBER_DUPLICATE);
        }
    }

    private void validateExistingSeller(Long memberId) {
        SellerEntity sellerEntity = sellerRepository.findByMemberId(memberId);
        if (sellerEntity == null) {
            return;
        }

        if (sellerEntity.getSellerStatus() == SellerStatus.PENDING) {
            throw new CoreException(ErrorType.SELLER_APPLICATION_PENDING);
        }

        if (sellerEntity.getSellerStatus() == SellerStatus.APPROVED) {
            throw new CoreException(ErrorType.SELLER_ALREADY_EXISTS);
        }
    }
}
