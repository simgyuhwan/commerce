package com.flowcommerce.core.domain.member.service;

import com.flowcommerce.core.IntegrationTestSupport;
import com.flowcommerce.core.domain.User;
import com.flowcommerce.core.domain.member.*;
import com.flowcommerce.core.storage.member.*;
import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import com.flowcommerce.core.support.utils.DocumentService;
import com.flowcommerce.core.support.utils.FileInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SellerServiceTest extends IntegrationTestSupport {

    @Autowired
    SellerService sellerService;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    SellerHistoryRepository sellerHistoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @MockitoBean
    AccountVerificationService accountVerificationService;

    @MockitoBean
    DocumentService documentService;

    private User testUser;
    private MockMultipartFile businessLicense;
    private MockMultipartFile bankBook;
    private MockMultipartFile onlineSalesReport;

    @BeforeEach
    void setUp() throws IOException {
        MemberEntity member = memberRepository.save(
                MemberEntity.forLocalSignUp("seller@test.com", "encodedPassword", "판매자")
        );
        testUser = new User(member.getId());

        businessLicense = new MockMultipartFile(
                "businessLicense",
                "business.pdf",
                "application/pdf",
                "business license content".getBytes()
        );
        bankBook = new MockMultipartFile(
                "bankBook",
                "bankbook.pdf",
                "application/pdf",
                "bankbook content".getBytes()
        );
        onlineSalesReport = new MockMultipartFile(
                "onlineSalesReport",
                "sales.pdf",
                "application/pdf",
                "online sales report content".getBytes()
        );

        given(accountVerificationService.verify(anyString(), anyString(), anyString()))
                .willReturn(true);

        given(documentService.save(anyString(), any()))
                .willReturn(new FileInfo("uuid-file.pdf", "seller/1/BUSINESS_LICENSE/uuid-file.pdf", 1024L));
    }

    @AfterEach
    void afterEach()  {
        sellerHistoryRepository.deleteAll();
        sellerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 판매자_신청_성공() throws IOException {
        // given
        SellerApply sellerApply = SellerFixture.createSellerApply();

        // when
        Long sellerId = sellerService.apply(testUser, sellerApply, businessLicense, bankBook, onlineSalesReport);

        // then
        SellerEntity savedSeller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new AssertionError("판매자가 저장되지 않았습니다."));

        assertThat(savedSeller.getMemberId()).isEqualTo(testUser.id());
        assertThat(savedSeller.getSellerName()).isEqualTo(sellerApply.getSellerName());
        assertThat(savedSeller.getSellerStatus()).isEqualTo(SellerStatus.PENDING);
        assertThat(savedSeller.getBusinessNumber()).isEqualTo(sellerApply.getBusinessNumber());

        // 계좌 검증이 호출되었는지 확인
        verify(accountVerificationService).verify(
                sellerApply.getAccountHolder(),
                sellerApply.getBankName(),
                sellerApply.getAccountNumber()
        );

        // 파일이 3번 저장되었는지 확인 (사업자등록증, 통장사본, 통신판매업신고증)
        verify(documentService, times(3)).save(anyString(), any());

        // 히스토리가 생성되었는지 확인
        assertThat(sellerHistoryRepository.findAll()).hasSize(1);
    }

    @Test
    void 중복_판매자명_예외() {
        // given
        SellerApply firstApply = SellerFixture.createSellerApply("중복셀러");
        sellerService.apply(testUser, firstApply, businessLicense, bankBook, onlineSalesReport);

        // 다른 회원으로 같은 판매자명 신청
        MemberEntity anotherMember = memberRepository.save(
                MemberEntity.forLocalSignUp("another@test.com", "password", "다른사용자")
        );
        User anotherUser = new User(anotherMember.getId());
        SellerApply duplicateApply = SellerFixture.createSellerApply("중복셀러");

        // when & then
        assertThatThrownBy(() ->
                sellerService.apply(anotherUser, duplicateApply, businessLicense, bankBook, onlineSalesReport)
        )
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.SELLER_NAME_DUPLICATE);
    }

    @Test
    void 중복_사업자등록번호_예외() {
        // given
        SellerApply firstApply = SellerFixture.createSellerApply("123-45-67890","seller1");
        sellerService.apply(testUser, firstApply, businessLicense, bankBook, onlineSalesReport);

        // 다른 회원으로 같은 사업자등록번호 신청
        MemberEntity anotherMember = memberRepository.save(
                MemberEntity.forLocalSignUp("another@test.com", "password", "다른사용자")
        );
        User anotherUser = new User(anotherMember.getId());
        SellerApply duplicateApply = SellerFixture.createSellerApply("123-45-67890", "seller2");

        // when & then
        assertThatThrownBy(() ->
                sellerService.apply(anotherUser, duplicateApply, businessLicense, bankBook, onlineSalesReport)
        )
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.BUSINESS_NUMBER_DUPLICATE);
    }

    @Test
    void 계좌_검증_실패_예외() {
        // given
        SellerApply sellerApply = SellerFixture.createSellerApply();
        given(accountVerificationService.verify(anyString(), anyString(), anyString()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() ->
                sellerService.apply(testUser, sellerApply, businessLicense, bankBook, onlineSalesReport)
        )
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ACCOUNT_VERIFICATION_FAILED);

        // 검증 실패 시 DB에 저장되지 않음 (@Transactional 롤백)
        assertThat(sellerRepository.findAll()).isEmpty();
    }

    @Test
    void 이미_신청_대기중인_판매자_예외() {
        // given
        SellerApply firstApply = SellerFixture.createSellerApply();
        sellerService.apply(testUser, firstApply, businessLicense, bankBook, onlineSalesReport);

        // 같은 회원이 다시 신청
        SellerApply secondApply = SellerFixture.createSellerApply("다른셀러명");

        // when & then
        assertThatThrownBy(() ->
                sellerService.apply(testUser, secondApply, businessLicense, bankBook, onlineSalesReport)
        )
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.SELLER_APPLICATION_PENDING);
    }

    @Test
    void 이미_승인된_판매자_예외() {
        // given
        sellerRepository.save(
                new SellerEntity(
                        testUser.id(),
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
                        java.math.BigDecimal.valueOf(0.15),
                        SettlementBasis.PURCHASE_CONFIRMED,
                        1L,
                        java.time.LocalDateTime.now()
                )
        );

        SellerApply newApply = SellerFixture.createSellerApply();

        // when & then
        assertThatThrownBy(() ->
                sellerService.apply(testUser, newApply, businessLicense, bankBook, onlineSalesReport)
        )
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.SELLER_ALREADY_EXISTS);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 파일_저장_실패_시_롤백() throws IOException {
        // given
        SellerApply sellerApply = SellerFixture.createSellerApply();
        given(documentService.save(anyString(), any()))
                .willThrow(new IOException("파일 저장 실패"));

        // when & then
        assertThatThrownBy(() ->
                sellerService.apply(testUser, sellerApply, businessLicense, bankBook, onlineSalesReport)
        )
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.FILE_SAVE_FAILED);

        assertThat(sellerRepository.findAll()).isEmpty();
        assertThat(sellerHistoryRepository.findAll()).isEmpty();
    }
}
