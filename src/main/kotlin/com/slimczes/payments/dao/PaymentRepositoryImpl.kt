package com.slimczes.payments.dao

import com.slimczes.payments.domain.Money
import com.slimczes.payments.domain.Payment
import com.slimczes.payments.domain.PaymentStatus
import com.slimczes.payments.domain.client.Client
import com.slimczes.payments.domain.client.Wallet
import com.slimczes.payments.jooq.generated.Tables.CLIENT
import com.slimczes.payments.jooq.generated.Tables.PAYMENT
import com.slimczes.payments.jooq.generated.Tables.WALLET
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.text.get

@Repository
internal class PaymentRepositoryImpl(private val dsl: DSLContext): PaymentRepository {

    override fun save(payment: Payment): Payment {
        dsl.insertInto(PAYMENT)
            .set(PAYMENT.ID, payment.id)
            .set(PAYMENT.ORDER_ID, payment.orderId)
            .set(PAYMENT.CLIENT_ID, payment.getClient().id)
            .set(PAYMENT.STATUS, payment.getStatus().name)
            .execute()
        return payment
    }

    override fun findByOrderId(orderId: UUID): Payment? {
        val record = dsl.select(
            PAYMENT.ID,
            PAYMENT.ORDER_ID,
            PAYMENT.STATUS,
            CLIENT.ID,
            WALLET.ID,
            WALLET.AMOUNT,
            WALLET.CURRENCY
        ).from(PAYMENT)
            .join(CLIENT).on(PAYMENT.CLIENT_ID.eq(CLIENT.ID))
            .join(WALLET).on(CLIENT.WALLET_ID.eq(WALLET.ID))
            .where(PAYMENT.ORDER_ID.eq(orderId))
            .fetchOne() ?: return null

        val wallet = Wallet.reconstitute(record.get(WALLET.ID), Money(
            record.get(WALLET.AMOUNT),
            record.get(WALLET.CURRENCY)
        ))
        val client = Client.reconstitute(record.get(CLIENT.ID), wallet)
        return Payment.reconstitute(
            id = record.get(PAYMENT.ID),
            orderId = record.get(PAYMENT.ORDER_ID),
            amount = Money(
                record.get(WALLET.AMOUNT),
                record.get(WALLET.CURRENCY)
            ),
            client = client,
            status = PaymentStatus.valueOf(record.get(PAYMENT.STATUS))
        )
    }

    override fun update(payment: Payment): Payment {
        dsl.update(PAYMENT)
            .set(PAYMENT.STATUS, payment.getStatus().name)
            .where(PAYMENT.ID.eq(payment.id))
            .execute()
        return payment
    }

    override fun deleteById(id: UUID): Boolean {
        val deletedRows = dsl.deleteFrom(PAYMENT)
            .where(PAYMENT.ID.eq(id))
            .execute()
        return deletedRows > 0
    }
}