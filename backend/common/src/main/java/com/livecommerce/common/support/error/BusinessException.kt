package com.livecommerce.common.support.error

class BusinessException(
    val errorType: ErrorType,
    val data: Any? = null
): RuntimeException(errorType.message) {

}