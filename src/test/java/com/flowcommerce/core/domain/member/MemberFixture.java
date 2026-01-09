package com.flowcommerce.core.domain.member;

public class MemberFixture {
    public static SignUp createSignUp() {
        return new SignUp("test@test.com", "Password1!", "test");
    }

    public static SignUp createSignUp(String email, String password, String name) {
        return new SignUp(email, password, name);
    }

    public static SignUp createSignUpWithEmail(String email) {
        return new SignUp(email, "Password1!", "test");
    }
}
