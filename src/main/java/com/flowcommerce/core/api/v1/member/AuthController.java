package com.flowcommerce.core.api.v1.member;

import com.flowcommerce.core.api.v1.member.request.SignUpRequest;
import com.flowcommerce.core.domain.member.service.MemberService;
import com.flowcommerce.core.support.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "회원가입, 로그인 등 인증 관련 API")
@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        memberService.signUp(request.toSignUp());
        return ApiResponse.success();
    }
}
