package com.livecommerce.common.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val code: ErrorCode,
    val message: String,
    val logLevel: LogLevel
) {
    // 공통
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "알 수 없는 오류가 발생했습니다.", LogLevel.ERROR),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 요청입니다.", LogLevel.WARN),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "인증이 필요합니다.", LogLevel.WARN),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E403, "접근 권한이 없습니다.", LogLevel.WARN),
    NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "리소스를 찾을 수 없습니다.", LogLevel.WARN),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.E405, "허용되지 않는 HTTP 메서드입니다.", LogLevel.WARN),
    CONFLICT(HttpStatus.CONFLICT, ErrorCode.E409, "이미 존재하는 리소스입니다.", LogLevel.WARN),
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.E422, "입력값이 올바르지 않습니다.", LogLevel.WARN),
}