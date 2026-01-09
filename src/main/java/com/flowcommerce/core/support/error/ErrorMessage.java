package com.flowcommerce.core.support.error;

public record ErrorMessage<T>(String code, String message, T data) {

    public ErrorMessage(ErrorType errorType, T data) {
        this(errorType.getCode().name(), errorType.getMessage(), data);
    }

    public ErrorMessage(ErrorType errorType) {
        this(errorType, null);
    }
}
