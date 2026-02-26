package com.livecommerce.common.messaging

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxPublisher(
    private val outboxRepository: OutboxRepository,
    private val rabbitTemplate: RabbitTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1000)
    @Transactional
    fun publish() {
        val events = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
        events.forEach { event ->
            runCatching {
                rabbitTemplate.convertAndSend(
                    "livecommerce.events",
                    event.eventType,
                    event.payload
                )
                event.markPublished()
                outboxRepository.save(event)
                log.info("Outbox 발행 완료 [id=${event.id}, eventType=${event.eventType}]")
            }.onFailure { e ->
                log.error("Outbox 발행 실패 [id=${event.id}, eventType=${event.eventType}]", e)
            }
        }
    }
}