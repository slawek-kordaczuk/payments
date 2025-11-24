package com.slimczes.payments.messaging.`in`

import com.slimczes.payments.messaging.`in`.event.PaymentCancelEvent
import com.slimczes.payments.messaging.`in`.event.PaymentEvent
import com.slimczes.payments.messaging.mapper.PaymentEventMapper
import com.slimczes.payments.service.payment.CancelPayment
import com.slimczes.payments.service.payment.CreatePayment
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
internal class PaymentListener(
    private val createPayment: CreatePayment,
    private val cancelPayment: CancelPayment,
    private val paymentEventMapper: PaymentEventMapper,
) {

    @KafkaListener(
        topics = ["\${kafka.topics.payment}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "createPaymentKafkaListenerContainerFactory"
    )
    suspend fun listenCreatePaymentEvents(event: PaymentEvent) {
        val createPaymentDto = paymentEventMapper.toCreatePaymentDto(event)
        createPayment.createPayment(createPaymentDto)
    }

    @KafkaListener(
        topics = ["\${kafka.topics.payment-cancelled}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "cancelPaymentKafkaListenerContainerFactory"
    )
    suspend fun listenCancelPaymentEvents(event: PaymentCancelEvent) {
        val cancelPaymentDto = paymentEventMapper.toCancelPaymentDto(event)
        cancelPayment.cancelPayment(cancelPaymentDto)
    }

}