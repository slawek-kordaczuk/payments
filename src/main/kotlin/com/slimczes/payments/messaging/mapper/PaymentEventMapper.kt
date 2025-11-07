package com.slimczes.payments.messaging.mapper

import com.slimczes.payments.messaging.`in`.event.PaymentCancelEvent
import com.slimczes.payments.messaging.`in`.event.PaymentEvent
import com.slimczes.payments.service.dto.CancelPaymentDto
import com.slimczes.payments.service.dto.CreatePaymentDto
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface PaymentEventMapper {

    fun toCreatePaymentDto(paymentEvent: PaymentEvent): CreatePaymentDto
    fun toCancelPaymentDto(paymentEvent: PaymentCancelEvent): CancelPaymentDto
}