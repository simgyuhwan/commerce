package com.flowcommerce.core.support.response

import com.flowcommerce.core.support.error.ErrorMessage
import com.flowcommerce.core.support.error.ErrorType

data class ApiResponse<T>(
    val resultType: ResultType,
    val data: T?,
    val errorMessage: ErrorMessage<*>?
) {
    companion object {
        fun success(): ApiResponse<Void> =
            ApiResponse(ResultType.SUCCESS, null, null)

        fun <S> success(data: S): ApiResponse<S> =
            ApiResponse(ResultType.SUCCESS, data, null)

        fun <S> error(errorType: ErrorType, errorData: Any? = null): ApiResponse<S> =
            ApiResponse(ResultType.ERROR, null, ErrorMessage(errorType, errorData))
    }
}
