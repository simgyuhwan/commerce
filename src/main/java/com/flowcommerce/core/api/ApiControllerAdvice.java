package com.flowcommerce.core.api;

import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import com.flowcommerce.core.support.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<Object>> handleCoreException(CoreException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("CoreException : {}", e.getMessage());
            case WARN -> log.warn("CoreException: {}", e.getMessage());
            default -> log.info("CoreException: {}", e.getMessage());
        }
        return new ResponseEntity<>(
                ApiResponse.error(
                        e.getErrorType(), e.getData()
                ),
                e.getErrorType().getStatus()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e){
        log.error("Exception : {}", e.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(
                        ErrorType.DEFAULT_ERROR
                ),
                ErrorType.DEFAULT_ERROR.getStatus()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException : {}", e.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(
                        ErrorType.INVALID_INPUT_VALUE, // 적절한 에러 타입을 사용하세요 (예: 400 에러)
                        e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
                ),
                ErrorType.INVALID_INPUT_VALUE.getStatus()
        );
    }
}
