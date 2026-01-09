package com.flowcommerce.core.storage.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    public boolean existsByEmail(String email);
}
