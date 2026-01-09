package com.flowcommerce.core.storage.member;

import com.flowcommerce.core.domain.member.SellerStatus;
import com.flowcommerce.core.domain.member.SettlementBasis;
import com.flowcommerce.core.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Table(name = "seller")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SellerEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String sellerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerStatus sellerStatus;

    private String companyName;
    @Column(unique = true, nullable = false)
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

    @Enumerated(EnumType.STRING)
    private SettlementBasis settlementBasis;

    private Long approvedBy;
    private LocalDateTime approvedAt;
}
