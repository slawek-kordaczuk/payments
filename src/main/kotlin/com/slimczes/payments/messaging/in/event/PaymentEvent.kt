package com.slimczes.payments.messaging.`in`.event

import com.slimczes.payments.domain.Money
import java.util.UUID

data class PaymentEvent(
    val clientId: UUID,
    val orderId: UUID,
    val amount: Money
)
