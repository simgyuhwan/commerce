package com.livecommerce.common.support.error

import com.livecommerce.common.support.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<*>> {
        val errorType = e.errorType
        when (errorType.logLevel) {
            LogLevel.ERROR -> log.error("BusinessException : {}", errorType.message, e)
            LogLevel.WARN -> log.warn("BusinessException : {}", errorType.message, e)
            else -> log.info("BusinessException : {}", errorType.message, e)
        }
        return ResponseEntity
            .status(errorType.status)
            .body(ApiResponse.error<Nothing>(errorType, e.data))
    }
    // 2. @Valid 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<*>> {
        log.warn("ValidationException: {}", e.message)

        // 필드 에러 목록을 data로 내려주는 게 프론트에 유용
        val errors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "invalid value")
        }
        return ResponseEntity
            .status(ErrorType.VALIDATION_ERROR.status)
            .body(ApiResponse.error<Nothing>(ErrorType.VALIDATION_ERROR, errors))
    }

    // 3. 허용되지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<*>> {
        log.warn("MethodNotAllowed: {}", e.message)
        return ResponseEntity
            .status(ErrorType.METHOD_NOT_ALLOWED.status)
            .body(ApiResponse.error<Nothing>(ErrorType.METHOD_NOT_ALLOWED))
    }

    // 4. 그 외 모든 예외
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<*>> {
        log.error("Unexpected Exception: {}", e.message, e)
        return ResponseEntity
            .status(ErrorType.DEFAULT_ERROR.status)
            .body(ApiResponse.error<Nothing>(ErrorType.DEFAULT_ERROR))
    }
}