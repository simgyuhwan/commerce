package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;

import java.math.BigDecimal;

public record Commission(BigDecimal rate) {
    public Commission{
        if (rate == null) {
            throw new CoreException(ErrorType.COMMISSION_REQUIRED);
        }
        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.COMMISSION_NEGATIVE);
        }
        if (rate.compareTo(BigDecimal.ONE) > 0) {
            throw new CoreException(ErrorType.COMMISSION_EXCEEDED);
        }
    }

    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(rate);
    }
}
