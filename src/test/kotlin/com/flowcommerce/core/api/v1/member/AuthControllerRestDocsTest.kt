package com.flowcommerce.core.api.v1.member

import com.flowcommerce.core.RestDocsTestSupport
import com.flowcommerce.core.api.v1.member.request.SignUpRequest
import com.flowcommerce.core.domain.member.SignUp
import com.flowcommerce.core.domain.member.service.MemberService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("Auth API 문서화 테스트")
class AuthControllerRestDocsTest : RestDocsTestSupport() {

    @MockkBean
    private lateinit var memberService: MemberService

    @Test
    @DisplayName("회원가입 API")
    fun signUp() {
        // Given
        val request = SignUpRequest(
            "user@example.com",
            "Password123!",
            "홍길동"
        )

        every { memberService.signUp(any<SignUp>()) } returns 1L

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultType").value("SUCCESS"))
            .andDo(
                document(
                    "auth/signup",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일 (이메일 형식)"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호 (8자 이상, 영문/숫자/특수문자)"),
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("이름")
                    ),
                    responseFields(
                        fieldWithPath("resultType").type(JsonFieldType.STRING)
                            .description("결과 타입 (SUCCESS/ERROR)"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("응답 데이터 (회원가입은 데이터 없음)").optional(),
                        fieldWithPath("errorMessage").type(JsonFieldType.NULL)
                            .description("에러 메시지 (성공 시 null)").optional()
                    )
                )
            )
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 입력")
    fun signUpWithInvalidInput() {
        // Given
        val request = SignUpRequest(
            "invalid-email",
            "",
            ""
        )

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.resultType").value("ERROR"))
            .andDo(
                document(
                    "auth/signup-error",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("잘못된 형식의 이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("빈 비밀번호"),
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("빈 이름")
                    ),
                    responseFields(
                        fieldWithPath("resultType").type(JsonFieldType.STRING)
                            .description("결과 타입 (ERROR)"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                            .description("응답 데이터 (에러 시 null)").optional(),
                        fieldWithPath("errorMessage").type(JsonFieldType.OBJECT)
                            .description("에러 메시지 객체"),
                        fieldWithPath("errorMessage.code").type(JsonFieldType.STRING)
                            .description("에러 코드"),
                        fieldWithPath("errorMessage.message").type(JsonFieldType.STRING)
                            .description("에러 상세 메시지"),
                        fieldWithPath("errorMessage.data").type(JsonFieldType.VARIES)
                            .description("에러 관련 추가 데이터").optional()
                    )
                )
            )
    }
}
