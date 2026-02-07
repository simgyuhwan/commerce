package com.flowcommerce.core.api.v1.member

import com.fasterxml.jackson.databind.ObjectMapper
import com.flowcommerce.core.api.ApiControllerAdvice
import com.flowcommerce.core.api.v1.member.request.SignUpRequest
import com.flowcommerce.core.domain.member.SignUp
import com.flowcommerce.core.domain.member.service.MemberService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [AuthController::class, ApiControllerAdvice::class])
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("회원가입 성공")
    fun signUpSuccess() {
        // Given
        val request = SignUpRequest("user@example.com", "Password123!", "홍길동")

        every { memberService.signUp(any<SignUp>()) } returns 1L

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultType").value("SUCCESS"))
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 입력값")
    fun signUpInvalidInput() {
        // Given
        val invalidRequest = SignUpRequest("", "", "")

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.resultType").value("ERROR"))
            .andExpect(jsonPath("$.errorMessage").exists())
    }
}
