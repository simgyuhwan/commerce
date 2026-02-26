package com.livecommerce.common.messaging

import org.springframework.data.jpa.repository.JpaRepository

interface OutboxRepository : JpaRepository<Outbox, Long> {
    fun findTop100ByStatusOrderByCreatedAtAsc(status: OutboxStatus): List<Outbox>
}