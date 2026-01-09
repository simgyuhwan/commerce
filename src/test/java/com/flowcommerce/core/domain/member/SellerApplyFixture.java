package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.TestFixtureConfig;
import com.navercorp.fixturemonkey.FixtureMonkey;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class SellerApplyFixture {
    private static final FixtureMonkey fixtureMonkey = TestFixtureConfig.fixtureMonkey;

    public static SellerApply createDefault() {
        return fixtureMonkey.giveMeOne(SellerApply.class);
    }

    /**
     * 유효한 SellerApply 생성 (검증 통과 가능한 데이터)
     */
    public static SellerApply createValid() {
        return fixtureMonkey.giveMeBuilder(SellerApply.class)
                .set("sellerName", "테스트판매자" + System.currentTimeMillis())
                .set("businessNumber", generateBusinessNumber())
                .set("onlineSalesNumber", generateOnlineSalesNumber())
                .set("companyPhone", "02-1234-5678")
                .set("contactPhone", "010-1234-5678")
                .set("contactEmail", "test@example.com")
                .set("commissionRate", BigDecimal.valueOf(10.0))
                .sample();
    }

    /**
     * 특정 판매자명의 SellerApply
     */
    public static SellerApply createWithSellerName(String sellerName) {
        return fixtureMonkey.giveMeBuilder(SellerApply.class)
                .set("sellerName", sellerName)
                .sample();
    }

    /**
     * 특정 사업자번호의 SellerApply
     */
    public static SellerApply createWithBusinessNumber(String businessNumber) {
        return fixtureMonkey.giveMeBuilder(SellerApply.class)
                .set("businessNumber", businessNumber)
                .sample();
    }

    /**
     * 특정 계좌 정보의 SellerApply
     */
    public static SellerApply createWithAccount(String accountHolder, String bankName, String accountNumber) {
        return fixtureMonkey.giveMeBuilder(SellerApply.class)
                .set("accountHolder", accountHolder)
                .set("bankName", bankName)
                .set("accountNumber", accountNumber)
                .sample();
    }

    private static String generateBusinessNumber() {
        return String.format("%03d-%02d-%05d",
                ThreadLocalRandom.current().nextInt(100, 999),
                ThreadLocalRandom.current().nextInt(10, 99),
                ThreadLocalRandom.current().nextInt(10000, 99999));
    }

    private static String generateOnlineSalesNumber() {
        return String.format("2024-%05d",
                ThreadLocalRandom.current().nextInt(10000, 99999));
    }
}
