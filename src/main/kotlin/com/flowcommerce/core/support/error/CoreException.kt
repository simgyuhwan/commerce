package com.flowcommerce.core.support.error

class CoreException @JvmOverloads constructor(
    val errorType: ErrorType,
    val data: Any? = null
) : RuntimeException(errorType.message)
