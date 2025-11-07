package com.slimczes.payments.service.dto

import java.util.UUID

data class CancelPaymentDto(
    val clientId: UUID,
    val orderId: UUID
)
