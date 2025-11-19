package com.slimczes.payments.messaging.`in`.event

import java.util.UUID

data class PaymentCancelEvent(
    val clientId: UUID,
    val orderId: UUID
)