package com.flowcommerce.core.domain.member.service

import com.flowcommerce.core.IntegrationTestSupport
import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.member.AccountVerificationService
import com.flowcommerce.core.domain.member.SellerFixture
import com.flowcommerce.core.domain.member.SellerStatus
import com.flowcommerce.core.domain.member.SettlementBasis
import com.flowcommerce.core.storage.member.MemberEntity
import com.flowcommerce.core.storage.member.MemberRepository
import com.flowcommerce.core.storage.member.SellerEntity
import com.flowcommerce.core.storage.member.SellerHistoryRepository
import com.flowcommerce.core.storage.member.SellerRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import com.flowcommerce.core.support.utils.DocumentService
import com.flowcommerce.core.support.utils.FileInfo
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDateTime

class SellerServiceTest : IntegrationTestSupport() {

    @Autowired
    lateinit var sellerService: SellerService

    @Autowired
    lateinit var sellerRepository: SellerRepository

    @Autowired
    lateinit var sellerHistoryRepository: SellerHistoryRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @MockkBean
    lateinit var accountVerificationService: AccountVerificationService

    @MockkBean
    lateinit var documentService: DocumentService

    private lateinit var testUser: User
    private lateinit var businessLicense: MockMultipartFile
    private lateinit var bankBook: MockMultipartFile
    private lateinit var onlineSalesReport: MockMultipartFile

    @BeforeEach
    fun setUp() {
        val member = memberRepository.save(
            MemberEntity.forLocalSignUp("seller@test.com", "encodedPassword", "판매자")
        )
        testUser = User(member.id)

        businessLicense = MockMultipartFile(
            "businessLicense",
            "business.pdf",
            "application/pdf",
            "business license content".toByteArray()
        )
        bankBook = MockMultipartFile(
            "bankBook",
            "bankbook.pdf",
            "application/pdf",
            "bankbook content".toByteArray()
        )
        onlineSalesReport = MockMultipartFile(
            "onlineSalesReport",
            "sales.pdf",
            "application/pdf",
            "online sales report content".toByteArray()
        )

        every { accountVerificationService.verify(any(), any(), any()) } returns true

        every { documentService.save(any(), any<MultipartFile>()) } returns
            FileInfo("uuid-file.pdf", "seller/1/BUSINESS_LICENSE/uuid-file.pdf", 1024L)
    }

    @AfterEach
    fun afterEach() {
        sellerHistoryRepository.deleteAll()
        sellerRepository.deleteAll()
        memberRepository.deleteAll()
    }

    @Test
    fun `판매자 신청 성공`() {
        // given
        val sellerApply = SellerFixture.createSellerApply()

        // when
        val sellerId = sellerService.apply(testUser, sellerApply, businessLicense, bankBook, onlineSalesReport)

        // then
        val savedSeller = sellerRepository.findById(sellerId)
            .orElseThrow { AssertionError("판매자가 저장되지 않았습니다.") }

        assertThat(savedSeller.memberId).isEqualTo(testUser.id)
        assertThat(savedSeller.sellerName).isEqualTo(sellerApply.sellerName)
        assertThat(savedSeller.sellerStatus).isEqualTo(SellerStatus.PENDING)
        assertThat(savedSeller.businessNumber).isEqualTo(sellerApply.businessNumber)

        // 계좌 검증이 호출되었는지 확인
        verify {
            accountVerificationService.verify(
                sellerApply.accountHolder,
                sellerApply.bankName,
                sellerApply.accountNumber
            )
        }

        // 파일이 3번 저장되었는지 확인 (사업자등록증, 통장사본, 통신판매업신고증)
        verify(exactly = 3) { documentService.save(any(), any<MultipartFile>()) }

        // 히스토리가 생성되었는지 확인
        assertThat(sellerHistoryRepository.findAll()).hasSize(1)
    }

    @Test
    fun `중복 판매자명 예외`() {
        // given
        val firstApply = SellerFixture.createSellerApply("중복셀러")
        sellerService.apply(testUser, firstApply, businessLicense, bankBook, onlineSalesReport)

        // 다른 회원으로 같은 판매자명 신청
        val anotherMember = memberRepository.save(
            MemberEntity.forLocalSignUp("another@test.com", "password", "다른사용자")
        )
        val anotherUser = User(anotherMember.id)
        val duplicateApply = SellerFixture.createSellerApply("중복셀러")

        // when & then
        assertThatThrownBy {
            sellerService.apply(anotherUser, duplicateApply, businessLicense, bankBook, onlineSalesReport)
        }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.SELLER_NAME_DUPLICATE)
    }

    @Test
    fun `중복 사업자등록번호 예외`() {
        // given
        val firstApply = SellerFixture.createSellerApply("123-45-67890", "seller1")
        sellerService.apply(testUser, firstApply, businessLicense, bankBook, onlineSalesReport)

        // 다른 회원으로 같은 사업자등록번호 신청
        val anotherMember = memberRepository.save(
            MemberEntity.forLocalSignUp("another@test.com", "password", "다른사용자")
        )
        val anotherUser = User(anotherMember.id)
        val duplicateApply = SellerFixture.createSellerApply("123-45-67890", "seller2")

        // when & then
        assertThatThrownBy {
            sellerService.apply(anotherUser, duplicateApply, businessLicense, bankBook, onlineSalesReport)
        }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.BUSINESS_NUMBER_DUPLICATE)
    }

    @Test
    fun `계좌 검증 실패 예외`() {
        // given
        val sellerApply = SellerFixture.createSellerApply()
        every { accountVerificationService.verify(any(), any(), any()) } returns false

        // when & then
        assertThatThrownBy {
            sellerService.apply(testUser, sellerApply, businessLicense, bankBook, onlineSalesReport)
        }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.ACCOUNT_VERIFICATION_FAILED)

        // 검증 실패 시 DB에 저장되지 않음 (@Transactional 롤백)
        assertThat(sellerRepository.findAll()).isEmpty()
    }

    @Test
    fun `이미 신청 대기중인 판매자 예외`() {
        // given
        val firstApply = SellerFixture.createSellerApply()
        sellerService.apply(testUser, firstApply, businessLicense, bankBook, onlineSalesReport)

        // 같은 회원이 다시 신청
        val secondApply = SellerFixture.createSellerApply("다른셀러명")

        // when & then
        assertThatThrownBy {
            sellerService.apply(testUser, secondApply, businessLicense, bankBook, onlineSalesReport)
        }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.SELLER_APPLICATION_PENDING)
    }

    @Test
    fun `이미 승인된 판매자 예외`() {
        // given
        sellerRepository.save(
            SellerEntity(
                testUser.id,
                "기존셀러",
                SellerStatus.APPROVED,
                "테스트컴퍼니",
                "999-99-99999",
                "2023-서울-9999",
                "도소매업",
                "02-9999-9999",
                "서울시 강남구",
                "홍길동",
                "010-9999-9999",
                "contact@test.com",
                "홍길동",
                "신한은행",
                "110-999-999999",
                BigDecimal.valueOf(0.15),
                SettlementBasis.PURCHASE_CONFIRMED,
                1L,
                LocalDateTime.now()
            )
        )

        val newApply = SellerFixture.createSellerApply()

        // when & then
        assertThatThrownBy {
            sellerService.apply(testUser, newApply, businessLicense, bankBook, onlineSalesReport)
        }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.SELLER_ALREADY_EXISTS)
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun `파일 저장 실패 시 롤백`() {
        // given
        val sellerApply = SellerFixture.createSellerApply()
        every { documentService.save(any(), any<MultipartFile>()) } throws IOException("파일 저장 실패")

        // when & then
        assertThatThrownBy {
            sellerService.apply(testUser, sellerApply, businessLicense, bankBook, onlineSalesReport)
        }
            .isInstanceOf(CoreException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.FILE_SAVE_FAILED)

        assertThat(sellerRepository.findAll()).isEmpty()
        assertThat(sellerHistoryRepository.findAll()).isEmpty()
    }
}
