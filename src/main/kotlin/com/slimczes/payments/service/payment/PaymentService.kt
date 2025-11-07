package com.slimczes.payments.service.payment

import com.slimczes.payments.service.dto.CancelPaymentDto
import com.slimczes.payments.service.dto.CreatePaymentDto

interface PaymentService {

    fun createPayment(createPaymentDto: CreatePaymentDto)
    fun cancelPayment(cancelPaymentDto: CancelPaymentDto)

}