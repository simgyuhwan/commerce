package com.flowcommerce.core.domain.member

import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import java.math.BigDecimal

data class Commission(val rate: BigDecimal) {
    init {
        if (rate < BigDecimal.ZERO) {
            throw CoreException(ErrorType.COMMISSION_NEGATIVE)
        }
        if (rate > BigDecimal.ONE) {
            throw CoreException(ErrorType.COMMISSION_EXCEEDED)
        }
    }

    fun calculate(amount: BigDecimal): BigDecimal = amount.multiply(rate)
}
