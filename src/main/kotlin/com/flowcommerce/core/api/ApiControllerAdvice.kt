package com.flowcommerce.core.api

import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import com.flowcommerce.core.support.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(e: CoreException): ResponseEntity<ApiResponse<Any>> {
        when (e.errorType.logLevel) {
            LogLevel.ERROR -> log.error("CoreException : {}", e.message)
            LogLevel.WARN -> log.warn("CoreException: {}", e.message)
            else -> log.info("CoreException: {}", e.message)
        }
        return ResponseEntity(
            ApiResponse.error(e.errorType, e.data),
            e.errorType.status
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Any>> {
        log.error("Exception : {}", e.message)
        return ResponseEntity(
            ApiResponse.error(ErrorType.DEFAULT_ERROR),
            ErrorType.DEFAULT_ERROR.status
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        log.warn("MethodArgumentNotValidException : {}", e.message)
        return ResponseEntity(
            ApiResponse.error(
                ErrorType.INVALID_INPUT_VALUE,
                e.bindingResult.allErrors.firstOrNull()?.defaultMessage
            ),
            ErrorType.INVALID_INPUT_VALUE.status
        )
    }
}
