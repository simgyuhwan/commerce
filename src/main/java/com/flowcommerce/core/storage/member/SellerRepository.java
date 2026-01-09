package com.flowcommerce.core.storage.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<SellerEntity, Long> {
    boolean existsBySellerName(String sellerName);
    boolean existsByBusinessNumber(String businessNumber);
    SellerEntity findByMemberId(Long memberId);
}
