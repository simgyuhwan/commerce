package com.flowcommerce.core.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerApply {
    private Long memberId;
    private String sellerName;
    private SellerStatus status;

    private String companyName;
    private String businessNumber;
    private String onlineSalesNumber;
    private String businessType;
    private String companyPhone;
    private String address;

    private String contactName;
    private String contactPhone;
    private String contactEmail;

    private String accountHolder;
    private String bankName;
    private String accountNumber;

    private BigDecimal commissionRate;

    private SettlementBasis settlementBasis;
}
