package com.flowcommerce.core.support.auth;

import com.flowcommerce.core.domain.User;
import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }

        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new CoreException(ErrorType.UNAUTHORIZED);
        }

        try {
            Long userId = Long.parseLong(userIdHeader);
            return new User(userId);
        } catch (NumberFormatException e) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
    }
}
