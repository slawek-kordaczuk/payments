package com.slimczes.payments.domain

import com.slimczes.payments.domain.client.Client
import java.math.BigDecimal
import java.util.*

class Payment private constructor(
    val id: UUID,
    val orderId: UUID,
    val amount: Money,
    private var client: Client,
    private var status: PaymentStatus = PaymentStatus.NEW
) {
    companion object {
        fun create(client: Client, orderId: UUID, amount: Money): Payment {
            return Payment(UUID.randomUUID(), orderId, amount, client)
        }
        fun reconstitute(id: UUID, orderId: UUID, amount: Money, client: Client, status: PaymentStatus): Payment {
            return Payment(id, orderId, amount, client, status)
        }
    }

    fun processPayment(): Result<Unit> {
        require(amount.amount >= BigDecimal.ZERO) { "Balance cannot be negative" }
        return client.deduct(amount)
            .onSuccess { status = PaymentStatus.SUCCESS }
            .onFailure { status = PaymentStatus.FAILED }
    }

    fun cancelPayment(): Result<Unit> {
        return if (status == PaymentStatus.SUCCESS) {
            return client.cancelDeduction(amount)
                .onSuccess { status = PaymentStatus.CANCELLED }
                .onFailure { "Failed to cancel payment: ${it.message}" }
        } else {
            Result.failure(IllegalStateException("Only successful payments can be cancelled"))
        }
    }

    fun getClient(): Client = client
    fun getStatus(): PaymentStatus = status
}

enum class PaymentStatus {
    NEW, SUCCESS, CANCELLED, FAILED
}