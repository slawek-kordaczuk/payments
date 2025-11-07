package com.slimczes.payments.domain.client

import com.slimczes.payments.domain.Money
import java.math.BigDecimal
import java.util.UUID

class Wallet private constructor(
    val id: UUID,
    private var balance: Money
) {
    companion object {
        fun create(initialBalance: Money): Wallet {
            require(initialBalance.amount >= BigDecimal.ZERO) { "Balance cannot be negative" }
            return Wallet(UUID.randomUUID(), initialBalance)
        }
        fun reconstitute(id: UUID, balance: Money): Wallet {
            return Wallet(id, balance)
        }
    }

    fun deduct(amount: Money): Result<Unit> {
        require(amount.currency == balance.currency) { "Currency mismatch" }

        return if (balance.amount >= amount.amount) {
            balance = Money(balance.amount - amount.amount, balance.currency)
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Insufficient funds"))
        }
    }

    fun add(amount: Money): Result<Unit> {
        require(amount.currency == balance.currency) { "Currency mismatch" }

        balance = Money(balance.amount + amount.amount, balance.currency)
        return Result.success(Unit)
    }

    fun getBalance(): Money = balance

}