package com.slimczes.payments.messaging.out.event

import java.util.UUID

data class PaidFailedEvent(
    val orderId: UUID,
    val reason: String
): PublishEvent
