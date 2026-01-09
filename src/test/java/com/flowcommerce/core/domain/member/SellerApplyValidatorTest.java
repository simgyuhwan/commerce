package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.domain.User;
import com.flowcommerce.core.storage.member.SellerEntity;
import com.flowcommerce.core.storage.member.SellerRepository;
import com.flowcommerce.core.support.error.CoreException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SellerApplyValidatorTest {

    @Mock
    SellerRepository sellerRepository;

    @Mock
    AccountVerificationService accountVerificationService;

    @InjectMocks
    SellerApplyValidator sellerApplyValidator;

    @Test
    void 정상_판매자_신청_검증_성공() {
        // given
        User user = new User(1L);
        SellerApply sellerApply = SellerApplyFixture.createDefault();

        given(sellerRepository.findByMemberId(any())).willReturn(null);
        given(sellerRepository.existsBySellerName(any())).willReturn(false);
        given(sellerRepository.existsByBusinessNumber(any())).willReturn(false);
        given(accountVerificationService.verify(any(), any(), any())).willReturn(true);

        // when, then
        assertDoesNotThrow(() -> sellerApplyValidator.validateApply(user, sellerApply));
    }

    @Test
    void 이미_승인된_판매자일_경우_신청실패(){
        // given
        User user = new User(1L);
        SellerApply sellerApply = SellerApplyFixture.createDefault();
        SellerEntity seller = SellerEntityFixture.createWithStatus(SellerStatus.APPROVED);

        given(sellerRepository.findByMemberId(any())).willReturn(seller);

        // when, then
        assertThatThrownBy(() -> sellerApplyValidator.validateApply(user, sellerApply))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 판매자_신청대기중인_판매자일_경우_신청실패(){
        // given
        User user = new User(1L);
        SellerApply sellerApply = SellerApplyFixture.createDefault();
        SellerEntity seller = SellerEntityFixture.createWithStatus(SellerStatus.PENDING);

        given(sellerRepository.findByMemberId(any())).willReturn(seller);

        // when, then
        assertThatThrownBy(() -> sellerApplyValidator.validateApply(user, sellerApply))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 계좌정보가_일치하지_않을경우_신청실패() {
        // given
        User user = new User(1L);
        SellerApply sellerApply = SellerApplyFixture.createDefault();

        given(sellerRepository.findByMemberId(any())).willReturn(null);
        given(sellerRepository.existsBySellerName(any())).willReturn(false);
        given(sellerRepository.existsByBusinessNumber(any())).willReturn(false);
        given(accountVerificationService.verify(any(), any(), any())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> sellerApplyValidator.validateApply(user, sellerApply))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 판매자명_중복일때_신청실패() {
        // given
        User user = new User(1L);
        SellerApply sellerApply = SellerApplyFixture.createDefault();

        given(sellerRepository.findByMemberId(any())).willReturn(null);
        given(sellerRepository.existsBySellerName(any())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> sellerApplyValidator.validateApply(user, sellerApply))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 사업자번호_중복일때_신청실패() {
        // given
        User user = new User(1L);
        SellerApply sellerApply = SellerApplyFixture.createDefault();

        given(sellerRepository.findByMemberId(any())).willReturn(null);
        given(sellerRepository.existsBySellerName(any())).willReturn(false);
        given(sellerRepository.existsByBusinessNumber(any())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> sellerApplyValidator.validateApply(user, sellerApply))
                .isInstanceOf(CoreException.class);
    }
}