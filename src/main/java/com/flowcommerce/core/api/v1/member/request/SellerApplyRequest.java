package com.flowcommerce.core.api.v1.member.request;

import com.flowcommerce.core.domain.member.SellerApply;
import com.flowcommerce.core.domain.member.SellerStatus;
import com.flowcommerce.core.domain.member.SettlementBasis;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class SellerApplyRequest {
    private Long memberId;
    @NotBlank
    private String sellerName;

    private SellerStatus status;

    @NotBlank
    private String companyName;
    @NotBlank
    private String businessNumber;
    @NotBlank
    private String onlineSalesNumber;
    @NotBlank
    private String businessType;
    @NotBlank
    private String companyPhone;
    @NotBlank
    private String address;

    @NotBlank
    private String contactName;
    @NotBlank
    private String contactPhone;

    @Email
    @NotBlank
    private String contactEmail;

    @NotBlank
    private String accountHolder;
    @NotBlank
    private String bankName;
    @NotBlank
    private String accountNumber;

    @NotNull
    @DecimalMin( "0.0")
    @DecimalMax( "100.0")
    private BigDecimal commissionRate;

    @NotNull
    private SettlementBasis settlementBasis;

    public SellerApply toSellerApply() {
        return new SellerApply(
               memberId,
               sellerName,
               status,
               companyName,
               businessNumber,
               onlineSalesNumber,
               businessType,
               companyPhone,
               address,
               contactName,
               contactPhone,
               contactEmail,
               accountHolder,
               bankName,
               accountNumber,
               commissionRate,
               settlementBasis
        );
    }
}
