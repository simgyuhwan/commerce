package com.livecommerce.common.messaging

object RabbitConstants {
    const val EXCHANGE = "livecommerce.events"

    object Queues {
        const val ORDER_CREATED_COMMERCE      = "order.created.commerce"
        const val ORDER_CREATED_PAYMENT       = "order.created.payment"
        const val PAYMENT_COMPLETED_ORDER     = "payment.completed.order"
        const val PAYMENT_COMPLETED_SETTLEMENT= "payment.completed.settlement"
        const val PAYMENT_FAILED_ORDER        = "payment.failed.order"
        const val PAYMENT_FAILED_COMMERCE     = "payment.failed.commerce"
        const val ORDER_CANCELLED_COMMERCE    = "order.cancelled.commerce"
        const val ORDER_CANCELLED_PAYMENT     = "order.cancelled.payment"
        const val BROADCAST_ENDED_SETTLEMENT  = "broadcast.ended.settlement"
    }

    object RoutingKeys {
        const val ORDER_CREATED     = "order.created"
        const val PAYMENT_COMPLETED = "payment.completed"
        const val PAYMENT_FAILED    = "payment.failed"
        const val ORDER_CANCELLED   = "order.cancelled"
        const val BROADCAST_ENDED   = "broadcast.ended"
    }
}