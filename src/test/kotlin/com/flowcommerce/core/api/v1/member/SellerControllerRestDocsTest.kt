package com.flowcommerce.core.api.v1.member

import com.flowcommerce.core.RestDocsTestSupport
import com.flowcommerce.core.api.v1.member.request.SellerApplyRequest
import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.member.SellerApply
import com.flowcommerce.core.domain.member.SettlementBasis
import com.flowcommerce.core.domain.member.service.SellerService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

@DisplayName("Seller API 문서화 테스트")
class SellerControllerRestDocsTest : RestDocsTestSupport() {

    @MockkBean
    private lateinit var sellerService: SellerService

    @Test
    @DisplayName("판매자 신청 API")
    fun applySeller() {
        // Given
        val request = createSellerApplyRequest()
        val requestJson = objectMapper.writeValueAsString(request)

        val dataFile = MockMultipartFile(
            "data",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.toByteArray()
        )

        val businessLicense = MockMultipartFile(
            "businessLicense",
            "business-license.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Business License Content".toByteArray()
        )

        val bankbookCopy = MockMultipartFile(
            "bankbookCopy",
            "bankbook-copy.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Bankbook Copy Content".toByteArray()
        )

        val onlineSalesLicense = MockMultipartFile(
            "onlineSalesLicense",
            "online-sales-license.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Online Sales License Content".toByteArray()
        )

        every {
            sellerService.apply(
                any<User>(),
                any<SellerApply>(),
                any<MultipartFile>(),
                any<MultipartFile>(),
                any<MultipartFile>()
            )
        } returns 1L

        // When & Then
        mockMvc.perform(
            multipart("/api/v1/sellers/apply")
                .file(dataFile)
                .file(businessLicense)
                .file(bankbookCopy)
                .file(onlineSalesLicense)
                .header("X-User-Id", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultType").value("SUCCESS"))
            .andDo(
                document(
                    "seller/apply",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("data").description("판매자 신청 정보 (JSON)"),
                        partWithName("businessLicense").description("사업자등록증 파일"),
                        partWithName("bankbookCopy").description("통장사본 파일"),
                        partWithName("onlineSalesLicense").description("통신판매업신고증 파일")
                    ),
                    requestPartFields(
                        "data",
                        fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                            .description("회원 ID"),
                        fieldWithPath("sellerName").type(JsonFieldType.STRING)
                            .description("판매자명"),
                        fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("판매자 상태 (PENDING, APPROVED, REJECTED, BLOCKED)").optional(),
                        fieldWithPath("companyName").type(JsonFieldType.STRING)
                            .description("회사명"),
                        fieldWithPath("businessNumber").type(JsonFieldType.STRING)
                            .description("사업자등록번호"),
                        fieldWithPath("onlineSalesNumber").type(JsonFieldType.STRING)
                            .description("통신판매업신고번호"),
                        fieldWithPath("businessType").type(JsonFieldType.STRING)
                            .description("업종"),
                        fieldWithPath("companyPhone").type(JsonFieldType.STRING)
                            .description("회사 전화번호"),
                        fieldWithPath("address").type(JsonFieldType.STRING)
                            .description("주소"),
                        fieldWithPath("contactName").type(JsonFieldType.STRING)
                            .description("담당자명"),
                        fieldWithPath("contactPhone").type(JsonFieldType.STRING)
                            .description("담당자 전화번호"),
                        fieldWithPath("contactEmail").type(JsonFieldType.STRING)
                            .description("담당자 이메일"),
                        fieldWithPath("accountHolder").type(JsonFieldType.STRING)
                            .description("예금주"),
                        fieldWithPath("bankName").type(JsonFieldType.STRING)
                            .description("은행명"),
                        fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                            .description("계좌번호"),
                        fieldWithPath("commissionRate").type(JsonFieldType.NUMBER)
                            .description("수수료율 (0.0 ~ 100.0)"),
                        fieldWithPath("settlementBasis").type(JsonFieldType.STRING)
                            .description("정산 기준 (PURCHASE_CONFIRMED: 구매확정일, DELIVERY_COMPLETED: 발송완료일)")
                    )
                )
            )
    }

    @Test
    @DisplayName("판매자 신청 실패 - 유효하지 않은 입력")
    fun applySellerWithInvalidInput() {
        // Given - 필수 필드가 빈 값인 요청 (파싱은 가능하지만 유효성 검증 실패)
        val invalidRequest = SellerApplyRequest(
            memberId = null,
            sellerName = "",  // 빈 값 - @NotBlank 위반
            status = null,
            companyName = "",
            businessNumber = "",
            onlineSalesNumber = "",
            businessType = "",
            companyPhone = "",
            address = "",
            contactName = "",
            contactPhone = "",
            contactEmail = "",
            accountHolder = "",
            bankName = "",
            accountNumber = "",
            commissionRate = BigDecimal.ZERO,
            settlementBasis = SettlementBasis.PURCHASE_CONFIRMED
        )
        val requestJson = objectMapper.writeValueAsString(invalidRequest)

        val dataFile = MockMultipartFile(
            "data",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            requestJson.toByteArray()
        )

        val businessLicense = MockMultipartFile(
            "businessLicense",
            "business-license.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Business License Content".toByteArray()
        )

        val bankbookCopy = MockMultipartFile(
            "bankbookCopy",
            "bankbook-copy.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Bankbook Copy Content".toByteArray()
        )

        val onlineSalesLicense = MockMultipartFile(
            "onlineSalesLicense",
            "online-sales-license.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Online Sales License Content".toByteArray()
        )

        // When & Then
        mockMvc.perform(
            multipart("/api/v1/sellers/apply")
                .file(dataFile)
                .file(businessLicense)
                .file(bankbookCopy)
                .file(onlineSalesLicense)
                .header("X-User-Id", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.resultType").value("ERROR"))
            .andDo(
                document(
                    "seller/apply-error",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                )
            )
    }

    private fun createSellerApplyRequest(): SellerApplyRequest {
        return SellerApplyRequest(
            memberId = 1L,
            sellerName = "홍길동 셀러",
            status = null,
            companyName = "플로우커머스",
            businessNumber = "123-45-67890",
            onlineSalesNumber = "2024-서울강남-12345",
            businessType = "전자상거래",
            companyPhone = "02-1234-5678",
            address = "서울시 강남구 테헤란로 123",
            contactName = "홍길동",
            contactPhone = "010-1234-5678",
            contactEmail = "seller@example.com",
            accountHolder = "홍길동",
            bankName = "신한은행",
            accountNumber = "110-123-456789",
            commissionRate = BigDecimal("10.5"),
            settlementBasis = SettlementBasis.PURCHASE_CONFIRMED
        )
    }
}
