package com.flowcommerce.core.api.v1.member.request;

import com.flowcommerce.core.domain.member.SignUp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @Schema(description = "이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호 (8자 이상, 영문/숫자/특수문자", example =  "Password123!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    public SignUp toSignUp() {
        return new SignUp(email, password, name);
    }

}
