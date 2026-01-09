package com.flowcommerce.core.support.auth;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class PasswordEncoder {

    // TODO 암호화 알고리즘 적용해야함
    public String encode(String password) {
        return password;
    }

    public boolean matches(String password, String encodedPassword) {
        return password.equals(encodedPassword);
    }
}
