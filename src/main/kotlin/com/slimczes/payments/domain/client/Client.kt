package com.slimczes.payments.domain.client

import com.slimczes.payments.domain.Money
import java.util.UUID

class Client private constructor(
    val id: UUID,
    private var wallet: Wallet
) {
    companion object {
        fun create(wallet: Wallet): Client {
            return Client(UUID.randomUUID(), wallet)
        }
        fun reconstitute(id: UUID, wallet: Wallet): Client {
            return Client(id, wallet)
        }
    }

    fun deduct(amount: Money): Result<Unit> {
        return wallet.deduct(amount)
    }

    fun cancelDeduction(amount: Money): Result<Unit> {
        return wallet.add(amount)
    }

    fun getWallet(): Wallet = wallet
}