package com.slimczes.payments.service.payment

import com.slimczes.payments.dao.ClientRepository
import com.slimczes.payments.dao.PaymentRepository
import com.slimczes.payments.domain.Payment
import com.slimczes.payments.messaging.out.PaymentPublisher
import com.slimczes.payments.messaging.out.event.PaidEvent
import com.slimczes.payments.messaging.out.event.PaidFailedEvent
import com.slimczes.payments.service.dto.CancelPaymentDto
import com.slimczes.payments.service.dto.CreatePaymentDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
internal class PaymentServiceImpl(
    private val clientRepository: ClientRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentPublisher: PaymentPublisher
) : PaymentService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun createPayment(createPaymentDto: CreatePaymentDto) {
        clientRepository.findById(createPaymentDto.clientId)?.let { client ->
            val payment = Payment.create(client, createPaymentDto.orderId, createPaymentDto.amount)
            payment.processPayment()
                .onSuccess {
                    paymentRepository.save(payment)
                    clientRepository.update(client)
                    paymentPublisher.publishPaidEvent(PaidEvent(createPaymentDto.orderId))
                    log.info("Payment processed successfully for orderId=${createPaymentDto.orderId}")
                }
                .onFailure {
                    paymentRepository.save(payment)
                    paymentPublisher.publishPaidFailedEvent(PaidFailedEvent(createPaymentDto.orderId, it.message ?: ""))
                    log.warn("Payment processed failed for orderId=${createPaymentDto.orderId}")
                }
        } ?: run {
            paymentPublisher.publishPaidFailedEvent(PaidFailedEvent(createPaymentDto.orderId, "Client not found"))
            log.info("Payment processed failed for orderId=${createPaymentDto.orderId} - client not found")
        }

    }

    override fun cancelPayment(cancelPaymentDto: CancelPaymentDto) {
        paymentRepository.findByOrderId(cancelPaymentDto.orderId)?.let { payment ->
            payment.cancelPayment()
                .onSuccess {
                    paymentRepository.update(payment)
                }
                .onFailure {
                    log.warn("Failed to cancel payment for orderId=${cancelPaymentDto.orderId}")
                }
        } ?: run {
            log.info("Payment not found for cancellation for orderId=${cancelPaymentDto.orderId}")
        }
    }

}
