package com.flowcommerce.core.support.error

data class ErrorMessage<T>(
    val code: String,
    val message: String,
    val data: T?
) {
    constructor(errorType: ErrorType, data: T?) : this(
        code = errorType.code.name,
        message = errorType.message,
        data = data
    )

    constructor(errorType: ErrorType) : this(errorType, null)
}
