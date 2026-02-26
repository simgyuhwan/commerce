package com.livecommerce.common.messaging

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun domainEventExchange(): TopicExchange =
        TopicExchange("livecommerce.events", true, false)

    // Queues
    @Bean
    fun orderCreatedCommerceQueue() = Queue(RabbitConstants.Queues.ORDER_CREATED_COMMERCE, true)

    @Bean
    fun orderCreatedPaymentQueue() = Queue(RabbitConstants.Queues.ORDER_CREATED_PAYMENT, true)

    @Bean
    fun paymentCompletedOrderQueue() = Queue(RabbitConstants.Queues.PAYMENT_COMPLETED_ORDER, true)

    @Bean
    fun paymentCompletedSettlementQueue() = Queue(RabbitConstants.Queues.PAYMENT_COMPLETED_SETTLEMENT, true)

    @Bean
    fun paymentFailedOrderQueue() = Queue(RabbitConstants.Queues.PAYMENT_FAILED_ORDER, true)

    @Bean
    fun paymentFailedCommerceQueue() = Queue(RabbitConstants.Queues.PAYMENT_FAILED_COMMERCE, true)

    @Bean
    fun orderCancelledCommerceQueue() = Queue(RabbitConstants.Queues.ORDER_CANCELLED_COMMERCE, true)

    @Bean
    fun orderCancelledPaymentQueue() = Queue(RabbitConstants.Queues.ORDER_CANCELLED_PAYMENT, true)

    @Bean
    fun broadcastEndedSettlementQueue() = Queue(RabbitConstants.Queues.BROADCAST_ENDED_SETTLEMENT, true)

    // ── Bindings ───────────────────────────────────────────
    @Bean
    fun bindOrderCreatedCommerce() =
        bind(orderCreatedCommerceQueue(), RabbitConstants.RoutingKeys.ORDER_CREATED)

    @Bean
    fun bindOrderCreatedPayment() =
        bind(orderCreatedPaymentQueue(), RabbitConstants.RoutingKeys.ORDER_CREATED)

    @Bean
    fun bindPaymentCompletedOrder() =
        bind(paymentCompletedOrderQueue(), RabbitConstants.RoutingKeys.PAYMENT_COMPLETED)

    @Bean
    fun bindPaymentCompletedSettlement() =
        bind(paymentCompletedSettlementQueue(), RabbitConstants.RoutingKeys.PAYMENT_COMPLETED)

    @Bean
    fun bindPaymentFailedOrder() =
        bind(paymentFailedOrderQueue(), RabbitConstants.RoutingKeys.PAYMENT_FAILED)

    @Bean
    fun bindPaymentFailedCommerce() =
        bind(paymentFailedCommerceQueue(), RabbitConstants.RoutingKeys.PAYMENT_FAILED)

    @Bean
    fun bindOrderCancelledCommerce() =
        bind(orderCancelledCommerceQueue(), RabbitConstants.RoutingKeys.ORDER_CANCELLED)

    @Bean
    fun bindOrderCancelledPayment() =
        bind(orderCancelledPaymentQueue(), RabbitConstants.RoutingKeys.ORDER_CANCELLED)

    @Bean
    fun bindBroadcastEndedSettlement() =
        bind(broadcastEndedSettlementQueue(), RabbitConstants.RoutingKeys.BROADCAST_ENDED)

    // ── 공통 ───────────────────────────────────────────────
    private fun bind(queue: Queue, routingKey: String): Binding =
        BindingBuilder.bind(queue)
            .to(domainEventExchange())
            .with(routingKey)

    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter =
        Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: Jackson2JsonMessageConverter
    ): RabbitTemplate =
        RabbitTemplate(connectionFactory)
            .apply { this.messageConverter = messageConverter }
}