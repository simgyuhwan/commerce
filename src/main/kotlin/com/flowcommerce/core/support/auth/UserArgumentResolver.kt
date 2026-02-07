package com.flowcommerce.core.support.auth

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class UserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw CoreException(ErrorType.INVALID_REQUEST)

        val userIdHeader = request.getHeader("X-User-Id")

        if (userIdHeader.isNullOrBlank()) {
            throw CoreException(ErrorType.UNAUTHORIZED)
        }

        return try {
            val userId = userIdHeader.toLong()
            User(userId)
        } catch (e: NumberFormatException) {
            throw CoreException(ErrorType.INVALID_REQUEST)
        }
    }
}
