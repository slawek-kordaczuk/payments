package com.slimczes.payments.service.payment

import com.slimczes.payments.dao.PaymentRepository
import com.slimczes.payments.service.dto.CancelPaymentDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CancelPayment (
    private val paymentRepository: PaymentRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun cancelPayment(cancelPaymentDto: CancelPaymentDto) {
        paymentRepository.findByOrderId(cancelPaymentDto.orderId)?.let { payment ->
            payment.cancelPayment()
                .onSuccess {
                    paymentRepository.update(payment)
                    log.info("Cancel payment processed successfully for orderId=${cancelPaymentDto.orderId}")
                }
                .onFailure {
                    log.warn("Failed to cancel payment for orderId=${cancelPaymentDto.orderId}")
                }
        } ?: run {
            log.info("Payment not found for cancellation for orderId=${cancelPaymentDto.orderId}")
        }
    }
}