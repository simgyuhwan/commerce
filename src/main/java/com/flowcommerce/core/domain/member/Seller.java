package com.flowcommerce.core.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Seller {
    private Long id;
    private Long memberId;
    private String sellerName;
    private SellerStatus status;

    private CompanyInfo companyInfo;
    private ContactInfo contactInfo;
    private PaymentInfo paymentInfo;
    private Commission commission;

    private Long approvedBy;
    private LocalDateTime approvedAt;
}
