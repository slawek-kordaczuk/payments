package com.slimczes.payments.service.payment

import com.slimczes.payments.dao.ClientRepository
import com.slimczes.payments.dao.PaymentRepository
import com.slimczes.payments.domain.Payment
import com.slimczes.payments.messaging.out.PaymentPublisher
import com.slimczes.payments.messaging.out.event.PaidEvent
import com.slimczes.payments.messaging.out.event.PaidFailedEvent
import com.slimczes.payments.service.dto.CreatePaymentDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePayment(
    private val clientRepository: ClientRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentPublisher: PaymentPublisher
) : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    suspend fun createPayment(createPaymentDto: CreatePaymentDto) {

        clientRepository.findById(createPaymentDto.clientId)?.let { client ->
            val payment = Payment.create(client, createPaymentDto.orderId, createPaymentDto.amount)
            payment.processPayment()
                .onSuccess {
                    paymentRepository.save(payment)
                    clientRepository.update(client)
                    launch {
                        paymentPublisher.publishPaidEvent(PaidEvent(createPaymentDto.orderId))
                    }

                    log.info("Payment processed successfully for orderId=${createPaymentDto.orderId}")
                }
                .onFailure {
                    paymentRepository.save(payment)
                    launch {
                        paymentPublisher.publishPaidFailedEvent(
                            PaidFailedEvent(
                                createPaymentDto.orderId,
                                it.message ?: ""
                            )
                        )
                    }
                    log.warn("Payment processed failed for orderId=${createPaymentDto.orderId}")
                }
        } ?: run {
            launch {
                paymentPublisher.publishPaidFailedEvent(PaidFailedEvent(createPaymentDto.orderId, "Client not found"))
            }
            log.info("Payment processed failed for orderId=${createPaymentDto.orderId} - client not found")
        }

    }

}
