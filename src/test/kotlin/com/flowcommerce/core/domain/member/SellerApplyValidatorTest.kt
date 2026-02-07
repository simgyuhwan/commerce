package com.flowcommerce.core.domain.member

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.storage.member.SellerRepository
import com.flowcommerce.core.support.error.CoreException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.BDDMockito.given
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SellerApplyValidatorTest {

    @Mock
    lateinit var sellerRepository: SellerRepository

    @Mock
    lateinit var accountVerificationService: AccountVerificationService

    @InjectMocks
    lateinit var sellerApplyValidator: SellerApplyValidator

    @Test
    fun `정상 판매자 신청 검증 성공`() {
        // given
        val user = User(1L)
        val sellerApply = SellerApplyFixture.createDefault()

        given(sellerRepository.findByMemberId(user.id)).willReturn(null)
        given(sellerRepository.existsBySellerName(sellerApply.sellerName)).willReturn(false)
        given(sellerRepository.existsByBusinessNumber(sellerApply.businessNumber)).willReturn(false)
        given(accountVerificationService.verify(
            sellerApply.accountHolder,
            sellerApply.bankName,
            sellerApply.accountNumber
        )).willReturn(true)

        // when, then
        assertDoesNotThrow { sellerApplyValidator.validateApply(user, sellerApply) }
    }

    @Test
    fun `이미 승인된 판매자일 경우 신청실패`() {
        // given
        val user = User(1L)
        val sellerApply = SellerApplyFixture.createDefault()
        val seller = SellerEntityFixture.createWithStatus(SellerStatus.APPROVED)

        given(sellerRepository.findByMemberId(user.id)).willReturn(seller)

        // when, then
        assertThatThrownBy { sellerApplyValidator.validateApply(user, sellerApply) }
            .isInstanceOf(CoreException::class.java)
    }

    @Test
    fun `판매자 신청대기중인 판매자일 경우 신청실패`() {
        // given
        val user = User(1L)
        val sellerApply = SellerApplyFixture.createDefault()
        val seller = SellerEntityFixture.createWithStatus(SellerStatus.PENDING)

        given(sellerRepository.findByMemberId(user.id)).willReturn(seller)

        // when, then
        assertThatThrownBy { sellerApplyValidator.validateApply(user, sellerApply) }
            .isInstanceOf(CoreException::class.java)
    }

    @Test
    fun `계좌정보가 일치하지 않을경우 신청실패`() {
        // given
        val user = User(1L)
        val sellerApply = SellerApplyFixture.createDefault()

        given(sellerRepository.findByMemberId(user.id)).willReturn(null)
        given(sellerRepository.existsBySellerName(sellerApply.sellerName)).willReturn(false)
        given(sellerRepository.existsByBusinessNumber(sellerApply.businessNumber)).willReturn(false)
        given(accountVerificationService.verify(
            sellerApply.accountHolder,
            sellerApply.bankName,
            sellerApply.accountNumber
        )).willReturn(false)

        // when, then
        assertThatThrownBy { sellerApplyValidator.validateApply(user, sellerApply) }
            .isInstanceOf(CoreException::class.java)
    }

    @Test
    fun `판매자명 중복일때 신청실패`() {
        // given
        val user = User(1L)
        val sellerApply = SellerApplyFixture.createDefault()

        given(sellerRepository.findByMemberId(user.id)).willReturn(null)
        given(sellerRepository.existsBySellerName(sellerApply.sellerName)).willReturn(true)

        // when, then
        assertThatThrownBy { sellerApplyValidator.validateApply(user, sellerApply) }
            .isInstanceOf(CoreException::class.java)
    }

    @Test
    fun `사업자번호 중복일때 신청실패`() {
        // given
        val user = User(1L)
        val sellerApply = SellerApplyFixture.createDefault()

        given(sellerRepository.findByMemberId(user.id)).willReturn(null)
        given(sellerRepository.existsBySellerName(sellerApply.sellerName)).willReturn(false)
        given(sellerRepository.existsByBusinessNumber(sellerApply.businessNumber)).willReturn(true)

        // when, then
        assertThatThrownBy { sellerApplyValidator.validateApply(user, sellerApply) }
            .isInstanceOf(CoreException::class.java)
    }
}
