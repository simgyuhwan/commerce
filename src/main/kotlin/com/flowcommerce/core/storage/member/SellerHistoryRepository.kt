package com.flowcommerce.core.storage.member

import org.springframework.data.jpa.repository.JpaRepository

interface SellerHistoryRepository : JpaRepository<SellerHistoryEntity, Long>
