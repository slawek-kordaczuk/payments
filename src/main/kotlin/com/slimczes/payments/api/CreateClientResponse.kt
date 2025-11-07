package com.slimczes.payments.api

import com.slimczes.payments.domain.client.Wallet
import java.util.*

data class CreateClientResponse(
    val clientId: UUID,
    val wallet: Wallet
)
