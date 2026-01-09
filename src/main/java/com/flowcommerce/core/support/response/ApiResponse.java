package com.flowcommerce.core.support.response;

import com.flowcommerce.core.support.error.ErrorMessage;
import com.flowcommerce.core.support.error.ErrorType;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final ResultType resultType;
    private final T data;
    private final ErrorMessage errorMessage;

    private ApiResponse(ResultType resultType, T data, ErrorMessage errorMessage) {
        this.resultType = resultType;
        this.data = data;
        this.errorMessage = errorMessage;
    }
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(ResultType.SUCCESS, null, null);
    }

    public static <S> ApiResponse<S> success(S data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static <S> ApiResponse<S> error(ErrorType errorType, Object errorData) {
        return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(errorType, errorData));
    }

    public static <S> ApiResponse<S> error(ErrorType errorType) {
        return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(errorType, null));
    }
}
