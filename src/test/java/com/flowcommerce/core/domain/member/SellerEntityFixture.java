package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.storage.member.SellerEntity;

import static com.flowcommerce.core.TestFixtureConfig.fixtureMonkey;

public class SellerEntityFixture {
    public static SellerEntity createDefault() {
        return fixtureMonkey.giveMeOne(SellerEntity.class);
    }

    public static SellerEntity createWithStatus(SellerStatus status) {
        return fixtureMonkey.giveMeBuilder(SellerEntity.class)
                .set("sellerStatus", status)
                .sample();
    }

    public static SellerEntity create(Long id, SellerStatus status) {
        return fixtureMonkey.giveMeBuilder(SellerEntity.class)
                .set("id", id)
                .set("sellerStatus", status)
                .sample();
    }

    public static SellerEntity createWithMember(Long memberId, SellerStatus status) {
        return fixtureMonkey.giveMeBuilder(SellerEntity.class)
                .set("memberId", memberId)
                .set("sellerStatus", status)
                .sample();
    }

    public static SellerEntity createPending() {
        return createWithStatus(SellerStatus.PENDING);
    }

    public static SellerEntity createApproved() {
        return createWithStatus(SellerStatus.APPROVED);
    }

    public static SellerEntity createRejected() {
        return createWithStatus(SellerStatus.REJECTED);
    }
}