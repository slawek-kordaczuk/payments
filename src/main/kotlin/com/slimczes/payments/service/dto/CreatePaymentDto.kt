package com.slimczes.payments.service.dto

import com.slimczes.payments.domain.Money
import java.util.UUID

data class CreatePaymentDto(
    val clientId: UUID,
    val orderId: UUID,
    val amount: Money
)
