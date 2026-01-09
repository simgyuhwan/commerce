package com.flowcommerce.core.api.v1.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowcommerce.core.api.ApiControllerAdvice;
import com.flowcommerce.core.api.v1.member.request.SignUpRequest;
import com.flowcommerce.core.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, ApiControllerAdvice.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() throws Exception {
        // Given
        SignUpRequest request = new SignUpRequest("user@example.com", "Password123!", "홍길동");

        when(memberService.signUp(Mockito.any())).thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultType").value("SUCCESS"));
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 입력값")
    void signUpInvalidInput() throws Exception {
        // Given
        SignUpRequest invalidRequest = new SignUpRequest("", "", "");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultType").value("ERROR"))
                .andExpect(jsonPath("$.errorMessage").exists());
    }
}