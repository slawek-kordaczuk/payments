package com.slimczes.payments.dao

import com.slimczes.payments.domain.Money
import com.slimczes.payments.domain.client.Client
import com.slimczes.payments.domain.client.Wallet
import com.slimczes.payments.jooq.generated.Tables.CLIENT
import com.slimczes.payments.jooq.generated.Tables.WALLET
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ClientRepository(private val dsl: DSLContext) {
    fun findById(id: UUID): Client? {
        val record = dsl.select(
            CLIENT.ID,
            CLIENT.WALLET_ID,
            WALLET.AMOUNT,
            WALLET.CURRENCY
        )
            .from(CLIENT)
            .join(WALLET)
            .on(WALLET.ID.eq(CLIENT.WALLET_ID))
            .where(CLIENT.ID.eq(id)).fetchOne()
            ?: return null

        val wallet = Wallet.reconstitute(
            record.get(CLIENT.WALLET_ID), Money(
                record.get(WALLET.AMOUNT),
                record.get(WALLET.CURRENCY)
            )
        )

        return Client.reconstitute(record.get(CLIENT.ID), wallet)
    }

    fun save(client: Client): Client {
        dsl.insertInto(WALLET)
            .set(WALLET.ID, client.getWallet().id)
            .set(WALLET.AMOUNT, client.getWallet().getBalance().amount)
            .set(WALLET.CURRENCY, client.getWallet().getBalance().currency)
            .execute()

        dsl.insertInto(CLIENT)
            .set(CLIENT.ID, client.id)
            .set(CLIENT.WALLET_ID, client.getWallet().id)
            .execute()
        return client
    }

    fun update(client: Client): Client {
        dsl.update(WALLET)
            .set(WALLET.AMOUNT, client.getWallet().getBalance().amount)
            .set(WALLET.CURRENCY, client.getWallet().getBalance().currency)
            .where(WALLET.ID.eq(client.getWallet().id))
            .execute()
        return client
    }

    fun delete(client: Client) {
        dsl.deleteFrom(CLIENT)
            .where(CLIENT.ID.eq(client.id))
            .execute()
    }

//    fun findById(id: UUID): Client? = null // TODO("Generate jOOQ first")
//    fun save(client: Client): Client = client // TODO
//    fun update(client: Client): Client = client // TODO
//    fun delete(client: Client) {} // TODO
}