package com.slimczes.payments.messaging.out.event

import java.util.UUID

data class PaidEvent (
    val orderId: UUID
): PublishEvent
