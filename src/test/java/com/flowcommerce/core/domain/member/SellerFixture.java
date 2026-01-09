package com.flowcommerce.core.domain.member;

import java.math.BigDecimal;

public class SellerFixture {

    public static SellerApply createSellerApply() {
        return createSellerApply("테스트셀러");
    }

    public static SellerApply createSellerApply(String sellerName) {
        return new SellerApply(
                1L,  // memberId
                sellerName,
                SellerStatus.PENDING,
                // 회사 정보
                "테스트컴퍼니",
                "123-45-67890",
                "2023-서울-1234",
                "도소매업",
                "02-1234-5678",
                "서울시 강남구 테스트로 123",
                // 연락처 정보
                "홍길동",
                "010-1234-5678",
                "contact@test.com",
                // 정산 정보
                "홍길동",
                "신한은행",
                "110-123-456789",
                // 수수료
                new BigDecimal("0.15"),
                // 정산 기준
                SettlementBasis.PURCHASE_CONFIRMED
        );
    }

    public static SellerApply createSellerApplyWithBusinessNumber(String businessNumber) {
        return new SellerApply(
                1L,
                "테스트셀러",
                SellerStatus.PENDING,
                "테스트컴퍼니",
                businessNumber,
                "2023-서울-1234",
                "도소매업",
                "02-1234-5678",
                "서울시 강남구 테스트로 123",
                "홍길동",
                "010-1234-5678",
                "contact@test.com",
                "홍길동",
                "신한은행",
                "110-123-456789",
                new BigDecimal("0.15"),
                SettlementBasis.PURCHASE_CONFIRMED
        );
    }

    public static SellerApply createSellerApply(String businessNumber, String sellerName) {
        return new SellerApply(
                1L,
                sellerName,
                SellerStatus.PENDING,
                "테스트컴퍼니",
                businessNumber,
                "2023-서울-1234",
                "도소매업",
                "02-1234-5678",
                "서울시 강남구 테스트로 123",
                "홍길동",
                "010-1234-5678",
                "contact@test.com",
                "홍길동",
                "신한은행",
                "110-123-456789",
                new BigDecimal("0.15"),
                SettlementBasis.PURCHASE_CONFIRMED
        );
    }
}
